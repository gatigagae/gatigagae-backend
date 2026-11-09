package com.gatigagae.gatigagae_api.domain.place.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Service;

import com.gatigagae.gatigagae_api.domain.place.dto.request.PlaceSearchRequest;
import com.gatigagae.gatigagae_api.domain.place.dto.response.PlaceSearchListResponse;
import com.gatigagae.gatigagae_api.domain.place.entity.enums.PlaceCategory;
import com.gatigagae.gatigagae_api.global.apipayload.exception.GeneralException;
import com.gatigagae.gatigagae_api.global.client.tour.TourClient;
import com.gatigagae.gatigagae_api.global.client.tour.dto.TourDetailResponse;
import com.gatigagae.gatigagae_api.global.client.tour.dto.TourPlaceResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceService {

	public static final int DEFAULT_RADIUS = 5_000;
	private static final int PAGE_SIZE = 3;
	private static final int CANDIDATE_COUNT = 100;

	private final TourClient tourClient;

	public PlaceSearchListResponse searchPlaces(PlaceSearchRequest request) {
		//공백과 문장부호를 지우고 비교
		String text = request.text().replaceAll("[\\s\\p{Z}\\p{Punct}]", "");
		Optional<PlaceCategory> found = PlaceCategory.from(text);
		if (found.isEmpty()) {
			log.info("장소 종류를 찾지 못함. text={}", request.text());
			return PlaceSearchListResponse.notUnderstood();
		}

		PlaceCategory category = found.get();
		int radius = Objects.requireNonNullElse(request.radius(), DEFAULT_RADIUS);
		int page = Objects.requireNonNullElse(request.page(), 0);

		List<TourPlaceResponse> nearby = tourClient.findNearbyPlace(
			request.latitude(),
			request.longitude(),
			radius,
			category.getLclsSystm1(),
			category.getLclsSystm2(),
			CANDIDATE_COUNT);
		List<TourPlaceResponse> candidates = nearby.stream()
			.filter(place -> category.includes(place.lclsSystm2()))
			.toList();

		int from = (int)Math.min((long)page * PAGE_SIZE, candidates.size());
		int to = Math.min(from + PAGE_SIZE, candidates.size());
		return new PlaceSearchListResponse(category, withConditions(candidates.subList(from, to)),
			to < candidates.size());
	}

	private List<PlaceSearchListResponse.PlaceSearchResponse> withConditions(List<TourPlaceResponse> places) {
		// 반려동물 동반 조건은 장소마다 따로 불러야 해서 동시에 호출
		try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
			List<CompletableFuture<PlaceSearchListResponse.PlaceSearchResponse>> futures = places.stream()
				.map(place -> CompletableFuture.supplyAsync(
					() -> PlaceSearchListResponse.PlaceSearchResponse.of(place, conditionsOf(place.contentId())),
					executor))
				.toList();
			return futures.stream()
				.map(CompletableFuture::join)
				.toList();
		}
	}

	private List<String> conditionsOf(String contentId) {
		try {
			return tourClient.findPlaceDetail(contentId)
				.map(TourDetailResponse::conditions)
				.orElse(List.of());
		} catch (GeneralException exception) {
			log.warn("반려동물 동반 정보 조회 실패. contentId={}", contentId, exception);
			return List.of();
		}
	}
}

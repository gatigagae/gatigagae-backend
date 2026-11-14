package com.gatigagae.gatigagae_api.domain.place.service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gatigagae.gatigagae_api.domain.place.dto.request.PlaceSearchRequest;
import com.gatigagae.gatigagae_api.domain.place.dto.response.PlaceSearchListResponse;
import com.gatigagae.gatigagae_api.domain.place.entity.Place;
import com.gatigagae.gatigagae_api.domain.place.entity.enums.PlaceCategory;
import com.gatigagae.gatigagae_api.domain.place.repository.PlaceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceService {

	public static final int DEFAULT_RADIUS = 5_000;
	private static final int PAGE_SIZE = 3;
	private static final double EARTH_RADIUS = 6_371_000;

	private final PlaceRepository placeRepository;

	// 하버사인 공식으로 구한 거리
	private static int distance(double latitude, double longitude, Place place) {
		double latitudeDiff = Math.toRadians(place.getLatitude() - latitude);
		double longitudeDiff = Math.toRadians(place.getLongitude() - longitude);
		double h = Math.pow(Math.sin(latitudeDiff / 2), 2)
			+ Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(place.getLatitude()))
			* Math.pow(Math.sin(longitudeDiff / 2), 2);
		return (int)Math.round(2 * EARTH_RADIUS * Math.asin(Math.sqrt(h)));
	}

	@Transactional(readOnly = true)
	public PlaceSearchListResponse searchPlaces(PlaceSearchRequest request) {
		String text = request.text().replaceAll("[\\s\\p{Z}\\p{Punct}]", "");
		Optional<PlaceCategory> found = PlaceCategory.from(text);
		if (found.isEmpty()) {
			log.info("장소 종류를 찾지 못함. text={}", request.text());
			return PlaceSearchListResponse.notUnderstood();
		}

		PlaceCategory category = found.get();
		int radius = Objects.requireNonNullElse(request.radius(), DEFAULT_RADIUS);
		int page = Objects.requireNonNullElse(request.page(), 0);
		double latitude = request.latitude();
		double longitude = request.longitude();

		// 반경을 감싸는 위도·경도 범위로 먼저 추리고, 그 안에서 실제 거리를 잰다
		double latitudeRange = Math.toDegrees(radius / EARTH_RADIUS);
		double longitudeRange = latitudeRange / Math.cos(Math.toRadians(latitude));
		List<PlaceSearchListResponse.PlaceSearchResponse> candidates = placeRepository
			.findByCategoryInAndLatitudeBetweenAndLongitudeBetween(category.targets(),
				latitude - latitudeRange, latitude + latitudeRange, longitude - longitudeRange,
				longitude + longitudeRange)
			.stream()
			.map(place -> PlaceSearchListResponse.PlaceSearchResponse.of(place, distance(latitude, longitude, place)))
			.filter(place -> place.distance() <= radius)
			// 좌표가 같은 장소도 있을 수 있어서, 거리가 같으면 ID로 순서를 고정해 페이지끼리 겹치지 않게 한다
			.sorted(Comparator.comparingInt(PlaceSearchListResponse.PlaceSearchResponse::distance)
				.thenComparing(PlaceSearchListResponse.PlaceSearchResponse::placeId))
			.toList();

		int from = (int)Math.min((long)page * PAGE_SIZE, candidates.size());
		int to = Math.min(from + PAGE_SIZE, candidates.size());

		return new PlaceSearchListResponse(
			category,
			candidates.subList(from, to),
			to < candidates.size(),
			candidates.size()
		);
	}
}
package com.gatigagae.gatigagae_api.global.client.tour;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import com.gatigagae.gatigagae_api.global.apipayload.exception.GeneralException;
import com.gatigagae.gatigagae_api.global.client.tour.dto.TourDetailResponse;
import com.gatigagae.gatigagae_api.global.client.tour.dto.TourPlaceResponse;
import com.gatigagae.gatigagae_api.global.client.tour.exception.TourErrorCode;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
public class TourClient {

	private static final String BASE_URL = "https://apis.data.go.kr/B551011/KorPetTourService2";

	private final RestClient restClient;
	private final JsonMapper jsonMapper;
	private final String serviceKey;

	public TourClient(RestClient.Builder restClientBuilder, JsonMapper jsonMapper,
		@Value("${tour-api.service-key}") String serviceKey) {
		this.restClient = restClientBuilder.baseUrl(BASE_URL).build();
		this.jsonMapper = jsonMapper;
		this.serviceKey = serviceKey;
	}

	private static TourPlaceResponse toTourPlace(JsonNode item) {
		String tel = text(item, "tel").strip();
		return new TourPlaceResponse(
			text(item, "contentid"),
			text(item, "title"),
			(text(item, "addr1") + " " + text(item, "addr2")).strip(),
			tel.isEmpty() ? null : tel,
			Double.parseDouble(text(item, "mapy")),
			Double.parseDouble(text(item, "mapx")),
			(int)Math.round(Double.parseDouble(text(item, "dist"))),
			text(item, "lclsSystm2"));
	}

	private static String text(JsonNode node, String field) {
		return node.path(field).asString("");
	}

	public List<TourPlaceResponse> findNearbyPlace(double latitude, double longitude, int radius, String lclsSystm1,
		String lclsSystm2, int count) {
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("mapX", longitude);
		params.put("mapY", latitude);
		params.put("radius", radius);
		params.put("arrange", "E");
		params.put("numOfRows", count);
		params.put("pageNo", 1);
		if (lclsSystm1 != null) {
			params.put("lclsSystm1", lclsSystm1);
		}
		if (lclsSystm2 != null) {
			params.put("lclsSystm2", lclsSystm2);
		}

		return get("/locationBasedList2", params).stream()
			// 좌표는 선택 항목이라 비어 있는 장소는 건너뛴다
			.filter(item -> !text(item, "mapx").isEmpty() && !text(item, "mapy").isEmpty())
			.map(TourClient::toTourPlace)
			.toList();
	}

	public Optional<TourDetailResponse> findPlaceDetail(String contentId) {
		return get("/detailPetTour2", Map.of("contentId", contentId)).stream()
			.findFirst()
			.map(item -> new TourDetailResponse(
				text(item, "acmpyPsblCpam"),
				text(item, "acmpyTypeCd"),
				text(item, "acmpyNeedMtr")));
	}

	private List<JsonNode> get(String path, Map<String, Object> params) {
		Map<String, Object> query = new LinkedHashMap<>(params);
		query.put("serviceKey", serviceKey);
		query.put("MobileOS", "AND");
		query.put("MobileApp", "gatigagae");
		query.put("_type", "json");

		String body;
		try {
			body = restClient.get()
				.uri(uriBuilder -> {
					uriBuilder.path(path);
					// 값을 URI 변수로 넘겨야 서비스키의 +, /, = 까지 인코딩된다
					query.keySet().forEach(name -> uriBuilder.queryParam(name, "{" + name + "}"));
					return uriBuilder.build(query);
				})
				.retrieve()
				.body(String.class);
		} catch (RestClientResponseException exception) {
			log.warn("관광공사 API 오류 응답. path={}, status={}, body={}", path, exception.getStatusCode(),
				StringUtils.truncate(exception.getResponseBodyAsString(), 300));
			throw new GeneralException(TourErrorCode.TOUR_API_FAILED);
		} catch (RestClientException exception) {
			log.warn("관광공사 API 호출 실패. path={}, cause={}", path, exception.getMostSpecificCause().toString());
			throw new GeneralException(TourErrorCode.TOUR_API_FAILED);
		}
		return items(path, body == null ? "" : body);
	}

	private List<JsonNode> items(String path, String body) {
		JsonNode response;
		try {
			response = jsonMapper.readTree(body).path("response");
		} catch (JacksonException exception) {
			log.warn("관광공사 API가 JSON이 아닌 응답을 보냄. path={}, body={}", path, StringUtils.truncate(body, 300));
			throw new GeneralException(TourErrorCode.TOUR_API_FAILED);
		}

		if (!"0000".equals(response.path("header").path("resultCode").asString(""))) {
			log.warn("관광공사 API 오류. path={}, body={}", path, StringUtils.truncate(body, 300));
			throw new GeneralException(TourErrorCode.TOUR_API_FAILED);
		}

		JsonNode item = response.path("body").path("items").path("item");
		List<JsonNode> items = new ArrayList<>();
		if (item.isArray()) {
			item.forEach(items::add);
		}
		return items;
	}
}
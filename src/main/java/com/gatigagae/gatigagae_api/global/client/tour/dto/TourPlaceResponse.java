package com.gatigagae.gatigagae_api.global.client.tour.dto;

public record TourPlaceResponse(
	String contentId,
	String name,
	String address,
	String tel,
	double latitude,
	double longitude,
	int distance,
	String lclsSystm2
) {
}

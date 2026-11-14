package com.gatigagae.gatigagae_api.domain.place.entity;

import com.gatigagae.gatigagae_api.domain.place.entity.enums.PlaceCategory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "places")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "place_id")
	private Long id;

	@Column(name = "name", nullable = false, length = 50)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(name = "category", nullable = false, length = 20)
	private PlaceCategory category;

	@Column(name = "latitude", nullable = false)
	private Double latitude;

	@Column(name = "longitude", nullable = false)
	private Double longitude;

	@Column(name = "address", nullable = false, length = 100)
	private String address;

	@Column(name = "tel", length = 20)
	private String tel;

	@Column(name = "homepage", length = 500)
	private String homepage;

	@Column(name = "business_hours", length = 150)
	private String businessHours;

	@Column(name = "closed_days", length = 100)
	private String closedDays;

	@Column(name = "parking_available")
	private Boolean parkingAvailable;

	@Column(name = "allowed_size", length = 100)
	private String allowedSize;

	@Column(name = "restriction", length = 200)
	private String restriction;
}

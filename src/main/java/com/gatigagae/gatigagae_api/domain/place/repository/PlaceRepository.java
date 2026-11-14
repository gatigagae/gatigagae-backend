package com.gatigagae.gatigagae_api.domain.place.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gatigagae.gatigagae_api.domain.place.entity.Place;
import com.gatigagae.gatigagae_api.domain.place.entity.enums.PlaceCategory;

public interface PlaceRepository extends JpaRepository<Place, Long> {

	List<Place> findByCategoryInAndLatitudeBetweenAndLongitudeBetween(Collection<PlaceCategory> categories,
		double minLatitude, double maxLatitude, double minLongitude, double maxLongitude);
}

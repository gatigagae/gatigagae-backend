package com.gatigagae.gatigagae_api.domain.pet.entity;

import java.math.BigDecimal;

import com.gatigagae.gatigagae_api.domain.pet.entity.enums.Breed;
import com.gatigagae.gatigagae_api.domain.pet.entity.enums.CoatType;
import com.gatigagae.gatigagae_api.domain.user.entity.User;
import com.gatigagae.gatigagae_api.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pets")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pet extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pet_id")
	private Long id;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;

	@Column(name = "name", nullable = false, length = 50)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(name = "breed", nullable = false, length = 30)
	private Breed breed;

	@Column(name = "custom_breed_name", length = 30)
	private String customBreedName;

	@Column(name = "age", nullable = false)
	private Integer age;

	@Column(name = "weight", precision = 5, scale = 2)
	private BigDecimal weight;

	@Column(name = "brachycephalic", nullable = false)
	private Boolean brachycephalic;

	@Enumerated(EnumType.STRING)
	@Column(name = "coat_type", nullable = false, length = 10)
	private CoatType coatType;

	@Column(name = "note", length = 200)
	private String note;

	@Column(name = "profile_image_key")
	private String profileImageKey;

	private static String blankToNull(String value) {
		return value == null || value.isBlank() ? null : value.strip();
	}

	public void update(
		String name,
		Breed breed,
		String customBreedName,
		CoatType coatType,
		boolean brachycephalic,
		int age,
		BigDecimal weight,
		String note
	) {
		this.name = name.strip();
		this.breed = breed;
		this.customBreedName = (breed == Breed.OTHER) ? blankToNull(customBreedName) : null;
		this.coatType = coatType;
		this.brachycephalic = brachycephalic;
		this.age = age;
		this.weight = weight;
		this.note = blankToNull(note);
	}

	public void changeProfileImage(String profileImageKey) {
		this.profileImageKey = profileImageKey;
	}
}

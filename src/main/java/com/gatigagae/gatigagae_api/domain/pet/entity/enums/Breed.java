package com.gatigagae.gatigagae_api.domain.pet.entity.enums;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Breed {
	// 이름, 단두종, 털 유형, 비만 기준
	PUG("퍼그", true, CoatType.DOUBLE, 10.2),
	FRENCH_BULLDOG("프렌치 불독", true, CoatType.SINGLE, null),
	BULLDOG("불독(잉글리시)", true, CoatType.SINGLE, 30.0),
	BOSTON_TERRIER("보스턴 테리어", true, CoatType.SINGLE, 13.2),
	BOXER("복서", true, CoatType.SINGLE, null),
	PEKINGESE("페키니즈", true, CoatType.DOUBLE, null),
	SHIH_TZU("시츄", true, null, 9.6),
	CAVALIER_KING_CHARLES_SPANIEL("캐벌리어 킹 찰스 스패니얼", true, null, null),
	MALTESE("말티즈", false, CoatType.SINGLE, 3.84),
	TOY_POODLE("토이 푸들", false, CoatType.SINGLE, 3.6),
	MINIATURE_POODLE("미니어처 푸들", false, CoatType.SINGLE, 8.4),
	YORKSHIRE_TERRIER("요크셔 테리어", false, CoatType.SINGLE, 3.72),
	BICHON_FRISE("비숑 프리제", false, null, 6.6),
	POMERANIAN("포메라니안", false, CoatType.DOUBLE, 3.84),
	CHIHUAHUA("치와와", false, null, 3.24),
	DACHSHUND("닥스훈트(스탠다드)", false, null, 12.0),
	WELSH_CORGI("웰시코기", false, CoatType.DOUBLE, 16.8),
	GOLDEN_RETRIEVER("골든 리트리버", false, CoatType.DOUBLE, 40.8),
	LABRADOR_RETRIEVER("래브라도 리트리버", false, CoatType.DOUBLE, null),
	JINDO("진돗개", false, CoatType.DOUBLE, 27.6),
	BEAGLE("비글", false, CoatType.DOUBLE, 14.4),
	MINIATURE_SCHNAUZER("미니어처 슈나우저", false, CoatType.DOUBLE, 12.0),
	DALMATIAN("달마시안", false, CoatType.SINGLE, 34.8),
	OTHER("믹스견·기타", null, null, null);

	private final String displayName;
	private final Boolean brachycephalic;   // 단두종
	private final CoatType coatType;        // 털 유형
	private final Double obesityWeight;   // 비만 기준

	public boolean isObese(BigDecimal weight) {
		return weight != null && obesityWeight != null && weight.compareTo(BigDecimal.valueOf(obesityWeight)) > 0;
	}
}
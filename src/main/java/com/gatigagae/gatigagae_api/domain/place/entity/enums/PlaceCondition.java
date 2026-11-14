package com.gatigagae.gatigagae_api.domain.place.entity.enums;

public enum PlaceCondition {
	LEASH,              // 목줄
	POOP_BAG,           // 배변봉투
	OUTDOOR_ONLY,       // 실외만 동반
	AREA_LIMITED,       // 일부 공간만
	CARRIER,            // 케이지
	STROLLER,           // 유모차·가방
	HOLD,               // 안고 입장
	MUZZLE,             // 입마개
	MANNER_BELT,        // 매너벨트
	VACCINATED,         // 접종 필수
	NEUTERED,           // 중성화 필수
	REGISTERED,         // 동물등록 필수
	NO_DANGEROUS_BREED, // 맹견 불가
	BREED_LIMITED,      // 일부 견종 불가
	NO_AGGRESSIVE,      // 공격성 있으면 제한
	NO_DISEASE,         // 질환 있으면 불가
	NO_SENIOR,          // 노령견 제한
	NO_BARKING,         // 짖음 심하면 제한
	BRING_SUPPLIES,     // 용품 지참
	MAX_PETS            // 마리 수 제한
}

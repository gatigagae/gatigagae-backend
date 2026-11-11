package com.gatigagae.gatigagae_api.domain.pet.entity;

import java.time.LocalDateTime;

import com.gatigagae.gatigagae_api.domain.pet.entity.enums.PetQrCodeStatus;
import com.gatigagae.gatigagae_api.domain.user.entity.User;
import com.gatigagae.gatigagae_api.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "pet_qr_codes")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PetQrCode extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pet_qr_code_id")
	private Long id;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;

	@Column(name = "code_hash", nullable = false, unique = true, length = 64)
	private String codeHash;

	@Column(name = "expires_at", nullable = false)
	private LocalDateTime expiresAt;

	@Column(name = "used_at")
	private LocalDateTime usedAt;

	public static PetQrCode create(User user, String codeHash, LocalDateTime expiresAt) {
		return PetQrCode.builder()
			.user(user)
			.codeHash(codeHash)
			.expiresAt(expiresAt)
			.build();
	}

	public void reissue(String codeHash, LocalDateTime expiresAt) {
		this.codeHash = codeHash;
		this.expiresAt = expiresAt;
		this.usedAt = null;
	}

	public boolean isUsable(LocalDateTime now) {
		return usedAt == null && now.isBefore(expiresAt);
	}

	public void use(LocalDateTime now) {
		this.usedAt = now;
	}

	public PetQrCodeStatus status(LocalDateTime now) {
		if (usedAt != null) {
			return PetQrCodeStatus.COMPLETED;
		}
		return now.isBefore(expiresAt) ? PetQrCodeStatus.WAITING : PetQrCodeStatus.EXPIRED;
	}
}

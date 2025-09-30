package klieme.artdiary.user.data_access.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class UserEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id", nullable = false)
	private Long userId;
	@Column(nullable = false)
	private String email;
	@Column(nullable = false)
	private String nickname;
	@Column
	private String profile;
	@Column(name = "art_field")
	private String artField;
	@Column(name = "refresh_token")
	private String refreshToken;
	@Column(name = "alarm_token")
	private String alarmToken;
	@Column(name = "provider_type", nullable = false)
	private String providerType;
	@Column(name = "role_type", nullable = false)
	private String roleType;

	@Builder
	public UserEntity(Long userId, String email, String nickname, String profile, String artField,
		String refreshToken, String alarmToken, String providerType, String roleType) {
		this.userId = userId;
		this.email = email;
		this.nickname = nickname;
		this.profile = profile;
		this.artField = artField;
		this.refreshToken = refreshToken;
		this.alarmToken = alarmToken;
		this.providerType = providerType;
		this.roleType = roleType;
	}

	public void updateUser(UserEntity user) {
		if (user.getNickname() != null) {
			this.nickname = user.getNickname();
		}
		this.profile = user.getProfile();
		if (user.getArtField() != null) {
			this.artField = user.getArtField();
		}
		if (user.getAlarmToken() != null) {
			this.alarmToken = user.getAlarmToken();
		}
		if (user.getProviderType() != null) {
			this.providerType = user.getProviderType();
		}
	}

	public void updateRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}

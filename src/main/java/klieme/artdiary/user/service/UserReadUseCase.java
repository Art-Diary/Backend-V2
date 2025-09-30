package klieme.artdiary.user.service;

import java.util.List;
import java.util.Objects;

import klieme.artdiary.user.data_access.entity.UserEntity;
import klieme.artdiary.user.dto.NotiInfo;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface UserReadUseCase {

	FindUserResult getUserInfo();

	String verifyNickname(VerifyNicknameQuery command);

	FindAccessTokenResult reissueAccessToken(ReissueAccessTokenQuery command);

	@EqualsAndHashCode
	@Getter
	@ToString
	@Builder
	class VerifyNicknameQuery {
		private final String nickname;
	}

	@EqualsAndHashCode
	@Getter
	@ToString
	@Builder
	class ReissueAccessTokenQuery {
		private final String accessToken;
	}

	@Getter
	@ToString
	@Builder
	class FindUserResult {
		private final Long userId;
		private final String nickname;
		private final String email;
		private final String profile;
		private final String artField;
		private final Boolean initInfo;
		private final String providerType;
		private final String accessToken;
		private final String roleType;
		private final List<NotiInfo> notiList;

		public static FindUserResult findUserInfo(UserEntity user) {
			return FindUserResult.builder()
				.userId(user.getUserId())
				.nickname(user.getNickname())
				.email(user.getEmail())
				.profile(user.getProfile())
				.artField(user.getArtField() == null || Objects.equals(user.getArtField(), ".") ? "그외" :
					user.getArtField())
				.providerType(user.getProviderType())
				.roleType(user.getRoleType())
				.build();
		}

		public static FindUserResult findUserLoginInfo(UserEntity user, Boolean initInfo, String accessToken,
			List<NotiInfo> notiList) {
			return FindUserResult.builder()
				.userId(user.getUserId())
				.email(user.getEmail())
				.initInfo(initInfo)
				.nickname(initInfo ? user.getNickname() : null)
				.profile(initInfo ? user.getProfile() : null)
				.artField(initInfo ? (user.getArtField() == null || Objects.equals(user.getArtField(), ".") ? "그외" :
					user.getArtField()) : null)
				.providerType(user.getProviderType())
				.accessToken(accessToken)
				.roleType(user.getRoleType())
				.notiList(notiList)
				.build();
		}
	}

	@Getter
	@ToString
	@Builder
	class FindAccessTokenResult {
		private final String accessToken;

		@Builder
		public static FindAccessTokenResult findAccessToken(String accessToken) {
			return FindAccessTokenResult.builder()
				.accessToken(accessToken)
				.build();
		}
	}
}

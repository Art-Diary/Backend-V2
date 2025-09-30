package klieme.artdiary.user.ui.view;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.user.dto.NotiInfo;
import klieme.artdiary.user.service.UserReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserView {
	private final Long userId;
	private final String nickname;
	private final String email;
	private final String profile; //⇒ profile: byte로 변환된 이미지가 string 형식으로 전달됨.
	private final String artField;
	private final Boolean initInfo;
	private final String providerType;
	private final String accessToken;
	private final String roleType;
	private final List<NotiInfo> notiList;

	@Builder
	public UserView(UserReadUseCase.FindUserResult result) {
		this.userId = result.getUserId();
		this.email = result.getEmail();
		this.profile = result.getProfile();
		this.artField = result.getArtField();
		this.nickname = result.getNickname();
		this.initInfo = result.getInitInfo();
		this.providerType = result.getProviderType();
		this.accessToken = result.getAccessToken();
		this.roleType = result.getRoleType();
		this.notiList = result.getNotiList();
	}
}

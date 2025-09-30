package klieme.artdiary.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class NotiInfo {
	private final Long notiId;
	private final String notiCode;
	private final String notiName;
	private final String notiSubText;
	private final Boolean notiState;
}

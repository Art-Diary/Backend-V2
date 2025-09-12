package klieme.artdiary.solo.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class SoloDiaryForCreateInfo {
	private final Long questionId;
	private final String answer;
	private final LocalDateTime writeDate;
	private final Boolean isPublic;
}

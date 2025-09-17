package klieme.artdiary.exhibition.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class SoloDiaryListForExh {
	private final Long soloDiaryId;
	private final Long questionId;
	private final String question;
	private final String answer;
	private final String writeDate;
	private final Long userId;
	private final String nickname;
	private final String profile;
}

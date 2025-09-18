package klieme.artdiary.exhibition.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.exhibition.service.ExhDetailReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SoloDiaryOfExhView {
	private final Long soloDiaryId;
	private final Long questionId;
	private final String question;
	private final String answer;
	private final String writeDate;
	private final Boolean isPublic;
	private final Long userId; // 작성자. 탈퇴한 경우면 null
	private final String nickname; // 작성자. 탈퇴한 경우면 null
	private final String profile; // 작성자. 탈퇴한 경우면 null

	@Builder
	public SoloDiaryOfExhView(ExhDetailReadUseCase.FindSoloDiaryResult result) {
		this.soloDiaryId = result.getSoloDiaryId();
		this.questionId = result.getQuestionId();
		this.question = result.getQuestion();
		this.answer = result.getAnswer();
		this.writeDate = result.getWriteDate();
		this.isPublic = result.getIsPublic();
		this.userId = result.getUserId();
		this.nickname = result.getNickname();
		this.profile = result.getProfile();
	}
}

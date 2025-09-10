package klieme.artdiary.solo.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.solo.service.SoloDiaryReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SoloDiaryView {
	private final Long soloDiaryId;
	private final Long questionId;
	private final String question;
	private final String answer;
	private final String writeDate;
	private final Boolean isPublic;

	@Builder
	public SoloDiaryView(SoloDiaryReadUseCase.FindSoloDiaryResult result) {
		this.soloDiaryId = result.getSoloDiaryId();
		this.questionId = result.getQuestionId();
		this.question = result.getQuestion();
		this.answer = result.getAnswer();
		this.writeDate = result.getWriteDate();
		this.isPublic = result.getIsPublic();
	}
}

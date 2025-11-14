package klieme.artdiary.solo.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.solo.service.QuestionReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuestionView {
	private final Long questionId;
	private final String questionText;

	@Builder
	public QuestionView(QuestionReadUseCase.FindQuestionResult result) {
		this.questionId = result.getQuestionId();
		this.questionText = result.getQuestionText();
	}
}

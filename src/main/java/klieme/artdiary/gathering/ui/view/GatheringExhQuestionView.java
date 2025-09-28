package klieme.artdiary.gathering.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.gathering.service.GatheringQuestionReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GatheringExhQuestionView {
	private final Long gatheringQuestionId;
	private final String question;

	@Builder
	public GatheringExhQuestionView(GatheringQuestionReadUseCase.FindGatheringQuestionResult result) {
		this.gatheringQuestionId = result.getGatheringQuestionId();
		this.question = result.getQuestion();
	}
}

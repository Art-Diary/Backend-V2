package klieme.artdiary.gathering.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface GatheringQuestionOperationUseCase {
	void updateGatheringExhQuestion(GatheringQuestionUpdateCommand command);

	@EqualsAndHashCode
	@Builder
	@Getter
	@ToString
	class GatheringQuestionUpdateCommand {
		private final Long gatheringId;
		private final Long exhId;
		private final Long questionId;
		private final String questionText;
	}
}

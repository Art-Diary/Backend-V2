package klieme.artdiary.solo.service;

import java.time.LocalDateTime;
import java.util.List;

import klieme.artdiary.solo.dto.EvalChoiceInfo;
import klieme.artdiary.solo.dto.SoloDiaryForCreateInfo;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface SoloDiaryOperationUseCase {
	void createSoloDiary(SoloDiaryCreateCommand command);

	void updateSoloDiary(SoloDiaryUpdateCommand command);

	void deleteSoloDiary(Long visitExhId, Long soloDiaryId);

	void updateEvaluationList(EvalChoiceUpdateCommand command);

	@EqualsAndHashCode
	@Builder
	@Getter
	@ToString
	class SoloDiaryCreateCommand {
		private final Long visitExhId;
		private final Boolean initEval;
		private final List<SoloDiaryForCreateInfo> soloDiaryInfoList;
		private final List<EvalChoiceInfo> evalChoiceInfoList;
	}

	@EqualsAndHashCode
	@Builder
	@Getter
	@ToString
	class SoloDiaryUpdateCommand {
		private final Long visitExhId;
		private final Long soloDiaryId;
		private final Long questionId;
		private final String answer;
		private final LocalDateTime writeDate;
		private final Boolean isPublic;
	}

	@EqualsAndHashCode
	@Builder
	@Getter
	@ToString
	class EvalChoiceUpdateCommand {
		private final Long visitExhId;
		List<EvalChoiceInfo> evalChoiceInfoList;
	}
}

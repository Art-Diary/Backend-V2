package klieme.artdiary.solo.service;

import java.time.LocalDate;
import java.util.List;

import klieme.artdiary.solo.dto.EvalChoiceInfo;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface SoloDiaryOperationUseCase {
	void createSoloDiary(SoloDiaryCreateUpdateCommand command);

	void updateSoloDiary(SoloDiaryCreateUpdateCommand command);

	void deleteSoloDiary(Long visitExhId, Long soloDiaryId);

	void updateEvaluationList(EvalChoiceUpdateCommand command);

	@EqualsAndHashCode
	@Builder
	@Getter
	@ToString
	class SoloDiaryCreateUpdateCommand {
		private final Long visitExhId;
		private final Long soloDiaryId;
		private final Long questionId;
		private final String answer;
		private final LocalDate writeDate;
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

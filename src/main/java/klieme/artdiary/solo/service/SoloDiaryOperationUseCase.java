package klieme.artdiary.solo.service;

import java.time.LocalDate;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface SoloDiaryOperationUseCase {
	SoloDiaryReadUseCase.FindSoloDiaryResult createSoloDiary(SoloDiaryCreateUpdateCommand command);

	SoloDiaryReadUseCase.FindSoloDiaryResult updateSoloDiary(SoloDiaryCreateUpdateCommand command);

	void deleteSoloDiary(Long visitExhId, Long soloDiaryId);

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
}

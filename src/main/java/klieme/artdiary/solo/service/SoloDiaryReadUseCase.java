package klieme.artdiary.solo.service;

import java.time.LocalDate;
import java.util.List;

import klieme.artdiary.solo.model.EvalInfo;
import klieme.artdiary.solo.model.SoloDiaryInfo;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface SoloDiaryReadUseCase {
	FindSoloDiaryResult getSoloDiaryList(Long visitExhId);

	@EqualsAndHashCode
	@Getter
	@ToString
	@Builder
	class MyDiariesFindQuery {
		private final Long exhId;
		private final Boolean forget;
		private final LocalDate visitDate;
		private final Long gatherId;
	}

	@Getter
	@ToString
	@Builder
	class FindSoloDiaryResult {
		private final List<EvalInfo> evalInfoList;
		private final List<SoloDiaryInfo> soloDiaryInfoList;

		public static FindSoloDiaryResult findBySoloDiary(List<EvalInfo> evalInfoList,
			List<SoloDiaryInfo> soloDiaryInfoList) {
			return FindSoloDiaryResult.builder()
				.evalInfoList(evalInfoList)
				.soloDiaryInfoList(soloDiaryInfoList)
				.build();
		}
	}
}

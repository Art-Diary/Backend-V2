package klieme.artdiary.mate.service;

import static klieme.artdiary.common.FormatDate.*;

import java.time.LocalDate;
import java.util.List;

import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.solo.model.EvalInfo;
import klieme.artdiary.solo.model.SoloDiaryInfo;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface MateExhReadUseCase {
	List<FindMateExhsResult> getMateExhsList(Long mateId);

	FindMateDiaryResult getMateDiaryList(MateDiaryFindQuery query);

	@EqualsAndHashCode
	@Getter
	@ToString
	@Builder
	class MateDiaryFindQuery {
		private final Long mateId;
		private final Long exhId;
	}

	@Getter
	@ToString
	@Builder
	class FindMateExhsResult {
		private final Long exhId;
		private final String exhName;
		private final String gallery;
		private final String poster;
		private final String visitDate;

		@Builder
		public static FindMateExhsResult findMateExhs(ExhEntity entity, LocalDate visitDate) {
			return FindMateExhsResult.builder()
				.exhId(entity.getExhId())
				.exhName(entity.getExhName())
				.poster(entity.getPoster())
				.visitDate(changeDateFormat(visitDate))
				.build();
		}
	}

	@Getter
	@ToString
	@Builder
	class FindMateDiaryResult {
		private final List<EvalInfo> evalInfoList;
		private final List<SoloDiaryInfo> soloDiaryInfoList;

		@Builder
		public static FindMateDiaryResult findMateDiary(List<EvalInfo> evalInfoList,
			List<SoloDiaryInfo> soloDiaryInfoList) {
			return FindMateDiaryResult.builder()
				.evalInfoList(evalInfoList)
				.soloDiaryInfoList(soloDiaryInfoList)
				.build();
		}
	}
}

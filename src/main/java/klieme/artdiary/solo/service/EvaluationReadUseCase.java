package klieme.artdiary.solo.service;

import java.util.List;

import klieme.artdiary.solo.data_access.entity.EvalFactorEntity;
import klieme.artdiary.solo.dto.OptionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public interface EvaluationReadUseCase {
	List<FindEvaluationResult> getEvaluationInfo();

	@Getter
	@ToString
	@Builder
	class FindEvaluationResult {
		private final Integer factorId;
		private final String factorCode;
		private final String factorName;
		private final List<OptionInfo> optionInfoList;

		public static FindEvaluationResult of(EvalFactorEntity evalFactor, List<OptionInfo> optionInfoList) {
			return FindEvaluationResult.builder()
				.factorId(evalFactor.getFactorId())
				.factorCode(evalFactor.getCode())
				.factorName(evalFactor.getName())
				.optionInfoList(optionInfoList)
				.build();
		}
	}
}

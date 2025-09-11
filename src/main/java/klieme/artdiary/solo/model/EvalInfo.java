package klieme.artdiary.solo.model;

import klieme.artdiary.solo.data_access.entity.EvalFactorEntity;
import klieme.artdiary.solo.data_access.entity.EvalOptionEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class EvalInfo {
	private final Integer factorId;
	private final String factorCode;
	private final String factorName;
	private final Integer optionId;
	private final String optionCode;
	private final String optionName;
	private final String optionIcon;

	public static EvalInfo of(EvalFactorEntity evalFactor, EvalOptionEntity evalOption) {
		return EvalInfo.builder()
			.factorId(evalFactor.getFactorId())
			.factorCode(evalFactor.getCode())
			.factorName(evalFactor.getName())
			.optionId(evalOption.getOptionId())
			.optionCode(evalOption.getCode())
			.optionName(evalOption.getName())
			.optionIcon(evalOption.getIcon())
			.build();
	}
}

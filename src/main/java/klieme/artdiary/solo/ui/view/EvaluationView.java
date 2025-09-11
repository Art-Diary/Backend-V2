package klieme.artdiary.solo.ui.view;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.solo.dto.OptionInfo;
import klieme.artdiary.solo.service.EvaluationReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EvaluationView {
	private final Integer factorId;
	private final String factorCode;
	private final String factorName;
	private final List<OptionInfo> optionInfoList;

	@Builder
	public EvaluationView(EvaluationReadUseCase.FindEvaluationResult result) {
		this.factorId = result.getFactorId();
		this.factorCode = result.getFactorCode();
		this.factorName = result.getFactorName();
		this.optionInfoList = result.getOptionInfoList();
	}
}

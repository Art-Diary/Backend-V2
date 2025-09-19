package klieme.artdiary.mate.ui.view;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.mate.service.MateExhReadUseCase;
import klieme.artdiary.solo.model.EvalInfo;
import klieme.artdiary.solo.model.SoloDiaryInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MateDiaryView {
	private final List<EvalInfo> evalInfoList;
	private final List<SoloDiaryInfo> soloDiaryInfoList;

	@Builder
	public MateDiaryView(MateExhReadUseCase.FindMateDiaryResult result) {
		this.evalInfoList = result.getEvalInfoList();
		this.soloDiaryInfoList = result.getSoloDiaryInfoList();
	}
}

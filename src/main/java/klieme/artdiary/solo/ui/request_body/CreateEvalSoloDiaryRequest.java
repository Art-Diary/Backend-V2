package klieme.artdiary.solo.ui.request_body;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateEvalSoloDiaryRequest {
	@NotNull
	private Boolean initEval;
	// 평가 목록
	private List<CreateEvalRequest> evalChoiceInfoList;
	// 기록 목록
	@NotEmpty
	private List<SoloDiaryRequest> soloDiaryInfoList;
}

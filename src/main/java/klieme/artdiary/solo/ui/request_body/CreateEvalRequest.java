package klieme.artdiary.solo.ui.request_body;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateEvalRequest {
	@NotNull
	private Integer factorId;
	@NotNull
	private Integer optionId;
}

package klieme.artdiary.gathering.ui.request_body;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateGatheringQuestionRequest {
	@NotBlank
	private String questionText;
}

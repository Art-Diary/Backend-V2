package klieme.artdiary.solo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class EvalChoiceInfo {
	private final Integer factorId;
	private final Integer optionId;
}

package klieme.artdiary.solo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class OptionInfo {
	private final Integer optionId;
	private final String optionCode;
	private final String optionName;
	private final String optionIcon;
}

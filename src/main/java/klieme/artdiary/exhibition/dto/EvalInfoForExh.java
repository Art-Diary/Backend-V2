package klieme.artdiary.exhibition.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class EvalInfoForExh {
	private final Integer factorId;
	private final String factorCode;
	private final String factorName;
	private final Integer optionId;
	private final String optionCode;
	private final String optionName;
	private final String optionIcon;
}
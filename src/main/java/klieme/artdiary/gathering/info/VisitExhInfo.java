package klieme.artdiary.gathering.info;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class VisitExhInfo {
	private final Long exhId;
	private final String exhName;
	private final String gallery;
	private final String poster;
	private final String startDate;
	private final String endDate;
	private final String visitDate;
}

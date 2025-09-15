package klieme.artdiary.calendar.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ScheduleInfo {
	private final Long exhId;
	private final String exhName;
	private final String gallery;
	private final String startDate;
	private final String endDate;
	private final String poster;
	private final String visitDate;
	private final Long gatheringId; // 개인일 경우 null
	private final String gatheringName; // 개인일 경우 null
	private final Long visitExhId;
}

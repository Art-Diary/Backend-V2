package klieme.artdiary.record_data_access.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import klieme.artdiary.calendar.enums.CalendarKind;

public interface VisitExhRepoCustom {
	List<Map<String, Object>> getSoloVisitExhListWithExhInfo(Long userId);

	List<Map<String, Object>> getVisitInfoForCalendar(CalendarKind kind, Long userId, Long gatheringId,
		LocalDate startDate, LocalDate endDate);

	List<Map<String, Object>> getGatheringVisitExhListWithExhInfo(Long gatheringId);

	List<Map<String, Object>> getVisitSoloDateForFcm(Long notiId);

	List<Map<String, Object>> getVisitGatheringDateForFcm(Long notiId);
}

package klieme.artdiary.record_data_access.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import klieme.artdiary.calendar.enums.CalendarKind;

public interface VisitExhRepoCustom {
	List<Map<String, Object>> getVisitExhListWithExhInfo(Long userId);

	List<Map<String, Object>> getVisitInfoForCalendar(CalendarKind kind, Long userId, Long gatherId,
		LocalDate startDate, LocalDate endDate);
}

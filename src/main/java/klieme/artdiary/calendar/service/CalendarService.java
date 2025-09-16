package klieme.artdiary.calendar.service;

import static klieme.artdiary.common.FormatDate.*;
import static klieme.artdiary.common.SecurityUtil.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import klieme.artdiary.calendar.dto.ScheduleInfo;
import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.gathering.data_access.entity.GatheringEntity;
import klieme.artdiary.record_data_access.entity.VisitExhEntity;
import klieme.artdiary.record_data_access.repository.VisitExhRepository;

@Service
public class CalendarService implements CalendarReadUseCase {
	private final VisitExhRepository visitExhRepository;

	@Autowired
	public CalendarService(VisitExhRepository visitExhRepository) {
		this.visitExhRepository = visitExhRepository;
	}

	@Override
	public List<FindCalendarResult> getExhSchedule(CalendarFindQuery query) {
		// 반환 리스트
		List<FindCalendarResult> results = new ArrayList<>();
		// FindCalendarTestResult의 dayOfScheduleInfos 값 구하기
		HashMap<Integer, List<ScheduleInfo>> dayOfScheduleInfos = new HashMap<>();
		// 날짜 비교 중 월 시작 날짜와 마지막 날짜 구하기
		LocalDate selectedStartDate = LocalDate.of(query.getYear(), query.getMonth(), 1);
		LocalDate selectedEndDate = selectedStartDate.withDayOfMonth(selectedStartDate.lengthOfMonth());
		List<Map<String, Object>> visitInfo = visitExhRepository.getVisitInfoForCalendar(query.getKind(), getUserId(),
			query.getGatheringId(), selectedStartDate, selectedEndDate);

		dayOfVisitInfo(visitInfo, dayOfScheduleInfos);
		for (int day = 1; day <= selectedEndDate.getDayOfMonth(); day++) {
			if (dayOfScheduleInfos.get(day) != null) {
				results.add(FindCalendarResult.findByCalendar(day, dayOfScheduleInfos.get(day)));
			} else {
				results.add(FindCalendarResult.findByCalendar(day, null));
			}
		}
		return results;
	}

	private void dayOfVisitInfo(List<Map<String, Object>> visitInfo,
		HashMap<Integer, List<ScheduleInfo>> dayOfScheduleInfos) {
		for (Map<String, Object> info : visitInfo) {
			VisitExhEntity visitExh = (VisitExhEntity)info.get("visitExh");
			GatheringEntity gathering = (GatheringEntity)info.get("gathering");
			ExhEntity exhibition = (ExhEntity)info.get("exhibition");

			// 날짜 별 전시회 추가
			int day = visitExh.getVisitDate().getDayOfMonth();

			dayOfScheduleInfos.computeIfAbsent(day, k -> new ArrayList<>());
			dayOfScheduleInfos.get(day).add(ScheduleInfo.builder()
				.exhId(exhibition.getExhId())
				.exhName(exhibition.getExhName())
				.gallery(exhibition.getGallery())
				.startDate(changeDateFormat(exhibition.getExhPeriodStart()))
				.endDate(changeDateFormat(exhibition.getExhPeriodEnd()))
				.poster(exhibition.getPoster())
				.visitDate(changeDateFormat(visitExh.getVisitDate()))
				.visitExhId(visitExh.getVisitExhId())
				.gatheringId(gathering != null ? gathering.getGatheringId() : null)
				.gatheringName(gathering != null ? gathering.getGatheringName() : null)
				.build());
		}
	}

	private Long getUserId() {
		return getCurrentUserEntity().getUserId();
	}
}

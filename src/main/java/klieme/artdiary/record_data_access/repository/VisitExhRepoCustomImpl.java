package klieme.artdiary.record_data_access.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import klieme.artdiary.calendar.enums.CalendarKind;
import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.exhibition.data_access.entity.QExhEntity;
import klieme.artdiary.gathering.data_access.entity.GatheringEntity;
import klieme.artdiary.gathering.data_access.entity.QGatheringEntity;
import klieme.artdiary.gathering.data_access.entity.QGatheringMemberEntity;
import klieme.artdiary.record_data_access.entity.QVisitExhEntity;
import klieme.artdiary.record_data_access.entity.VisitExhEntity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VisitExhRepoCustomImpl implements VisitExhRepoCustom {
	private final JPAQueryFactory query;

	@Override
	public List<Map<String, Object>> getSoloVisitExhListWithExhInfo(Long userId) {
		QExhEntity exh = QExhEntity.exhEntity;
		QVisitExhEntity visitExh = QVisitExhEntity.visitExhEntity;

		List<Tuple> tuples = query
			.select(exh, visitExh.visitDate.max())
			.from(visitExh)
			.leftJoin(exh).on(visitExh.exhId.eq(exh.exhId))
			.fetchJoin()
			.where(visitExh.userId.eq(userId))
			.groupBy(visitExh.exhId)
			.orderBy(visitExh.visitDate.max().desc())
			.fetch();

		List<Map<String, Object>> result = new ArrayList<>();

		for (Tuple tuple : tuples) {
			Map<String, Object> row = new HashMap<>();
			row.put("exhibition", tuple.get(0, ExhEntity.class));
			row.put("visitDate", tuple.get(1, LocalDate.class));
			result.add(row);
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> getVisitInfoForCalendar(CalendarKind kind, Long userId, Long gatheringId,
		LocalDate startDate, LocalDate endDate) {
		QVisitExhEntity visitExh = QVisitExhEntity.visitExhEntity;
		QGatheringEntity gathering = QGatheringEntity.gatheringEntity;
		QGatheringMemberEntity gatheringMember = QGatheringMemberEntity.gatheringMemberEntity;
		QExhEntity exh = QExhEntity.exhEntity;
		BooleanBuilder builder = new BooleanBuilder();
		BooleanBuilder gatheringBuilder = new BooleanBuilder();

		// 개인: gatheringId는 항상 null
		// 모임: gatheringId는 항상 notnull
		// 전체: gatheringId는 무엇이 들어가든 상관x
		if (kind == CalendarKind.ALONE) {
			builder.and(visitExh.userId.eq(userId));
			builder.and(visitExh.gatheringId.isNull());
		} else if (kind == CalendarKind.GATHER) {
			builder.and(gatheringMember.gatheringMemberId.userId.eq(userId));
			builder.and(visitExh.gatheringId.eq(gatheringId));
		} else {
			gatheringBuilder.and(visitExh.gatheringId.isNotNull());
			gatheringBuilder.and(gatheringMember.gatheringMemberId.userId.eq(userId));
			builder.or(gatheringBuilder);
			builder.or(visitExh.userId.eq(userId));
		}
		List<Tuple> tuples = query.select(visitExh, gathering, exh)
			.from(visitExh)
			.leftJoin(gathering).on(visitExh.gatheringId.eq(gathering.gatheringId))
			.leftJoin(gatheringMember).on(gathering.gatheringId.eq(gatheringMember.gatheringMemberId.gatheringId))
			.leftJoin(exh).on(visitExh.exhId.eq(exh.exhId))
			.fetchJoin()
			.where(builder, visitExh.visitDate.goe(startDate), visitExh.visitDate.loe(endDate))
			.fetch();
		List<Map<String, Object>> result = new ArrayList<>();

		for (Tuple tuple : tuples) {
			Map<String, Object> row = new HashMap<>();
			row.put("visitExh", tuple.get(0, VisitExhEntity.class));
			row.put("gathering", tuple.get(1, GatheringEntity.class));
			row.put("exhibition", tuple.get(2, ExhEntity.class));
			result.add(row);
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> getGatheringVisitExhListWithExhInfo(Long gatheringId) {
		QExhEntity exh = QExhEntity.exhEntity;
		QVisitExhEntity visitExh = QVisitExhEntity.visitExhEntity;

		List<Tuple> tuples = query
			.select(exh, visitExh.visitDate.max())
			.from(visitExh)
			.leftJoin(exh).on(visitExh.exhId.eq(exh.exhId))
			.fetchJoin()
			.where(visitExh.gatheringId.eq(gatheringId))
			.groupBy(visitExh.exhId)
			.orderBy(visitExh.visitDate.max().desc())
			.fetch();

		List<Map<String, Object>> result = new ArrayList<>();

		for (Tuple tuple : tuples) {
			Map<String, Object> row = new HashMap<>();
			row.put("exhibition", tuple.get(0, ExhEntity.class));
			row.put("visitDate", tuple.get(1, LocalDate.class));
			result.add(row);
		}
		return result;
	}
}

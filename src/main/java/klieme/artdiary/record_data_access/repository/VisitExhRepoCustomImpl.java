package klieme.artdiary.record_data_access.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.exhibition.data_access.entity.QExhEntity;
import klieme.artdiary.record_data_access.entity.QVisitDateEntity;
import klieme.artdiary.record_data_access.entity.QVisitExhEntity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VisitExhRepoCustomImpl implements VisitExhRepoCustom {
	private final JPAQueryFactory query;

	@Override
	public List<Map<String, Object>> getVisitExhListWithExhInfo(Long userId, Long gatheringId) {
		QExhEntity exh = QExhEntity.exhEntity;
		QVisitExhEntity visitExh = QVisitExhEntity.visitExhEntity;
		QVisitDateEntity visitDate = QVisitDateEntity.visitDateEntity;
		BooleanBuilder builder = new BooleanBuilder();

		if (userId != null) {
			builder.and(visitExh.userId.eq(userId));
		}
		if (gatheringId != null) {
			builder.and(visitExh.gatheringId.eq(gatheringId));
		}

		List<Tuple> tuples = query
			.select(exh, visitDate.visitExhId, visitDate.visitDate.max())
			.from(visitExh)
			.leftJoin(exh).on(visitExh.exhId.eq(exh.exhId))
			.leftJoin(visitDate).on(visitExh.visitExhId.eq(visitDate.visitExhId))
			.fetchJoin()
			.where(builder)
			.groupBy(visitDate.visitExhId)
			.orderBy(visitDate.visitDate.max().desc())
			.fetch();

		List<Map<String, Object>> result = new ArrayList<>();

		for (Tuple tuple : tuples) {
			Map<String, Object> row = new HashMap<>();
			row.put("exhibition", tuple.get(0, ExhEntity.class));
			row.put("visitExhId", tuple.get(1, Long.class));
			row.put("visitDate", tuple.get(2, LocalDate.class));
			result.add(row);
		}
		return result;
	}
}

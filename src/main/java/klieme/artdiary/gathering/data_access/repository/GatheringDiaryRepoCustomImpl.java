package klieme.artdiary.gathering.data_access.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import klieme.artdiary.gathering.data_access.entity.GatheringDiaryEntity;
import klieme.artdiary.gathering.data_access.entity.QGatheringDiaryEntity;
import klieme.artdiary.user.data_access.entity.QUserEntity;
import klieme.artdiary.user.data_access.entity.UserEntity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GatheringDiaryRepoCustomImpl implements GatheringDiaryRepoCustom {
	private final JPAQueryFactory query;

	@Override
	public List<Map<String, Object>> getGatheringDiaryListWithUser(Long gatherId, Long exhId, Long questionId) {
		QGatheringDiaryEntity gatheringDiary = QGatheringDiaryEntity.gatheringDiaryEntity;
		QUserEntity user = QUserEntity.userEntity;

		List<Tuple> tuples = query
			.select(gatheringDiary, user)
			.from(gatheringDiary)
			.leftJoin(user).on(gatheringDiary.userId.eq(user.userId))
			.fetchJoin()
			.where(gatheringDiary.gatheringId.eq(gatherId), gatheringDiary.exhId.eq(exhId),
				gatheringDiary.gatheringQuestionId.eq(questionId))
			.orderBy(gatheringDiary.writeDate.desc())
			.fetch();

		List<Map<String, Object>> results = new ArrayList<>();

		for (Tuple tuple : tuples) {
			Map<String, Object> row = new HashMap<>();
			row.put("gatheringDiary", tuple.get(0, GatheringDiaryEntity.class));
			row.put("user", tuple.get(1, UserEntity.class));
			results.add(row);
		}
		return results;
	}
}

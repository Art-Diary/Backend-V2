package klieme.artdiary.like_exh.data_access.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.exhibition.data_access.entity.QExhEntity;
import klieme.artdiary.like_exh.data_access.entity.QLikeExhEntity;
import klieme.artdiary.user.data_access.entity.QUserEntity;
import klieme.artdiary.user.data_access.entity.UserEntity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LikeExhRepoCustomImpl implements LikeExhRepoCustom {
	private final JPAQueryFactory query;

	@Override
	public List<Map<String, Object>> getLikeExhWithUserAndExh() {
		QLikeExhEntity likeExh = QLikeExhEntity.likeExhEntity;
		QUserEntity user = QUserEntity.userEntity;
		QExhEntity exh = QExhEntity.exhEntity;

		List<Tuple> tuples = query
			.select(user, exh)
			.from(likeExh)
			.leftJoin(user).on(likeExh.likeExhId.userId.eq(user.userId))
			.leftJoin(exh).on(likeExh.likeExhId.exhId.eq(exh.exhId))
			.fetchJoin()
			.where(user.alarmToken.isNotNull(), exh.exhId.isNotNull())//, user.favoriteExhAlarm.eq(true)
			.fetch();

		List<Map<String, Object>> result = new ArrayList<>();

		for (Tuple tuple : tuples) {
			Map<String, Object> row = new HashMap<>();
			row.put("user", tuple.get(0, UserEntity.class));
			row.put("exhibition", tuple.get(1, ExhEntity.class));
			result.add(row);
		}
		return result;
	}

	@Override
	public List<ExhEntity> getLikeExhByUserId(Long userId) {
		QLikeExhEntity likeExh = QLikeExhEntity.likeExhEntity;
		QExhEntity exh = QExhEntity.exhEntity;

		return query
			.select(exh)
			.from(likeExh)
			.leftJoin(exh).on(likeExh.likeExhId.exhId.eq(exh.exhId))
			.fetchJoin()
			.where(likeExh.likeExhId.userId.eq(userId))
			.orderBy(likeExh.initDate.desc(), exh.exhName.asc())
			.fetch();
	}
}

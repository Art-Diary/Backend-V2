package klieme.artdiary.gathering.data_access.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import klieme.artdiary.gathering.data_access.entity.GatheringEntity;
import klieme.artdiary.gathering.data_access.entity.QGatheringEntity;
import klieme.artdiary.gathering.data_access.entity.QGatheringMemberEntity;
import klieme.artdiary.mate.data_access.entity.QMateEntity;
import klieme.artdiary.record_data_access.entity.QVisitExhEntity;
import klieme.artdiary.user.data_access.entity.QUserEntity;
import klieme.artdiary.user.data_access.entity.UserEntity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GatheringMemberRepoCustomImpl implements GatheringMemberRepoCustom {
	private final JPAQueryFactory query;

	@Override
	public List<GatheringEntity> getGatheringListByRecentVisitDate(Long userId) {
		QGatheringMemberEntity gatheringMember = QGatheringMemberEntity.gatheringMemberEntity;
		QGatheringEntity gathering = QGatheringEntity.gatheringEntity;
		QVisitExhEntity visitExh = QVisitExhEntity.visitExhEntity;

		// 사용자가 모임에서 최근에 전시회를 방문한 날짜 순으로 정렬
		return query
			.select(gathering)
			.from(gatheringMember)
			.leftJoin(gathering).on(gatheringMember.gatheringMemberId.gatheringId.eq(gathering.gatheringId))
			.leftJoin(visitExh).on(gathering.gatheringId.eq(visitExh.gatheringId))
			.fetchJoin()
			.where(gatheringMember.gatheringMemberId.userId.eq(userId))
			.groupBy(gatheringMember.gatheringMemberId.gatheringId)
			.orderBy(visitExh.visitDate.max().desc())
			.fetch();
	}

	@Override
	public List<Map<String, Object>> getGatheringMateListForSearch(Long gatherId, Long userId, String nickname) {
		QMateEntity mate = QMateEntity.mateEntity;
		QUserEntity user = QUserEntity.userEntity;
		QGatheringMemberEntity gatheringMember = QGatheringMemberEntity.gatheringMemberEntity;

		List<Tuple> tuples = query
			.select(user, new CaseBuilder()
				.when(gatheringMember.gatheringMemberId.gatheringId.isNull()).then(false)
				.otherwise(true))
			.from(mate)
			.leftJoin(gatheringMember)
			.on(mate.toUserId.eq(gatheringMember.gatheringMemberId.userId),
				gatheringMember.gatheringMemberId.gatheringId.eq(gatherId))
			.leftJoin(user)
			.on(mate.toUserId.eq(user.userId))
			.fetchJoin()
			.where(mate.fromUserId.eq(userId)
				, user.nickname.toLowerCase().notLike("%kakao_%")
				, user.nickname.toLowerCase().notLike("%google_%")
				, user.nickname.toLowerCase().notLike("%naver_%")
				, user.nickname.contains(nickname))
			.orderBy(user.nickname.asc())
			.fetch();

		List<Map<String, Object>> results = new ArrayList<>();

		for (Tuple tuple : tuples) {
			Map<String, Object> row = new HashMap<>();
			row.put("userEntity", tuple.get(0, UserEntity.class));
			row.put("isGatheringMate", tuple.get(1, Boolean.class));
			results.add(row);
		}
		return results;
	}

	@Override
	public List<UserEntity> getGatheringMateList(Long gatherId) {
		QGatheringMemberEntity gatheringMember = QGatheringMemberEntity.gatheringMemberEntity;
		QUserEntity user = QUserEntity.userEntity;

		// 사용자가 모임에서 최근에 전시회를 방문한 날짜 순으로 정렬
		return query
			.select(user)
			.from(gatheringMember)
			.leftJoin(user).on(gatheringMember.gatheringMemberId.userId.eq(user.userId))
			.fetchJoin()
			.where(gatheringMember.gatheringMemberId.gatheringId.eq(gatherId))
			.fetch();
	}
}

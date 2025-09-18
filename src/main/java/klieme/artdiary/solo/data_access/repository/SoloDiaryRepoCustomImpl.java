package klieme.artdiary.solo.data_access.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import klieme.artdiary.solo.data_access.entity.QQuestionEntity;
import klieme.artdiary.solo.data_access.entity.QSoloDiaryEntity;
import klieme.artdiary.solo.data_access.entity.QuestionEntity;
import klieme.artdiary.solo.data_access.entity.SoloDiaryEntity;
import klieme.artdiary.user.data_access.entity.QUserEntity;
import klieme.artdiary.user.data_access.entity.UserEntity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SoloDiaryRepoCustomImpl implements SoloDiaryRepoCustom {
	private final JPAQueryFactory query;

	@Override
	public List<Map<String, Object>> getSoloDiaryListWithQuestion(Long exhId, Long userId) {
		QSoloDiaryEntity soloDiary = QSoloDiaryEntity.soloDiaryEntity;
		QQuestionEntity question = QQuestionEntity.questionEntity;

		List<Tuple> tuples = query
			.select(soloDiary, question)
			.from(soloDiary)
			.leftJoin(question).on(soloDiary.questionId.eq(question.questionId))
			.fetchJoin()
			.where(soloDiary.exhId.eq(exhId), soloDiary.userId.eq(userId))
			.orderBy(soloDiary.writeDate.desc())
			.fetch();

		List<Map<String, Object>> result = new ArrayList<>();

		for (Tuple tuple : tuples) {
			Map<String, Object> row = new HashMap<>();
			row.put("soloDiary", tuple.get(0, SoloDiaryEntity.class));
			row.put("question", tuple.get(1, QuestionEntity.class));
			result.add(row);
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> getSoloDiaryListAndUserInfo(Long exhId, Long userId) {
		QSoloDiaryEntity soloDiary = QSoloDiaryEntity.soloDiaryEntity;
		QQuestionEntity question = QQuestionEntity.questionEntity;
		QUserEntity user = QUserEntity.userEntity;
		BooleanBuilder builder = new BooleanBuilder();
		BooleanBuilder privateBuilder = new BooleanBuilder();

		builder.and(soloDiary.isPublic.eq(false));
		builder.and(soloDiary.userId.eq(userId));
		privateBuilder.or(builder);
		privateBuilder.or(soloDiary.isPublic.eq(true));

		List<Tuple> tuples = query
			.select(soloDiary, question, user)
			.from(soloDiary)
			.leftJoin(question).on(soloDiary.questionId.eq(question.questionId))
			.leftJoin(user).on(soloDiary.userId.eq(user.userId))
			.fetchJoin()
			.where(soloDiary.exhId.eq(exhId), privateBuilder)
			.orderBy(soloDiary.writeDate.desc())
			.fetch();

		List<Map<String, Object>> result = new ArrayList<>();

		for (Tuple tuple : tuples) {
			Map<String, Object> row = new HashMap<>();
			row.put("soloDiary", tuple.get(0, SoloDiaryEntity.class));
			row.put("question", tuple.get(1, QuestionEntity.class));
			row.put("user", tuple.get(2, UserEntity.class));
			result.add(row);
		}
		return result;
	}
}

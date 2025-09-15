package klieme.artdiary.solo.data_access.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import klieme.artdiary.solo.data_access.entity.QQuestionEntity;
import klieme.artdiary.solo.data_access.entity.QSoloDiaryEntity;
import klieme.artdiary.solo.data_access.entity.QuestionEntity;
import klieme.artdiary.solo.data_access.entity.SoloDiaryEntity;
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
}

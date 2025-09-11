package klieme.artdiary.solo.data_access.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import klieme.artdiary.solo.data_access.entity.EvalFactorEntity;
import klieme.artdiary.solo.data_access.entity.EvalOptionEntity;
import klieme.artdiary.solo.data_access.entity.QEvalFactorEntity;
import klieme.artdiary.solo.data_access.entity.QEvalOptionEntity;
import klieme.artdiary.solo.data_access.entity.QVisitEvalChoiceEntity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VisitEvalChoiceRepoCustomImpl implements VisitEvalChoiceRepoCustom {
	private final JPAQueryFactory query;

	@Override
	public List<Map<String, Object>> getChoices(Long visitExhId) {
		QEvalFactorEntity evalFactor = QEvalFactorEntity.evalFactorEntity;
		QEvalOptionEntity evalOption = QEvalOptionEntity.evalOptionEntity;
		QVisitEvalChoiceEntity visitEvalChoice = QVisitEvalChoiceEntity.visitEvalChoiceEntity;

		List<Tuple> tuples = query
			.select(evalFactor, evalOption)
			.from(visitEvalChoice)
			.leftJoin(evalOption).on(visitEvalChoice.optionId.eq(evalOption.optionId))
			.leftJoin(evalFactor).on(evalOption.factorId.eq(evalFactor.factorId))
			.fetchJoin()
			.where(visitEvalChoice.visitExhId.eq(visitExhId))
			.orderBy(evalFactor.factorId.asc(), evalOption.optionId.asc())
			.fetch();

		List<Map<String, Object>> result = new ArrayList<>();

		for (Tuple tuple : tuples) {
			Map<String, Object> row = new HashMap<>();
			row.put("evalFactor", tuple.get(0, EvalFactorEntity.class));
			row.put("evalOption", tuple.get(1, EvalOptionEntity.class));
			result.add(row);
		}
		return result;
	}
}

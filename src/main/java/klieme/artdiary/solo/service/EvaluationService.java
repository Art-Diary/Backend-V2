package klieme.artdiary.solo.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import klieme.artdiary.solo.data_access.entity.EvalFactorEntity;
import klieme.artdiary.solo.data_access.entity.EvalOptionEntity;
import klieme.artdiary.solo.data_access.repository.EvalOptionRepository;
import klieme.artdiary.solo.dto.OptionInfo;

@Service
public class EvaluationService implements EvaluationReadUseCase {
	private final EvalOptionRepository evalOptionRepository;

	@Autowired
	public EvaluationService(EvalOptionRepository evalOptionRepository) {
		this.evalOptionRepository = evalOptionRepository;
	}

	@Override
	public List<FindEvaluationResult> getEvaluationInfo() {
		List<Map<String, Object>> infos = evalOptionRepository.getFactorOptionInfoList();
		Map<EvalFactorEntity, List<OptionInfo>> maps = new HashMap<>();

		for (Map<String, Object> info : infos) {
			EvalFactorEntity evalFactor = (EvalFactorEntity)info.get("evalFactor");
			EvalOptionEntity evalOption = (EvalOptionEntity)info.get("evalOption");

			List<OptionInfo> options = maps.computeIfAbsent(evalFactor, k -> new ArrayList<>());

			options.add(OptionInfo.builder()
				.optionId(evalOption.getOptionId())
				.optionCode(evalOption.getCode())
				.optionName(evalOption.getName())
				.optionIcon(evalOption.getIcon())
				.build());
			maps.put(evalFactor, options);
		}
		return maps.entrySet().stream()
			.sorted(Comparator.comparingLong(e -> e.getKey().getFactorId()))
			.map(e -> FindEvaluationResult.of(e.getKey(), e.getValue()))
			.toList();
	}
}

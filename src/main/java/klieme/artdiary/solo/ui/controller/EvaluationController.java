package klieme.artdiary.solo.ui.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import klieme.artdiary.solo.service.EvaluationReadUseCase;
import klieme.artdiary.solo.ui.view.EvaluationView;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/evaluation")
public class EvaluationController {
	private final EvaluationReadUseCase evaluationReadUseCase;

	@Autowired
	public EvaluationController(EvaluationReadUseCase evaluationReadUseCase) {
		this.evaluationReadUseCase = evaluationReadUseCase;
	}

	@GetMapping("")
	public ResponseEntity<List<EvaluationView>> getEvaluationInfo() {

		log.info("[평가 선택지 조회]");

		// 비즈니스 로직 호출
		List<EvaluationReadUseCase.FindEvaluationResult> soloDiaryResults = evaluationReadUseCase.getEvaluationInfo();
		List<EvaluationView> responses = new ArrayList<>();

		soloDiaryResults.forEach((res) -> {
			responses.add(EvaluationView.builder().result(res).build());
		});
		return ResponseEntity.ok(responses);
	}
}
package klieme.artdiary.solo.ui.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import klieme.artdiary.solo.service.QuestionReadUseCase;
import klieme.artdiary.solo.ui.view.QuestionView;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/questions")
public class QuestionController {
	private final QuestionReadUseCase questionReadUseCase;

	@Autowired
	public QuestionController(QuestionReadUseCase questionReadUseCase) {
		this.questionReadUseCase = questionReadUseCase;
	}

	@GetMapping("")
	public ResponseEntity<List<QuestionView>> getQuestionList() {

		log.info("[질문 목록 조회]");

		// 비즈니스 로직 호출
		List<QuestionReadUseCase.FindQuestionResult> questionListResult = questionReadUseCase.getQuestionList();
		List<QuestionView> responses = new ArrayList<>();

		questionListResult.forEach((res) -> {
			responses.add(QuestionView.builder().result(res).build());
		});
		return ResponseEntity.ok(responses);
	}
}

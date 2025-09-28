package klieme.artdiary.gathering.ui.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import klieme.artdiary.gathering.service.GatheringQuestionOperationUseCase;
import klieme.artdiary.gathering.service.GatheringQuestionReadUseCase;
import klieme.artdiary.gathering.ui.request_body.UpdateGatheringQuestionRequest;
import klieme.artdiary.gathering.ui.view.GatheringExhQuestionView;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/gatherings/{gatheringId}/exh-visits/{exhId}/questions")
public class GatheringQuestionController {
	private final GatheringQuestionOperationUseCase gatheringQuestionOperationUseCase;
	private final GatheringQuestionReadUseCase gatheringQuestionReadUseCase;

	@Autowired
	public GatheringQuestionController(GatheringQuestionOperationUseCase gatheringQuestionOperationUseCase,
		GatheringQuestionReadUseCase gatheringQuestionReadUseCase) {
		this.gatheringQuestionOperationUseCase = gatheringQuestionOperationUseCase;
		this.gatheringQuestionReadUseCase = gatheringQuestionReadUseCase;
	}

	// [TODO] create를 따로 만들어야 하나?

	/**
	 * 방문한 전시회의 대화를 위한 질문 목록 조회
	 */
	@GetMapping("")
	public ResponseEntity<List<GatheringExhQuestionView>> getGatheringExhQuestionList(
		@PathVariable(name = "gatheringId") Long gatheringId,
		@PathVariable(name = "exhId") Long exhId
	) {

		log.info("[방문한 전시회의 대화를 위한 질문 목록 조회]");

		var query = GatheringQuestionReadUseCase.GatheringQuestionFindQuery.builder()
			.gatheringId(gatheringId)
			.exhId(exhId)
			.build();

		// 비즈니스 로직 호출
		List<GatheringQuestionReadUseCase.FindGatheringQuestionResult> diaryList = gatheringQuestionReadUseCase.getGatheringExhQuestionList(
			query);
		List<GatheringExhQuestionView> results = new ArrayList<>();

		for (GatheringQuestionReadUseCase.FindGatheringQuestionResult diary : diaryList) {
			results.add(GatheringExhQuestionView.builder().result(diary).build());
		}
		return ResponseEntity.ok(results);
	}

	/**
	 * 방문한 전시회의 한 주제에 대한 하나의 질문 수정
	 */
	@PatchMapping("/{questionId}")
	public ResponseEntity<Void> updateGatheringExhQuestion(
		@PathVariable(name = "gatheringId") Long gatheringId,
		@PathVariable(name = "exhId") Long exhId,
		@PathVariable(name = "questionId") Long questionId,
		@Valid @RequestBody UpdateGatheringQuestionRequest request
	) {
		log.info("[방문한 전시회의 한 주제에 대한 하나의 질문 수정]");
		// request body 데이터 받아오기
		var command = GatheringQuestionOperationUseCase.GatheringQuestionUpdateCommand.builder()
			.gatheringId(gatheringId)
			.exhId(exhId)
			.questionId(questionId)
			.questionText(request.getQuestionText())
			.build();
		// 비즈니스 로직 호출
		gatheringQuestionOperationUseCase.updateGatheringExhQuestion(command);

		return ResponseEntity.status(HttpStatus.OK).build();
	}
}

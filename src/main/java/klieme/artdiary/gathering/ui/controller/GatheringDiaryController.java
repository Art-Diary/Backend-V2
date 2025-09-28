package klieme.artdiary.gathering.ui.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import klieme.artdiary.gathering.service.GatheringDiaryOperationUseCase;
import klieme.artdiary.gathering.service.GatheringDiaryReadUseCase;
import klieme.artdiary.gathering.ui.request_body.GatheringDiaryRequest;
import klieme.artdiary.gathering.ui.view.GatheringDiaryView;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/gatherings/{gatheringId}/exh-visits/{exhId}/questions/{questionId}/diaries")
public class GatheringDiaryController {
	private final GatheringDiaryOperationUseCase gatheringDiaryOperationUseCase;
	private final GatheringDiaryReadUseCase gatheringDiaryReadUseCase;

	@Autowired
	public GatheringDiaryController(GatheringDiaryOperationUseCase gatheringDiaryOperationUseCase,
		GatheringDiaryReadUseCase gatheringDiaryReadUseCase) {
		this.gatheringDiaryOperationUseCase = gatheringDiaryOperationUseCase;
		this.gatheringDiaryReadUseCase = gatheringDiaryReadUseCase;
	}

	/**
	 * 방문한 전시회의 한 주제에 대한 대화 목록 조회
	 */
	@GetMapping("")
	public ResponseEntity<List<GatheringDiaryView>> getGatheringDiaryList(
		@PathVariable(name = "gatheringId") Long gatheringId,
		@PathVariable(name = "exhId") Long exhId,
		@PathVariable(name = "questionId") Long questionId
	) {

		log.info("[방문한 전시회의 한 주제에 대한 대화 목록 조회]");

		var query = GatheringDiaryReadUseCase.GatheringDiaryFindQuery.builder()
			.gatheringId(gatheringId)
			.exhId(exhId)
			.questionId(questionId)
			.build();

		// 비즈니스 로직 호출
		List<GatheringDiaryReadUseCase.FindGatheringDiaryResult> diaryList = gatheringDiaryReadUseCase.getGatheringDiaryList(
			query);
		List<GatheringDiaryView> results = new ArrayList<>();

		for (GatheringDiaryReadUseCase.FindGatheringDiaryResult diary : diaryList) {
			results.add(GatheringDiaryView.builder().result(diary).build());
		}
		return ResponseEntity.ok(results);
	}

	/**
	 * 한 주제의 한 대화 작성
	 */
	@PostMapping("")
	public ResponseEntity<Void> createGatheringDiary(
		@PathVariable(name = "gatheringId") Long gatheringId,
		@PathVariable(name = "exhId") Long exhId,
		@PathVariable(name = "questionId") Long questionId,
		@Valid @RequestBody GatheringDiaryRequest request
	) {
		log.info("[한 주제의 한 대화 작성]");
		// request body 데이터 받아오기
		var command = GatheringDiaryOperationUseCase.GatheringDiaryCreateCommand.builder()
			.gatheringId(gatheringId)
			.exhId(exhId)
			.questionId(questionId)
			.content(request.getContent())
			.writeDate(request.getWriteDate())
			.build();
		// 비즈니스 로직 호출
		gatheringDiaryOperationUseCase.createGatheringDiary(command);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * 한 주제의 한 대화 수정
	 */
	@PatchMapping("/{gatheringDiaryId}")
	public ResponseEntity<Void> updateGatheringExhQuestion(
		@PathVariable(name = "gatheringId") Long gatheringId,
		@PathVariable(name = "exhId") Long exhId,
		@PathVariable(name = "questionId") Long questionId,
		@PathVariable(name = "gatheringDiaryId") Long gatheringDiaryId,
		@Valid @RequestBody GatheringDiaryRequest request
	) {
		log.info("[한 주제의 한 대화 수정]");
		// request body 데이터 받아오기
		var command = GatheringDiaryOperationUseCase.GatheringDiaryUpdateCommand.builder()
			.gatheringId(gatheringId)
			.exhId(exhId)
			.questionId(questionId)
			.gatheringDiaryId(gatheringDiaryId)
			.content(request.getContent())
			.writeDate(request.getWriteDate())
			.build();
		// 비즈니스 로직 호출
		gatheringDiaryOperationUseCase.updateGatheringDiary(command);

		return ResponseEntity.status(HttpStatus.OK).build();
	}

	/**
	 * 한 주제의 한 대화 삭제
	 */
	@DeleteMapping("/{gatheringDiaryId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteDiary(
		@PathVariable(name = "gatheringId") Long gatheringId,
		@PathVariable(name = "exhId") Long exhId,
		@PathVariable(name = "questionId") Long questionId,
		@PathVariable(name = "gatheringDiaryId") Long gatheringDiaryId
	) {
		log.info("[한 주제의 한 대화 삭제]");
		// request body 데이터 받아오기
		var command = GatheringDiaryOperationUseCase.GatheringDiaryDeleteCommand.builder()
			.gatheringId(gatheringId)
			.exhId(exhId)
			.questionId(questionId)
			.gatheringDiaryId(gatheringDiaryId)
			.build();
		// 비즈니스 로직 호출
		gatheringDiaryOperationUseCase.deleteGatheringDiary(command);
	}
}

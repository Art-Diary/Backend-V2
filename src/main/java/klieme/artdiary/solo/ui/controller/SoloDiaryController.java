package klieme.artdiary.solo.ui.controller;

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
import jakarta.validation.constraints.NotEmpty;
import klieme.artdiary.solo.dto.EvalChoiceInfo;
import klieme.artdiary.solo.service.SoloDiaryOperationUseCase;
import klieme.artdiary.solo.service.SoloDiaryReadUseCase;
import klieme.artdiary.solo.ui.request_body.EvalChoiceRequest;
import klieme.artdiary.solo.ui.request_body.SoloDiaryRequest;
import klieme.artdiary.solo.ui.view.SoloDiaryView;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/exh-visits/{visitExhId}")
public class SoloDiaryController {
	private final SoloDiaryOperationUseCase soloDiaryOperationUseCase;
	private final SoloDiaryReadUseCase soloDiaryReadUseCase;

	@Autowired
	public SoloDiaryController(SoloDiaryOperationUseCase soloDiaryOperationUseCase,
		SoloDiaryReadUseCase soloDiaryReadUseCase) {
		this.soloDiaryOperationUseCase = soloDiaryOperationUseCase;
		this.soloDiaryReadUseCase = soloDiaryReadUseCase;
	}

	/**
	 * 기록 목록 조회
	 * "/exh-visits/${visit_exh_id}/diaries"
	 */
	@GetMapping("/diaries")
	public ResponseEntity<SoloDiaryView> getDiaries(@PathVariable(name = "visitExhId") Long visitExhId) {

		log.info("[기록&평가 목록 조회]");

		// 비즈니스 로직 호출
		SoloDiaryReadUseCase.FindSoloDiaryResult soloDiaryResults = soloDiaryReadUseCase.getSoloDiaryList(visitExhId);

		return ResponseEntity.ok(SoloDiaryView.builder().result(soloDiaryResults).build());
	}

	/**
	 *기록 추가
	 * "/exh-visits/${visit_exh_id}/diaries"
	 */
	@PostMapping("/diaries")
	public ResponseEntity<Void> createDiary(
		@PathVariable(name = "visitExhId") Long visitExhId,
		@Valid @RequestBody SoloDiaryRequest request
	) {
		log.info("[기록&평가 추가]");
		// request body 데이터 받아오기
		var command = SoloDiaryOperationUseCase.SoloDiaryCreateUpdateCommand.builder()
			.visitExhId(visitExhId)
			.questionId(request.getQuestionId())
			.answer(request.getAnswer())
			.writeDate(request.getWriteDate())
			.isPublic(request.getIsPublic())
			.build();
		// 비즈니스 로직 호출
		soloDiaryOperationUseCase.createSoloDiary(command);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * 기록 수정
	 * "/exh-visits/{visit_exh_id}/diaries/{solo_diary_id}"
	 */
	@PatchMapping("/diaries/{soloDiaryId}")
	public ResponseEntity<Void> updateMyDiary(
		@PathVariable(name = "visitExhId") Long visitExhId,
		@PathVariable(name = "soloDiaryId") Long soloDiaryId,
		@Valid @RequestBody SoloDiaryRequest request
	) {
		log.info("[기록 수정]");
		// request body 데이터 받아오기
		var command = SoloDiaryOperationUseCase.SoloDiaryCreateUpdateCommand.builder()
			.visitExhId(visitExhId)
			.soloDiaryId(soloDiaryId)
			.questionId(request.getQuestionId())
			.answer(request.getAnswer())
			.writeDate(request.getWriteDate())
			.isPublic(request.getIsPublic())
			.build();
		// 비즈니스 로직 호출
		soloDiaryOperationUseCase.updateSoloDiary(command);

		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@DeleteMapping("/diaries/{soloDiaryId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteDiary(@PathVariable(name = "visitExhId") Long visitExhId,
		@PathVariable(name = "soloDiaryId") Long soloDiaryId) {
		log.info("[기록 삭제]");

		soloDiaryOperationUseCase.deleteSoloDiary(visitExhId, soloDiaryId);
	}

	/**
	 * 평가 수정
	 * "/exh-visits/{visit_exh_id}/evaluations"
	 */
	@PatchMapping("/evaluations")
	public ResponseEntity<Void> updateEvaluations(
		@PathVariable(name = "visitExhId") Long visitExhId,
		@Valid @RequestBody @NotEmpty List<EvalChoiceRequest> request
	) {
		log.info("[평가 수정]");
		for (EvalChoiceRequest request1 : request) {
			System.out.println(request1.getFactorId() + ", " + request1.getOptionId());
		}
		// request body 데이터 받아오기
		var command = SoloDiaryOperationUseCase.EvalChoiceUpdateCommand.builder()
			.visitExhId(visitExhId)
			.evalChoiceInfoList(request.stream()
				.map((req) -> EvalChoiceInfo.builder().factorId(req.getFactorId()).optionId(req.getOptionId()).build())
				.toList())
			.build();
		// 비즈니스 로직 호출
		soloDiaryOperationUseCase.updateEvaluationList(command);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}

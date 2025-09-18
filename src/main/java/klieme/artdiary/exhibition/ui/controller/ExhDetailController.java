package klieme.artdiary.exhibition.ui.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import klieme.artdiary.exhibition.service.ExhDetailReadUseCase;
import klieme.artdiary.exhibition.service.ExhDetailOperationUseCase;
import klieme.artdiary.exhibition.ui.view.SoloDiaryOfExhView;
import klieme.artdiary.exhibition.ui.view.ExhDetailView;
import klieme.artdiary.exhibition.ui.view.StoredDateView;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/exhibitions/{exhId}")
public class ExhDetailController {
	private final ExhDetailOperationUseCase exhDetailOperationUseCase;
	private final ExhDetailReadUseCase exhDetailReadUseCase;

	@Autowired
	public ExhDetailController(ExhDetailOperationUseCase exhDetailOperationUseCase,
		ExhDetailReadUseCase exhDetailReadUseCase) {
		this.exhDetailOperationUseCase = exhDetailOperationUseCase;
		this.exhDetailReadUseCase = exhDetailReadUseCase;
	}

	@GetMapping("")
	public ResponseEntity<ExhDetailView> getExhDetailInfo(@PathVariable(name = "exhId") Long exhId) {
		log.info("[전시회 상세 정보 조회]");

		ExhDetailReadUseCase.FindExhResult result = exhDetailReadUseCase.getExhDetailInfo(exhId);

		return ResponseEntity.ok(ExhDetailView.builder().result(result).build());
	}

	@GetMapping("/diaries")
	public ResponseEntity<List<SoloDiaryOfExhView>> getAllOfExhIdDiaries(@PathVariable(name = "exhId") Long exhId) {
		log.info("[전시회 상세 정보 중 기록 조회]");

		List<ExhDetailReadUseCase.FindSoloDiaryResult> diaryResults = exhDetailReadUseCase.getAllOfExhIdDiaries(exhId);

		List<SoloDiaryOfExhView> result = new ArrayList<>();

		for (ExhDetailReadUseCase.FindSoloDiaryResult diaryResult : diaryResults) {
			result.add(SoloDiaryOfExhView.builder().result(diaryResult).build());
		}
		return ResponseEntity.ok(result);
	}

	@GetMapping("/date") // ResponseEntity<>
	public ResponseEntity<StoredDateView> getStoredDateOfExhsByGatherId(
		@PathVariable(name = "exhId") Long exhId,
		@RequestParam(name = "gatherId", required = false) Long gatherId
	) {
		log.info("[한 전시회에 대해 캘린더에 저장된 날짜 조회]");
		var query = ExhDetailReadUseCase.StoredDateFindQuery.builder()
			.exhId(exhId)
			.gatherId(gatherId)
			.build();
		ExhDetailReadUseCase.FindStoredDateResult result = exhDetailReadUseCase.getStoredDateOfExhsByGatherId(query);

		return ResponseEntity.ok(StoredDateView.builder().result(result).build());
	}

	// @PatchMapping("")
	// public ResponseEntity<ExhDetailView> updateExhDetailInfo(@PathVariable(name = "exhId") Long exhId,
	//     @Valid @ModelAttribute ExhRequest exhRequest) {
	//     log.info("[전시회 상세 정보 업데이트]");
	//
	//     // request body 데이터 받아오기
	//     var command = ExhDetailOperationUseCase.ExhUpdateCommand.builder()
	//        .exhId(exhId)
	//        .exhName(exhRequest.getExhName())
	//        .gallery(exhRequest.getGallery())
	//        .exhPeriodStart(exhRequest.getExhPeriodStart())
	//        .exhPeriodEnd(exhRequest.getExhPeriodEnd())
	//        .painter(exhRequest.getPainter())
	//        .fee(exhRequest.getFee())
	//        .intro(exhRequest.getIntro())
	//        .url(exhRequest.getUrl())
	//        .poster(exhRequest.getPoster())
	//        .art(exhRequest.getArt())
	//        .source(exhRequest.getSource())
	//        .build();
	//     // 비즈니스 로직 호출
	//     ExhDetailReadUseCase.FindExhResult result = exhDetailOperationUseCase.updateExhDetailInfo(command);
	//
	//     return ResponseEntity.ok(ExhDetailView.builder().result(result).build());
	// }
}
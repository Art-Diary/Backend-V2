package klieme.artdiary.exhibition.ui.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import klieme.artdiary.exhibition.service.ExhOperationUseCase;
import klieme.artdiary.exhibition.service.ExhReadUseCase;
import klieme.artdiary.exhibition.ui.request_body.ExhRequest;
import klieme.artdiary.exhibition.ui.view.AllDiaryOfExhIdView;
import klieme.artdiary.exhibition.ui.view.ExhView;
import klieme.artdiary.exhibition.ui.view.StoredDateView;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/exhibitions/{exhId}")
public class ExhDetailController {
	private final ExhOperationUseCase exhOperationUseCase;
	private final ExhReadUseCase exhReadUseCase;

	@Autowired
	public ExhDetailController(ExhOperationUseCase exhOperationUseCase, ExhReadUseCase exhReadUseCase) {
		this.exhOperationUseCase = exhOperationUseCase;
		this.exhReadUseCase = exhReadUseCase;
	}

	@GetMapping("/date") // ResponseEntity<>
	public ResponseEntity<StoredDateView> getStoredDateOfExhsByGatherId(
		@PathVariable(name = "exhId") Long exhId,
		@RequestParam(name = "gatherId", required = false) Long gatherId
	) {
		log.info("[한 전시회에 대해 캘린더에 저장된 날짜 조회]");
		var query = ExhReadUseCase.StoredDateFindQuery.builder()
			.exhId(exhId)
			.gatherId(gatherId)
			.build();
		ExhReadUseCase.FindStoredDateResult result = exhReadUseCase.getStoredDateOfExhsByGatherId(query);

		return ResponseEntity.ok(StoredDateView.builder().result(result).build());
	}

	@GetMapping("")
	public ResponseEntity<ExhView> getExhDetailInfo(@PathVariable(name = "exhId") Long exhId) {
		log.info("[전시회 상세 정보 조회]");

		ExhReadUseCase.FindExhResult result = exhReadUseCase.getExhDetailInfo(exhId);

		return ResponseEntity.ok(ExhView.builder().result(result).build());

	}

	@GetMapping("/diaries")
	public ResponseEntity<List<AllDiaryOfExhIdView>> getAllOfExhIdDiaries(@PathVariable(name = "exhId") Long exhId) {
		log.info("[전시회 상세 정보 중 기록 조회]");

		List<ExhReadUseCase.FindDiaryResult> diaryResults = exhReadUseCase.getAllOfExhIdDiaries(exhId);

		List<AllDiaryOfExhIdView> result = new ArrayList<>();

		for (ExhReadUseCase.FindDiaryResult diaryResult : diaryResults) {
			result.add(AllDiaryOfExhIdView.builder().result(diaryResult).build());
		}
		return ResponseEntity.ok(result);

	}

	@PatchMapping("")
	public ResponseEntity<ExhView> updateExhDetailInfo(@PathVariable(name = "exhId") Long exhId,
		@Valid @ModelAttribute ExhRequest exhRequest) {
		log.info("[전시회 상세 정보 업데이트]");

		// request body 데이터 받아오기
		var command = ExhOperationUseCase.ExhUpdateCommand.builder()
			.exhId(exhId)
			.exhName(exhRequest.getExhName())
			.gallery(exhRequest.getGallery())
			.exhPeriodStart(exhRequest.getExhPeriodStart())
			.exhPeriodEnd(exhRequest.getExhPeriodEnd())
			.painter(exhRequest.getPainter())
			.fee(exhRequest.getFee())
			.intro(exhRequest.getIntro())
			.url(exhRequest.getUrl())
			.poster(exhRequest.getPoster())
			.art(exhRequest.getArt())
			.source(exhRequest.getSource())
			.build();
		// 비즈니스 로직 호출
		ExhReadUseCase.FindExhResult result = exhOperationUseCase.updateExhDetailInfo(command);

		return ResponseEntity.ok(ExhView.builder().result(result).build());
	}
}

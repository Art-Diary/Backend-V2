package klieme.artdiary.gathering.ui.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import klieme.artdiary.gathering.service.GatheringOperationUseCase;
import klieme.artdiary.gathering.service.GatheringReadUseCase;
import klieme.artdiary.gathering.ui.request_body.AddExhDateRequest;
import klieme.artdiary.gathering.ui.request_body.AddGatheringMateRequest;
import klieme.artdiary.gathering.ui.request_body.AddGatheringRequest;
import klieme.artdiary.gathering.ui.view.GatheringDetailInfoView;
import klieme.artdiary.gathering.ui.view.GatheringMateSearchView;
import klieme.artdiary.gathering.ui.view.GatheringMateView;
import klieme.artdiary.gathering.ui.view.GatheringNotVisitExhView;
import klieme.artdiary.gathering.ui.view.GatheringView;
import klieme.artdiary.gathering.ui.view.GatheringVisitExhView;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/gatherings")
public class GatheringController {
	private final GatheringOperationUseCase gatheringOperationUseCase;
	private final GatheringReadUseCase gatheringReadUseCase;

	@Autowired
	public GatheringController(GatheringOperationUseCase gatheringOperationUseCase,
		GatheringReadUseCase gatheringReadUseCase) {
		this.gatheringOperationUseCase = gatheringOperationUseCase;
		this.gatheringReadUseCase = gatheringReadUseCase;
	}

	@PostMapping("")
	public ResponseEntity<GatheringView> createGathering(@Valid @RequestBody AddGatheringRequest request) {
		log.info("[모임 생성]");
		// request body 데이터 받아오기
		var command = GatheringOperationUseCase.GatheringCreateCommand.builder()
			.gatheringName(request.getGatheringName())
			.build();
		// 비즈니스 로직 호출
		GatheringReadUseCase.FindGatheringResult result = gatheringOperationUseCase.createGathering(command);
		// 비즈니스 로직 결과값을 view 형식에 맞춰 반환
		return ResponseEntity.status(HttpStatus.CREATED).body(GatheringView.builder().result(result).build());
	}

	@GetMapping("")
	public ResponseEntity<List<GatheringView>> getGatheringList() {
		log.info("[모임 목록 조회]");

		List<GatheringReadUseCase.FindGatheringResult> results = gatheringReadUseCase.getGatheringList();

		List<GatheringView> viewResult = new ArrayList<>();

		for (GatheringReadUseCase.FindGatheringResult result : results) {
			viewResult.add(GatheringView.builder().result(result).build());
		}
		return ResponseEntity.ok(viewResult);

	}

	/**
	 * 모임 상세 정보 조회(모임 멤버 + 갔다온 전시회 목록)
	 */
	@GetMapping("/{gatheringId}")
	public ResponseEntity<GatheringDetailInfoView> getGatheringDetailInfo(
		@PathVariable(name = "gatheringId") Long gatheringId) {
		log.info("[모임 상세 정보 조회(모임 멤버 + 갔다온 전시회 목록)]");
		// 비즈니스 로직 호출
		GatheringReadUseCase.FindGatheringDetailInfoResult detailInfoResult = gatheringReadUseCase.getGatheringDetailInfo(
			gatheringId);
		// 반환
		return ResponseEntity.ok(GatheringDetailInfoView.builder().result(detailInfoResult).build());
	}

	/**
	 * 모임 메이트 추가할 때 닉네임 검색
	 */
	@GetMapping("/{gatheringId}/search")
	public ResponseEntity<GatheringMateSearchView> searchUserForGathering(
		@PathVariable(name = "gatheringId") Long gatheringId,
		@RequestParam(name = "nickname") String nickname
	) {
		log.info("[모임 메이트 추가할 때 닉네임 검색]");
		var query = GatheringReadUseCase.GatheringNicknameFindQuery.builder()
			.gatheringId(gatheringId)
			.nickname(nickname)
			.build();
		// 비즈니스 로직 호출
		GatheringReadUseCase.FindIsGatheringMemberResult result = gatheringReadUseCase.searchNicknameNotInGathering(
			query);

		return ResponseEntity.ok(GatheringMateSearchView.builder().result(result).build());
	}

	/**
	 * 모임 메이트 추가
	 */
	@PostMapping("/{gatheringId}")
	public ResponseEntity<List<GatheringMateView>> addGatheringMate(
		@PathVariable(name = "gatheringId") Long gatheringId,
		@Valid @RequestBody AddGatheringMateRequest request
	) {
		log.info("[모임 메이트 추가]");
		var command = GatheringOperationUseCase.GatheringMateCreateCommand.builder()
			.gatheringId(gatheringId)
			.userId(request.getUserId())
			.build();
		// 비즈니스 로직 호출
		List<GatheringReadUseCase.FindGatheringMemberResult> results = gatheringOperationUseCase.addGatheringMate(
			command);
		// 비즈니스 로직 결과값을 view 형식에 맞춰 list로 반환
		List<GatheringMateView> viewList = new ArrayList<>();

		for (GatheringReadUseCase.FindGatheringMemberResult result : results) {
			viewList.add(GatheringMateView.builder().result(result).build());
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(viewList);
	}

	/**
	 * 모임 나가기
	 */
	@DeleteMapping("/{gatheringId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteGathering(@PathVariable(name = "gatheringId") Long gatheringId) {
		log.info("[모임 나가기]");
		gatheringOperationUseCase.deleteMyGathering(gatheringId);
	}

	/**
	 * 모임의 일정에 전시회 관람 날짜 조회
	 */
	@GetMapping("/{gatheringId}/exh-visits")
	public ResponseEntity<List<GatheringVisitExhView>> getGatheringVisitDateList(
		@PathVariable(name = "gatheringId") Long gatheringId,
		@RequestParam(name = "year") Integer year,
		@RequestParam(name = "month") Integer month) {
		log.info("[모임의 일정에 전시회 관람 날짜 조회]");
		var query = GatheringReadUseCase.GatheringVisitExhFindQuery.builder()
			.gatheringId(gatheringId)
			.year(year)
			.month(month)
			.build();
		// 비즈니스 로직 호출
		List<GatheringReadUseCase.FindGatheringVisitExhResult> results = gatheringReadUseCase.getGatheringVisitDateList(
			query);
		List<GatheringVisitExhView> viewResult = new ArrayList<>();

		for (GatheringReadUseCase.FindGatheringVisitExhResult result : results) {
			viewResult.add(GatheringVisitExhView.builder().result(result).build());
		}
		return ResponseEntity.ok(viewResult);
	}

	/**
	 * 모임이 특정 날짜에 방문하지 않은 전시회 목록 조회
	 */
	@GetMapping("/{gatheringId}/exh-visits/date")
	public ResponseEntity<List<GatheringNotVisitExhView>> getGatheringNotVisitedExhListWithDate(
		@PathVariable(name = "gatheringId") Long gatheringId,
		@NotNull @RequestParam(name = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
		log.info("[모임이 특정 날짜에 방문하지 않은 전시회 목록 조회]");
		System.out.println("date: " + date);

		var query = GatheringReadUseCase.GatheringNotVisitExhFindQuery.builder()
			.gatheringId(gatheringId)
			.date(date)
			.build();
		// 비즈니스 로직 호출
		List<GatheringReadUseCase.FindGatheringNotVisitExhResult> results = gatheringReadUseCase.getGatheringNotVisitedExhListWithDate(
			query);
		List<GatheringNotVisitExhView> viewResult = new ArrayList<>();

		for (GatheringReadUseCase.FindGatheringNotVisitExhResult result : results) {
			viewResult.add(GatheringNotVisitExhView.builder().result(result).build());
		}
		return ResponseEntity.ok(viewResult);
	}

	/**
	 * 모임의 일정에 전시회 관람 날짜 추가
	 */
	@PostMapping("/{gatheringId}/exh-visits")
	public ResponseEntity<Void> addExhAboutGathering(
		@PathVariable(name = "gatheringId") Long gatheringId,
		@Valid @RequestBody AddExhDateRequest request
	) {
		log.info("[모임의 일정에 전시회 관람 날짜 추가]");
		// request body 데이터 받아오기
		var command = GatheringOperationUseCase.ExhGatheringCreateCommand.builder()
			.gatheringId(gatheringId)
			.exhId(request.getExhId())
			.visitDate(request.getVisitDate())
			.build();

		// 비즈니스 로직 호출
		gatheringOperationUseCase.addExhAboutGathering(command);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}

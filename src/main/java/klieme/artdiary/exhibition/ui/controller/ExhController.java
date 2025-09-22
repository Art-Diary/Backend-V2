package klieme.artdiary.exhibition.ui.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import klieme.artdiary.common.api.ArtDiaryException;
import klieme.artdiary.common.api.MessageType;
import klieme.artdiary.exhibition.enums.ExhField;
import klieme.artdiary.exhibition.enums.ExhPrice;
import klieme.artdiary.exhibition.enums.ExhState;
import klieme.artdiary.exhibition.service.ExhReadUseCase;
import klieme.artdiary.exhibition.ui.view.LiteExhInfoView;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/exhibitions")
public class ExhController {
	private final ExhReadUseCase exhReadUseCase;

	@Autowired
	public ExhController(ExhReadUseCase exhReadUseCase) {
		this.exhReadUseCase = exhReadUseCase;
	}

	@GetMapping("")
	public ResponseEntity<List<LiteExhInfoView>> getExhList(
		@RequestParam(name = "keyword", required = false) String keyword, //검색 내용
		@RequestParam(name = "field", required = false) String[] fieldList, // 전시 분야
		@RequestParam(name = "price", required = false) String price, // 가격
		@RequestParam(name = "state", required = false) String[] stateList, // 전시 오픈 상태
		@RequestParam(name = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date // 날짜
	) {
		log.info("[전시회 목록 조회(+전시회 목록 조회, 좋아요 조회)]");

		// string 자료형을 갖는 변수일 경우 빈 문자열인지 확인
		boolean invalidKeyword = keyword != null && keyword.isBlank();
		boolean invalidFieldList = fieldList != null && fieldList.length == 0;
		boolean invalidPrice = price != null && price.isBlank();
		boolean invalidStateList = stateList != null && stateList.length == 0;
		boolean invalidStateDate = stateList != null && date != null;

		if (invalidKeyword || invalidFieldList || invalidPrice || invalidStateList || invalidStateDate) {
			throw new ArtDiaryException(MessageType.BAD_REQUEST);
		}
		// field 정해진 값이 들어왔는지 확인
		List<ExhField> fields = new ArrayList<>();
		if (fieldList != null) {
			for (String field : fieldList) {
				if (ExhField.valueOfLabel(field) == null) {
					throw new ArtDiaryException(MessageType.BAD_REQUEST);
				}
				fields.add(ExhField.valueOfLabel(field));
			}
		}
		// state 정해진 값이 들어왔는지 확인
		List<ExhState> states = new ArrayList<>();
		if (stateList != null) {
			for (String state : stateList) {
				if (ExhState.valueOfLabel(state) == null) {
					throw new ArtDiaryException(MessageType.BAD_REQUEST);
				}
				states.add(ExhState.valueOfLabel(state));
			}
		}
		// price 정해진 값이 들어왔는지 확인
		if ((price != null && ExhPrice.valueOfLabel(price) == null)) {
			throw new ArtDiaryException(MessageType.BAD_REQUEST);
		}
		// 비즈니스 로직 호출
		var query = ExhReadUseCase.ExhListFindQuery.builder()
			.keyword(keyword)
			.fieldList(fields)
			.stateList(states)
			.price(ExhPrice.valueOfLabel(price))
			.date(date)
			.build();
		List<ExhReadUseCase.FindLiteExhInfoResult> exhResults = exhReadUseCase.getExhList(query);
		// 비즈니스 로직 결과값을 view 형식에 맞춰 list로 반환
		List<LiteExhInfoView> result = new ArrayList<>();

		for (ExhReadUseCase.FindLiteExhInfoResult exhResult : exhResults) {
			result.add(LiteExhInfoView.builder().result(exhResult).build());
		}
		return ResponseEntity.ok(result);
	}

	@GetMapping("/date")
	public ResponseEntity<List<LiteExhInfoView>> getNotVisitedExhListWithDate(
		@NotNull @RequestParam(name = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
		log.info("[특정 날짜에 방문하지 않은 전시회 목록 조회]");

		// 비즈니스 로직 호출
		var query = ExhReadUseCase.ExhListFindQuery.builder().date(date).build();
		List<ExhReadUseCase.FindLiteExhInfoResult> exhResults = exhReadUseCase.getNotVisitedExhListWithDate(query);
		// 비즈니스 로직 결과값을 view 형식에 맞춰 list로 반환
		List<LiteExhInfoView> result = new ArrayList<>();

		for (ExhReadUseCase.FindLiteExhInfoResult exhResult : exhResults) {
			result.add(LiteExhInfoView.builder().result(exhResult).build());
		}
		return ResponseEntity.ok(result);
	}

	@GetMapping("/search")
	public ResponseEntity<List<LiteExhInfoView>> getExhListBySearchName(
		@NotBlank @RequestParam(name = "searchName") String searchName) {

		log.info("[전시회 이름 검색 결과 조회]");

		// 비즈니스 로직 호출
		List<ExhReadUseCase.FindLiteExhInfoResult> exhResults = exhReadUseCase.getExhListBySearchName(
			searchName);
		// 비즈니스 로직 결과값을 view 형식에 맞춰 list로 반환
		List<LiteExhInfoView> result = new ArrayList<>();

		for (ExhReadUseCase.FindLiteExhInfoResult exhResult : exhResults) {
			result.add(LiteExhInfoView.builder().result(exhResult).build());
		}
		return ResponseEntity.ok(result);
	}
}
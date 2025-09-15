package klieme.artdiary.solo.ui.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import klieme.artdiary.solo.service.MyExhReadUseCase;
import klieme.artdiary.solo.ui.view.MyVisitExhsView;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/exh-visits")
public class MyExhController {
	private final MyExhReadUseCase myExhReadUseCase;

	@Autowired
	public MyExhController(MyExhReadUseCase myExhReadUseCase) {
		this.myExhReadUseCase = myExhReadUseCase;
	}

	/**
	 * 내 기록의 전시회 목록 조회
	 * "/myexhs"
	 */
	@GetMapping("")
	public ResponseEntity<List<MyVisitExhsView>> getMyExhsList() throws IOException {
		log.info("[내 기록의 전시회 목록 조회]");

		List<MyExhReadUseCase.FindMyVisitExhsResult> results = myExhReadUseCase.getMyVisitExhsList();
		List<MyVisitExhsView> viewResult = new ArrayList<>();

		results.forEach((res) -> {
			viewResult.add(MyVisitExhsView.builder().result(res).build());
		});
		return ResponseEntity.ok(viewResult);
	}
}

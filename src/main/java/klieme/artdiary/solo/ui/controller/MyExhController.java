package klieme.artdiary.solo.ui.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import klieme.artdiary.solo.service.MyExhOperationUseCase;
import klieme.artdiary.solo.service.MyExhReadUseCase;
import klieme.artdiary.solo.ui.request_body.CreateVisitExhRequest;
import klieme.artdiary.solo.ui.view.MyVisitExhsView;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/exh-visits")
public class MyExhController {
	private final MyExhReadUseCase myExhReadUseCase;
	private final MyExhOperationUseCase myExhOperationUseCase;

	@Autowired
	public MyExhController(MyExhReadUseCase myExhReadUseCase, MyExhOperationUseCase myExhOperationUseCase) {
		this.myExhReadUseCase = myExhReadUseCase;
		this.myExhOperationUseCase = myExhOperationUseCase;
	}

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

	@PostMapping("/{exhId}/date")
	public ResponseEntity<Void> createVisitExh(@PathVariable(name = "exhId") Long exhId,
		@Valid @RequestBody CreateVisitExhRequest request) {
		log.info("[전시회 방문 날짜 추가]");

		var command = MyExhOperationUseCase.VisitExhCreateCommand.builder()
			.exhId(exhId)
			.visitDate(request.getVisitDate())
			.build();

		myExhOperationUseCase.createVisitExh(command);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}

package klieme.artdiary.like_exh.ui.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import klieme.artdiary.like_exh.service.LikeExhOperationUseCase;
import klieme.artdiary.like_exh.service.LikeExhReadUseCase;
import klieme.artdiary.like_exh.ui.request_body.DeleteLikeExhsRequest;
import klieme.artdiary.like_exh.ui.request_body.LikeExhRequest;
import klieme.artdiary.like_exh.ui.view.LikeExhView;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/favorites")
public class LikeExhController {
	private final LikeExhOperationUseCase likeExhOperationUseCase;
	private final LikeExhReadUseCase likeExhReadUseCase;

	@Autowired
	public LikeExhController(LikeExhOperationUseCase likeExhOperationUseCase,
		LikeExhReadUseCase likeExhReadUseCase) {
		this.likeExhOperationUseCase = likeExhOperationUseCase;
		this.likeExhReadUseCase = likeExhReadUseCase;
	}

	/**
	 * 좋아요 전시회 목록 조회
	 * "/favorites"
	 */
	@GetMapping("")
	public ResponseEntity<List<LikeExhView>> getFavoriteExhList() {
		log.info("[좋아요 전시회 목록 조회]");

		List<LikeExhReadUseCase.FindLikeExhResult> results = likeExhReadUseCase.getLikeExhs();

		List<LikeExhView> viewResult = new ArrayList<>();

		for (LikeExhReadUseCase.FindLikeExhResult result : results) {
			viewResult.add(LikeExhView.builder().result(result).build());
		}

		return ResponseEntity.ok(viewResult);
	}

	@PostMapping("/like")
	public ResponseEntity<LikeExhView> createFavoriteExh(@Valid @RequestBody LikeExhRequest request) {
		log.info("[전시회 좋아요 생성 클릭]");
		// request data 저장
		var command = LikeExhOperationUseCase.LikeExhCreateCommand.builder()
			.exhId(request.getExhId())
			.build();
		// 비즈니스 로직 호출
		LikeExhReadUseCase.FindLikeExhResult result = likeExhOperationUseCase.createLikeExh(command);
		return ResponseEntity.created(null).body(LikeExhView.builder().result(result).build());
	}

	@PostMapping("/unlike")
	public void deleteFavoriteExh(@Valid @RequestBody DeleteLikeExhsRequest deleterequests) {
		log.info("[전시회 좋아요 해제 클릭]");

		List<LikeExhOperationUseCase.LikeExhCreateCommand> commands = new ArrayList<>();
		List<Long> requests = deleterequests.getFavoriteExhsList();

		//List<FavoriteExhRequest> requests=deleterequests.getFavoriteExhsList();
		for (Long request : requests) {
			var command = LikeExhOperationUseCase.LikeExhCreateCommand.builder()
				.exhId(request)
				.build();
			commands.add(command);
		}

		likeExhOperationUseCase.deleteLikeExh(commands);
	}
}

package klieme.artdiary.solo.service;

import static klieme.artdiary.common.SecurityUtil.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.record_data_access.repository.VisitExhRepository;

@Service
public class MyExhService implements MyExhReadUseCase {
	private final VisitExhRepository visitExhRepository;

	@Autowired
	public MyExhService(VisitExhRepository visitExhRepository) {
		this.visitExhRepository = visitExhRepository;
	}

	@Override
	public List<FindMyVisitExhsResult> getMyVisitExhsList() {
		/*
		사용자가 방문한 전시회 정보와 함께 가져오기
		*/
		Long userId = getUserId();
		List<Map<String, Object>> visitExhList = visitExhRepository.getVisitExhListWithExhInfo(userId);
		List<FindMyVisitExhsResult> result = new ArrayList<>();

		for (Map<String, Object> info : visitExhList) {
			ExhEntity exhibition = (ExhEntity)info.get("exhibition");
			LocalDate visitDate = (LocalDate)info.get("visitDate");

			result.add(FindMyVisitExhsResult.findMyVisitExhs(exhibition, visitDate));
		}
		return result;
	}

	private Long getUserId() {
		return getCurrentUserEntity().getUserId();
	}
}

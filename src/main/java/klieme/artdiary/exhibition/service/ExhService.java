package klieme.artdiary.exhibition.service;

import static klieme.artdiary.common.SecurityUtil.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.exhibition.data_access.repository.ExhRepository;

@Service
public class ExhService implements ExhReadUseCase {
	private final ExhRepository exhRepository;

	@Autowired
	public ExhService(ExhRepository exhRepository) {
		this.exhRepository = exhRepository;
	}

	@Override
	public List<FindLiteExhInfoResult> getExhList(ExhListFindQuery query) {
		/* api 요청 옵션에 전시회 진행 상황이 포함되어있으면 해당 진행 상황 적용.
		 * 옵션에 진행 상황이 없으면 "현재 진행 중"인 전시회 적용.
		 */
		/* 전시회 리스트 고정 순서
		 * 1. 좋아요 많은 순
		 * 2. 최근에 시작한 순
		 * */
		List<FindLiteExhInfoResult> results = new ArrayList<>();
		List<Map<String, Object>> infoList = exhRepository.searchExhList(query.getKeyword(), query.getFieldList(),
			query.getPrice(), query.getStateList(), query.getDate(), getUserId());

		for (Map<String, Object> info : infoList) {
			ExhEntity exhibition = (ExhEntity)info.get("exhibition");
			Boolean isLikeExh = (Boolean)info.get("isLikeExh");

			results.add(FindLiteExhInfoResult.findByExhWithLike(exhibition, isLikeExh));
		}
		return results;
	}

	@Override
	public List<FindLiteExhInfoResult> getNotVisitedExhListWithDate(ExhListFindQuery query) {
		List<FindLiteExhInfoResult> results = new ArrayList<>();
		List<ExhEntity> infoList = exhRepository.getNotVisitedExhListWithDate(query.getDate(), getUserId());

		for (ExhEntity exhibition : infoList) {
			results.add(FindLiteExhInfoResult.findByNotVisitedExh(exhibition));
		}
		return results;
	}

	@Override
	public List<FindLiteExhInfoResult> getExhListBySearchName(String searchName) {

		List<FindLiteExhInfoResult> results = new ArrayList<>();
		List<Map<String, Object>> infoList = exhRepository.searchExhListBySearchName(searchName, getUserId());

		for (Map<String, Object> info : infoList) {
			ExhEntity exhibition = (ExhEntity)info.get("exhibition");
			Integer haveFavoriteByUser = (Integer)info.get("haveFavoriteByUser");

			results.add(FindLiteExhInfoResult.findByExhWithLike(exhibition, haveFavoriteByUser == 1));
		}
		return results;
	}

	private Long getUserId() {
		return getCurrentUserEntity().getUserId();
	}
}

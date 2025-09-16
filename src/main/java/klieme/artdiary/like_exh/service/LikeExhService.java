package klieme.artdiary.like_exh.service;

import static klieme.artdiary.common.SecurityUtil.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import klieme.artdiary.common.api.ArtDiaryException;
import klieme.artdiary.common.api.MessageType;
import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.exhibition.data_access.repository.ExhRepository;
import klieme.artdiary.like_exh.data_access.entity.LikeExhEntity;
import klieme.artdiary.like_exh.data_access.entity.LikeExhId;
import klieme.artdiary.like_exh.data_access.repository.LikeExhRepository;

@Service
public class LikeExhService implements LikeExhOperationUseCase, LikeExhReadUseCase {
	private final ExhRepository exhRepository;
	private final LikeExhRepository likeExhRepository;

	@Autowired
	public LikeExhService(ExhRepository exhRepository, LikeExhRepository likeExhRepository) {
		this.exhRepository = exhRepository;
		this.likeExhRepository = likeExhRepository;
	}

	@Override
	@Transactional
	public FindLikeExhResult createLikeExh(LikeExhCreateCommand command) {
		// 전시회가 있는지 확인
		ExhEntity exhEntity = exhRepository.findByExhId(command.getExhId()).orElseThrow(() -> new ArtDiaryException(
			MessageType.NOT_FOUND));
		// 이미 저장한 전시회인지 확인
		Optional<LikeExhEntity> savedFavoriteExh = likeExhRepository.findByLikeExhId(LikeExhId.builder()
			.userId(getUserId())
			.exhId(exhEntity.getExhId())
			.build());

		if (savedFavoriteExh.isPresent()) {
			throw new ArtDiaryException(MessageType.CONFLICT);
		}
		// 없으면 저장
		LikeExhEntity favoriteExh = LikeExhEntity.builder()
			.likeExhId(LikeExhId.builder()
				.userId(getUserId())
				.exhId(exhEntity.getExhId())
				.build())
			.initDate(LocalDateTime.now())
			.build();
		likeExhRepository.save(favoriteExh);
		return FindLikeExhResult.findByLikeExh(favoriteExh);
	}

	@Override
	public List<FindLikeExhResult> getLikeExhs() {

		List<FindLikeExhResult> favorites = new ArrayList<>();
		//favoriteExh에서 userId에 해당하는 exhId 알아내기
		List<ExhEntity> exhEntityList = likeExhRepository.getLikeExhByUserId(getUserId());

		for (ExhEntity exh : exhEntityList) {
			favorites.add(FindLikeExhResult.findByLikeExhDetail(exh));
		}
		return favorites;
	}

	@Override
	@Transactional
	public void deleteLikeExh(List<LikeExhCreateCommand> commands) {

		for (LikeExhCreateCommand command : commands) {

			LikeExhId likeExhId = LikeExhId.builder()
				.userId(getUserId())
				.exhId(command.getExhId())
				.build();
			LikeExhEntity fEntity = likeExhRepository.findByLikeExhId(likeExhId)
				.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
			likeExhRepository.delete(fEntity);
		}
	}

	private Long getUserId() {
		return getCurrentUserEntity().getUserId();
	}
}

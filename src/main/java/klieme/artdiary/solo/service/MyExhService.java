package klieme.artdiary.solo.service;

import static klieme.artdiary.common.SecurityUtil.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import klieme.artdiary.common.api.ArtDiaryException;
import klieme.artdiary.common.api.MessageType;
import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.exhibition.data_access.repository.ExhRepository;
import klieme.artdiary.record_data_access.entity.VisitExhEntity;
import klieme.artdiary.record_data_access.repository.VisitExhRepository;
import klieme.artdiary.solo.data_access.entity.UserExhPresenceEntity;
import klieme.artdiary.solo.data_access.entity.UserExhPresenceId;
import klieme.artdiary.solo.data_access.repository.UserExhPresenceRepository;

@Service
public class MyExhService implements MyExhReadUseCase, MyExhOperationUseCase {
	private final VisitExhRepository visitExhRepository;
	private final ExhRepository exhRepository;
	private final UserExhPresenceRepository userExhPresenceRepository;

	@Autowired
	public MyExhService(VisitExhRepository visitExhRepository, ExhRepository exhRepository,
		UserExhPresenceRepository userExhPresenceRepository) {
		this.visitExhRepository = visitExhRepository;
		this.exhRepository = exhRepository;
		this.userExhPresenceRepository = userExhPresenceRepository;
	}

	@Override
	public List<FindMyVisitExhsResult> getMyVisitExhsList() {
		/*
		사용자가 방문한 전시회 정보와 함께 가져오기
		*/
		Long userId = getUserId();
		List<Map<String, Object>> visitExhList = visitExhRepository.getSoloVisitExhListWithExhInfo(userId);
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

	@Transactional
	@Override
	public void createVisitExh(VisitExhCreateCommand command) {
		Long userId = getUserId();

		// 전시회가 해당 날짜에 진행 중인지 확인
		ExhEntity storedExhEntity = exhRepository.findByExhId(command.getExhId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

		if (storedExhEntity.getStartDate().isAfter(command.getVisitDate())
			|| storedExhEntity.getEndDate().isBefore(command.getVisitDate())) {
			throw new ArtDiaryException(MessageType.FORBIDDEN_DATE);
		}
		// visitexh에 추가 - 이미 존재하는지 확인
		Boolean isExistedVisitExh = visitExhRepository.existsByExhIdAndUserIdAndGatheringIdAndVisitDate(
			command.getExhId(), userId, null, command.getVisitDate());

		if (!isExistedVisitExh) {
			VisitExhEntity visitExh = VisitExhEntity.builder()
				.exhId(command.getExhId())
				.userId(userId)
				.gatheringId(null)
				.visitDate(command.getVisitDate())
				.build();
			visitExhRepository.save(visitExh);
		}
		// userexhpresence에 추가 - 이미 존재하는지 확인
		Boolean isPresence = userExhPresenceRepository.existsByUserExhPresenceId(
			UserExhPresenceId.builder().userId(userId).exhId(command.getExhId()).build());

		if (!isPresence) {
			UserExhPresenceEntity userExhPresence = UserExhPresenceEntity.builder()
				.userExhPresenceId(UserExhPresenceId.builder().userId(userId).exhId(command.getExhId()).build())
				.build();

			userExhPresenceRepository.save(userExhPresence);
		}
	}
}

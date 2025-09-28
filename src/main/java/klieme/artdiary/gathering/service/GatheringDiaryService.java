package klieme.artdiary.gathering.service;

import static klieme.artdiary.common.SecurityUtil.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import klieme.artdiary.common.api.ArtDiaryException;
import klieme.artdiary.common.api.MessageType;
import klieme.artdiary.gathering.data_access.entity.GatheringDiaryEntity;
import klieme.artdiary.gathering.data_access.entity.GatheringMemberEntity;
import klieme.artdiary.gathering.data_access.entity.GatheringMemberId;
import klieme.artdiary.gathering.data_access.repository.GatheringDiaryRepository;
import klieme.artdiary.gathering.data_access.repository.GatheringMemberRepository;
import klieme.artdiary.gathering.data_access.repository.GatheringQuestionRepository;
import klieme.artdiary.user.data_access.entity.UserEntity;

@Service
public class GatheringDiaryService implements GatheringDiaryOperationUseCase, GatheringDiaryReadUseCase {
	private final GatheringMemberRepository gatheringMemberRepository;
	private final GatheringDiaryRepository gatheringDiaryRepository;
	private final GatheringQuestionRepository gatheringQuestionRepository;

	@Autowired
	public GatheringDiaryService(GatheringMemberRepository gatheringMemberRepository,
		GatheringDiaryRepository gatheringDiaryRepository, GatheringQuestionRepository gatheringQuestionRepository) {
		this.gatheringMemberRepository = gatheringMemberRepository;
		this.gatheringDiaryRepository = gatheringDiaryRepository;
		this.gatheringQuestionRepository = gatheringQuestionRepository;
	}

	@Override
	public List<FindGatheringDiaryResult> getGatheringDiaryList(GatheringDiaryFindQuery query) {
		// 모임에 있는지 확인
		if (!isUserInGathering(query.getGatheringId())) {
			throw new ArtDiaryException(MessageType.NOT_FOUND);
		}
		// 대화 목록 가져오기 (questionId, gatheringId, exhId) 사용해서 가져오기, 오래된 순으로 가져오기
		List<Map<String, Object>> gatheringDiaryListWithUser = gatheringDiaryRepository.getGatheringDiaryListWithUser(
			query.getGatheringId(), query.getExhId(), query.getQuestionId());
		List<FindGatheringDiaryResult> results = new ArrayList<>();

		for (Map<String, Object> diaryWithUser : gatheringDiaryListWithUser) {
			GatheringDiaryEntity gatheringDiary = (GatheringDiaryEntity)diaryWithUser.get("gatheringDiary");
			UserEntity user = (UserEntity)diaryWithUser.get("user");

			results.add(FindGatheringDiaryResult.of(gatheringDiary, user));
		}
		return results;
	}

	@Transactional
	@Override
	public void createGatheringDiary(GatheringDiaryCreateCommand command) {
		// 모임에 있는지 확인
		if (!isUserInGathering(command.getGatheringId())) {
			throw new ArtDiaryException(MessageType.NOT_FOUND);
		}
		// 질문이 있는지 확인
		gatheringQuestionRepository.findByGatheringQuestionIdAndGatheringIdAndExhId(command.getQuestionId(),
				command.getGatheringId(), command.getExhId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		// 대화 넣기
		GatheringDiaryEntity newGatheringDiary = GatheringDiaryEntity.builder()
			.gatheringQuestionId(command.getQuestionId())
			.userId(getUserId())
			.gatheringId(command.getGatheringId())
			.exhId(command.getExhId())
			.content(command.getContent())
			.writeDate(command.getWriteDate())
			.build();
		gatheringDiaryRepository.save(newGatheringDiary);
	}

	@Transactional
	@Override
	public void updateGatheringDiary(GatheringDiaryUpdateCommand command) {
		// 모임에 있는지 확인
		if (!isUserInGathering(command.getGatheringId())) {
			throw new ArtDiaryException(MessageType.NOT_FOUND);
		}
		// 질문이 있는지 확인
		gatheringQuestionRepository.findByGatheringQuestionIdAndGatheringIdAndExhId(command.getQuestionId(),
				command.getGatheringId(), command.getExhId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		// 대화 넣기
		GatheringDiaryEntity gatheringDiary = gatheringDiaryRepository.findByGatheringDiaryIdAndGatheringQuestionIdAndUserIdAndGatheringIdAndExhId(
			command.getGatheringDiaryId(), command.getQuestionId(), getUserId(), command.getGatheringId(),
			command.getExhId()).orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

		gatheringDiary.updateGatheringDiary(command.getContent(), command.getWriteDate());

		gatheringDiaryRepository.save(gatheringDiary);
	}

	@Override
	public void deleteGatheringDiary(GatheringDiaryDeleteCommand command) {
		// 모임에 있는지 확인
		if (!isUserInGathering(command.getGatheringId())) {
			throw new ArtDiaryException(MessageType.NOT_FOUND);
		}
		// 질문이 있는지 확인
		gatheringQuestionRepository.findByGatheringQuestionIdAndGatheringIdAndExhId(command.getQuestionId(),
				command.getGatheringId(), command.getExhId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		// 대화 찾기
		GatheringDiaryEntity gatheringDiary = gatheringDiaryRepository.findByGatheringDiaryIdAndGatheringQuestionIdAndUserIdAndGatheringIdAndExhId(
			command.getGatheringDiaryId(), command.getQuestionId(), getUserId(), command.getGatheringId(),
			command.getExhId()).orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

		gatheringDiaryRepository.delete(gatheringDiary);
	}

	private Long getUserId() {
		return getCurrentUserEntity().getUserId();
	}

	private Boolean isUserInGathering(Long gatheringId) {
		Long userId = getUserId();
		// 모임에 속해 있는지 확인
		Optional<GatheringMemberEntity> member = gatheringMemberRepository.findByGatheringMemberId(
			GatheringMemberId.builder().gatheringId(gatheringId).userId(userId).build());

		return member.isPresent();
	}
}

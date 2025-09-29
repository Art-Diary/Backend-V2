package klieme.artdiary.gathering.service;

import static klieme.artdiary.common.SecurityUtil.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import klieme.artdiary.common.api.ArtDiaryException;
import klieme.artdiary.common.api.MessageType;
import klieme.artdiary.gathering.data_access.entity.GatheringMemberEntity;
import klieme.artdiary.gathering.data_access.entity.GatheringMemberId;
import klieme.artdiary.gathering.data_access.entity.GatheringQuestionEntity;
import klieme.artdiary.gathering.data_access.repository.GatheringMemberRepository;
import klieme.artdiary.gathering.data_access.repository.GatheringQuestionRepository;

@Service
public class GatheringQuestionService implements GatheringQuestionOperationUseCase, GatheringQuestionReadUseCase {
	private final GatheringMemberRepository gatheringMemberRepository;
	private final GatheringQuestionRepository gatheringQuestionRepository;

	@Autowired
	public GatheringQuestionService(GatheringMemberRepository gatheringMemberRepository,
		GatheringQuestionRepository gatheringQuestionRepository) {
		this.gatheringMemberRepository = gatheringMemberRepository;
		this.gatheringQuestionRepository = gatheringQuestionRepository;
	}

	@Override
	public List<FindGatheringQuestionResult> getGatheringExhQuestionList(GatheringQuestionFindQuery query) {
		// 모임에 있는지 확인
		if (!isUserInGathering(query.getGatheringId())) {
			throw new ArtDiaryException(MessageType.NOT_FOUND);
		}
		// 질문 리스트 가져오기 (gatheringId, exhId) 사용해서 가져오기 ,questionId 순으로 정렬
		List<GatheringQuestionEntity> gatheringQuestionList = gatheringQuestionRepository.findByGatheringIdAndExhIdOrderByGatheringQuestionIdDesc(
			query.getGatheringId(), query.getExhId());
		List<FindGatheringQuestionResult> results = new ArrayList<>();

		for (GatheringQuestionEntity question : gatheringQuestionList) {
			results.add(FindGatheringQuestionResult.of(question));
		}
		return results;
	}

	@Transactional
	@Override
	public void createGatheringExhQuestion(GatheringQuestionCreateCommand command) {
		// 모임에 있는지 확인
		if (!isUserInGathering(command.getGatheringId())) {
			throw new ArtDiaryException(MessageType.NOT_FOUND);
		}
		GatheringQuestionEntity gatheringQuestion = GatheringQuestionEntity.builder()
			.gatheringId(command.getGatheringId())
			.exhId(command.getExhId())
			.questionText(command.getQuestionText())
			.build();
		gatheringQuestionRepository.save(gatheringQuestion);
	}

	@Transactional
	@Override
	public void updateGatheringExhQuestion(GatheringQuestionUpdateCommand command) {
		// 모임에 있는지 확인
		if (!isUserInGathering(command.getGatheringId())) {
			throw new ArtDiaryException(MessageType.NOT_FOUND);
		}
		// question text 수정
		GatheringQuestionEntity gatheringQuestion = gatheringQuestionRepository.findByGatheringQuestionIdAndGatheringIdAndExhId(
				command.getQuestionId(),
				command.getGatheringId(), command.getExhId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

		gatheringQuestion.updateQuestionText(command.getQuestionText());
		gatheringQuestionRepository.save(gatheringQuestion);
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

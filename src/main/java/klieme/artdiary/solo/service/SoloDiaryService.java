package klieme.artdiary.solo.service;

import static klieme.artdiary.common.SecurityUtil.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import klieme.artdiary.common.api.ArtDiaryException;
import klieme.artdiary.common.api.MessageType;
import klieme.artdiary.record_data_access.repository.VisitExhRepository;
import klieme.artdiary.solo.data_access.entity.QuestionEntity;
import klieme.artdiary.solo.data_access.entity.SoloDiaryEntity;
import klieme.artdiary.solo.data_access.repository.QuestionRepository;
import klieme.artdiary.solo.data_access.repository.SoloDiaryRepository;

@Service
public class SoloDiaryService implements SoloDiaryOperationUseCase, SoloDiaryReadUseCase {
	private final SoloDiaryRepository soloDiaryRepository;
	private final VisitExhRepository visitExhRepository;
	private final QuestionRepository questionRepository;

	@Autowired
	public SoloDiaryService(SoloDiaryRepository soloDiaryRepository, VisitExhRepository visitExhRepository,
		QuestionRepository questionRepository) {
		this.soloDiaryRepository = soloDiaryRepository;
		this.visitExhRepository = visitExhRepository;
		this.questionRepository = questionRepository;
	}

	@Override
	public List<FindSoloDiaryResult> getMyDiaries(Long visitExhId) {
		Long userId = getUserId();
		// visitExh의 userId 확인
		visitExhRepository.findByVisitExhIdAndUserId(visitExhId, userId)
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

		// [TODO] 평가

		// soloDiary에서 visitExhId에 해당하는 것 모두 가져오기 - question 필요
		List<Map<String, Object>> diaryListWithQuestion = soloDiaryRepository.getSoloDiaryListWithQuestion(visitExhId);
		List<FindSoloDiaryResult> result = new ArrayList<>();

		for (Map<String, Object> info : diaryListWithQuestion) {
			SoloDiaryEntity soloDiary = (SoloDiaryEntity)info.get("soloDiary");
			QuestionEntity question = (QuestionEntity)info.get("question");

			result.add(FindSoloDiaryResult.findBySoloDiary(soloDiary, question));
		}
		return result;
	}

	@Transactional
	@Override
	public FindSoloDiaryResult createSoloDiary(SoloDiaryCreateUpdateCommand command) {
		Long userId = getUserId();
		// visitExh의 userId 확인
		visitExhRepository.findByVisitExhIdAndUserId(command.getVisitExhId(), userId)
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

		// [TODO] 평가

		// question 조회
		QuestionEntity question = questionRepository.findByQuestionId(command.getQuestionId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		// soloDiary 추가
		SoloDiaryEntity newSoloDiary = SoloDiaryEntity.builder()
			.visitExhId(command.getVisitExhId())
			.questionId(command.getQuestionId())
			.answer(command.getAnswer())
			.writeDate(command.getWriteDate())
			.writeDate(command.getWriteDate())
			.isPublic(command.getIsPublic())
			.build();
		soloDiaryRepository.save(newSoloDiary);
		return FindSoloDiaryResult.findBySoloDiary(newSoloDiary, question);
	}

	@Transactional
	@Override
	public FindSoloDiaryResult updateSoloDiary(SoloDiaryCreateUpdateCommand command) {
		Long userId = getUserId();
		// visitExh의 userId 확인
		visitExhRepository.findByVisitExhIdAndUserId(command.getVisitExhId(), userId)
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		// soloDiary 조회
		SoloDiaryEntity soloDiary = soloDiaryRepository.findBySoloDiaryIdAndVisitExhIdAndQuestionId(
				command.getSoloDiaryId(), command.getVisitExhId(), command.getQuestionId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

		soloDiary.updateSoloDiary(SoloDiaryEntity.builder()
			.questionId(command.getQuestionId())
			.answer(command.getAnswer())
			.writeDate(command.getWriteDate())
			.isPublic(command.getIsPublic())
			.build());
		soloDiaryRepository.save(soloDiary);
		// question 조회
		QuestionEntity question = questionRepository.findByQuestionId(command.getQuestionId())
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		return FindSoloDiaryResult.findBySoloDiary(soloDiary, question);
	}

	@Transactional
	@Override
	public void deleteSoloDiary(Long visitExhId, Long soloDiaryId) {
		Long userId = getUserId();
		// visitExh의 userId 확인
		visitExhRepository.findByVisitExhIdAndUserId(visitExhId, userId)
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
		// soloDiary 조회
		SoloDiaryEntity soloDiary = soloDiaryRepository.findBySoloDiaryIdAndVisitExhId(soloDiaryId, visitExhId)
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

		soloDiaryRepository.delete(soloDiary);
	}

	private Long getUserId() {
		return getCurrentUserEntity().getUserId();
	}
}

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
import klieme.artdiary.solo.data_access.entity.EvalFactorEntity;
import klieme.artdiary.solo.data_access.entity.EvalOptionEntity;
import klieme.artdiary.solo.data_access.entity.QuestionEntity;
import klieme.artdiary.solo.data_access.entity.SoloDiaryEntity;
import klieme.artdiary.solo.data_access.repository.SoloDiaryRepository;
import klieme.artdiary.solo.data_access.repository.VisitEvalChoiceRepository;
import klieme.artdiary.solo.model.EvalInfo;
import klieme.artdiary.solo.model.SoloDiaryInfo;

@Service
public class SoloDiaryService implements SoloDiaryOperationUseCase, SoloDiaryReadUseCase {
	private final SoloDiaryRepository soloDiaryRepository;
	private final VisitExhRepository visitExhRepository;
	private final VisitEvalChoiceRepository visitExhChoiceRepository;

	@Autowired
	public SoloDiaryService(SoloDiaryRepository soloDiaryRepository, VisitExhRepository visitExhRepository,
		VisitEvalChoiceRepository visitExhChoiceRepository) {
		this.soloDiaryRepository = soloDiaryRepository;
		this.visitExhRepository = visitExhRepository;
		this.visitExhChoiceRepository = visitExhChoiceRepository;
	}

	@Override
	public FindSoloDiaryResult getSoloDiaryList(Long visitExhId) {
		Long userId = getUserId();
		// visitExh의 userId 확인
		visitExhRepository.findByVisitExhIdAndUserId(visitExhId, userId)
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

		List<EvalInfo> evalInfoList = new ArrayList<>();
		List<SoloDiaryInfo> soloDiaryInfoList = new ArrayList<>();
		// 평가 정보 가져오기
		List<Map<String, Object>> evalChoices = visitExhChoiceRepository.getChoices(visitExhId);

		for (Map<String, Object> info : evalChoices) {
			EvalFactorEntity evalFactor = (EvalFactorEntity)info.get("evalFactor");
			EvalOptionEntity evalOption = (EvalOptionEntity)info.get("evalOption");

			evalInfoList.add(EvalInfo.of(evalFactor, evalOption));
		}

		// soloDiary에서 visitExhId에 해당하는 것 모두 가져오기 - question 필요
		List<Map<String, Object>> diaryListWithQuestion = soloDiaryRepository.getSoloDiaryListWithQuestion(visitExhId);

		for (Map<String, Object> info : diaryListWithQuestion) {
			SoloDiaryEntity soloDiary = (SoloDiaryEntity)info.get("soloDiary");
			QuestionEntity question = (QuestionEntity)info.get("question");

			soloDiaryInfoList.add(SoloDiaryInfo.of(soloDiary, question));
		}
		return FindSoloDiaryResult.findBySoloDiary(evalInfoList, soloDiaryInfoList);
	}

	@Transactional
	@Override
	public void createSoloDiary(SoloDiaryCreateUpdateCommand command) {
		Long userId = getUserId();
		// visitExh의 userId 확인
		visitExhRepository.findByVisitExhIdAndUserId(command.getVisitExhId(), userId)
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

		// [TODO] 평가

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
	}

	@Transactional
	@Override
	public void updateSoloDiary(SoloDiaryCreateUpdateCommand command) {
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

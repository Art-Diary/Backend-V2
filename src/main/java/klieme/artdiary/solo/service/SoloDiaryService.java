package klieme.artdiary.solo.service;

import static klieme.artdiary.common.SecurityUtil.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
import klieme.artdiary.solo.data_access.entity.VisitEvalChoiceEntity;
import klieme.artdiary.solo.data_access.repository.SoloDiaryRepository;
import klieme.artdiary.solo.data_access.repository.VisitEvalChoiceRepository;
import klieme.artdiary.solo.dto.EvalChoiceInfo;
import klieme.artdiary.solo.model.EvalInfo;
import klieme.artdiary.solo.model.SoloDiaryInfo;

@Service
public class SoloDiaryService implements SoloDiaryOperationUseCase, SoloDiaryReadUseCase {
	private final SoloDiaryRepository soloDiaryRepository;
	private final VisitExhRepository visitExhRepository;
	private final VisitEvalChoiceRepository visitEvalChoiceRepository;

	@Autowired
	public SoloDiaryService(SoloDiaryRepository soloDiaryRepository, VisitExhRepository visitExhRepository,
		VisitEvalChoiceRepository visitEvalChoiceRepository) {
		this.soloDiaryRepository = soloDiaryRepository;
		this.visitExhRepository = visitExhRepository;
		this.visitEvalChoiceRepository = visitEvalChoiceRepository;
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
		List<Map<String, Object>> evalChoices = visitEvalChoiceRepository.getChoices(visitExhId);

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

	@Transactional
	@Override
	public void updateEvaluationList(EvalChoiceUpdateCommand command) {
		Long userId = getUserId();
		// 1) 소유자 검증
		visitExhRepository.findByVisitExhIdAndUserId(command.getVisitExhId(), userId)
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

		// 2) 기존 선택 불러오기
		List<VisitEvalChoiceEntity> existing = visitEvalChoiceRepository.findByVisitExhId(command.getVisitExhId());

		List<VisitEvalChoiceEntity> toSave = new ArrayList<>();
		List<VisitEvalChoiceEntity> toDelete = new ArrayList<>();

		// --- 3) 단일선택(upsert) 처리: factorId != 1 ---
		for (VisitEvalChoiceEntity visitEvalChoice : existing) {
			Integer compareFactorId = visitEvalChoice.getFactorId();

			if (compareFactorId != 1) {
				// 2번, 3번은 하나씩만 있어서 optionId만 바꿔주면 됨.
				EvalChoiceInfo evalChoiceInfo = command.evalChoiceInfoList.stream()
					.filter(value -> Objects.equals(value.getFactorId(), compareFactorId))
					.findFirst()
					.orElse(null);
				assert evalChoiceInfo != null;
				visitEvalChoice.updateOptionId(evalChoiceInfo.getOptionId());
				toSave.add(visitEvalChoice);
			}
		}

		// --- 4) 다중선택(sync) 처리: factorId == 1 ---
		List<Integer> requestedMulti = command.evalChoiceInfoList.stream()
			.filter(info -> info.getFactorId() == 1)
			.map(EvalChoiceInfo::getOptionId)
			.toList();
		Set<Integer> requestedSet = new HashSet<>(requestedMulti);

		List<VisitEvalChoiceEntity> currentMulti = existing.stream()
			.filter(value -> value.getFactorId().equals(1))
			.toList();
		Set<Integer> currentSet = currentMulti.stream()
			.map(VisitEvalChoiceEntity::getOptionId)
			.collect(Collectors.toSet());

		// 추가할 것(요청 - 현재)
		Set<Integer> toAdd = new HashSet<>(requestedSet);
		toAdd.removeAll(currentSet);

		// 제거할 것(현재 - 요청)
		Set<Integer> toRemove = new HashSet<>(currentSet);
		toRemove.removeAll(requestedSet);

		// 추가
		for (Integer optionId : toAdd) {
			toSave.add(VisitEvalChoiceEntity.builder()
				.visitExhId(command.getVisitExhId())
				.factorId(1)
				.optionId(optionId)
				.build());
		}

		// 삭제
		for (VisitEvalChoiceEntity entity : currentMulti) {
			if (toRemove.contains(entity.getOptionId())) {
				toDelete.add(entity);
			}
		}

		// 5) 저장/삭제
		if (!toDelete.isEmpty()) {
			visitEvalChoiceRepository.deleteAll(toDelete);
		}
		if (!toSave.isEmpty()) {
			visitEvalChoiceRepository.saveAll(toSave);
		}
	}

	private Long getUserId() {
		return getCurrentUserEntity().getUserId();
	}
}

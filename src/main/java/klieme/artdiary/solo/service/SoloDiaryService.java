package klieme.artdiary.solo.service;

import static klieme.artdiary.common.SecurityUtil.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import klieme.artdiary.common.api.ArtDiaryException;
import klieme.artdiary.common.api.MessageType;
import klieme.artdiary.solo.data_access.entity.EvalFactorEntity;
import klieme.artdiary.solo.data_access.entity.EvalOptionEntity;
import klieme.artdiary.solo.data_access.entity.ExhEvalChoiceEntity;
import klieme.artdiary.solo.data_access.entity.ExhEvalChoiceId;
import klieme.artdiary.solo.data_access.entity.QuestionEntity;
import klieme.artdiary.solo.data_access.entity.SoloDiaryEntity;
import klieme.artdiary.solo.data_access.entity.UserExhPresenceId;
import klieme.artdiary.solo.data_access.repository.SoloDiaryRepository;
import klieme.artdiary.solo.data_access.repository.UserExhPresenceRepository;
import klieme.artdiary.solo.data_access.repository.ExhEvalChoiceRepository;
import klieme.artdiary.solo.dto.EvalChoiceInfo;
import klieme.artdiary.solo.model.EvalInfo;
import klieme.artdiary.solo.dto.SoloDiaryForCreateInfo;
import klieme.artdiary.solo.model.SoloDiaryInfo;

@Service
public class SoloDiaryService implements SoloDiaryOperationUseCase, SoloDiaryReadUseCase {
	private final SoloDiaryRepository soloDiaryRepository;
	private final ExhEvalChoiceRepository exhEvalChoiceRepository;
	private final UserExhPresenceRepository userExhPresenceRepository;

	@Autowired
	public SoloDiaryService(SoloDiaryRepository soloDiaryRepository, ExhEvalChoiceRepository exhEvalChoiceRepository,
		UserExhPresenceRepository userExhPresenceRepository) {
		this.soloDiaryRepository = soloDiaryRepository;
		this.exhEvalChoiceRepository = exhEvalChoiceRepository;
		this.userExhPresenceRepository = userExhPresenceRepository;
	}

	@Override
	public FindSoloDiaryResult getSoloDiaryList(Long exhId) {
		Long userId = getUserId();
		// userExhPresence의 exhid와 userid 확인
		Boolean isPresent = userExhPresenceRepository.existsByUserExhPresenceId(
			UserExhPresenceId.builder().userId(userId).exhId(exhId).build());

		if (!isPresent) {
			throw new ArtDiaryException(MessageType.NOT_FOUND);
		}

		List<EvalInfo> evalInfoList = new ArrayList<>(); // 평가 정보 목록
		List<SoloDiaryInfo> soloDiaryInfoList = new ArrayList<>(); // 기록 목록
		// 평가 정보 가져오기
		List<Map<String, Object>> evalChoices = exhEvalChoiceRepository.getChoices(exhId, userId); // 수정

		for (Map<String, Object> info : evalChoices) {
			EvalFactorEntity evalFactor = (EvalFactorEntity)info.get("evalFactor");
			EvalOptionEntity evalOption = (EvalOptionEntity)info.get("evalOption");

			evalInfoList.add(EvalInfo.of(evalFactor, evalOption));
		}

		// soloDiary에서 exhid와 userid에 해당하는 것 모두 가져오기 - question 필요
		List<Map<String, Object>> diaryListWithQuestion = soloDiaryRepository.getSoloDiaryListWithQuestion(exhId,
			userId, false);

		for (Map<String, Object> info : diaryListWithQuestion) {
			SoloDiaryEntity soloDiary = (SoloDiaryEntity)info.get("soloDiary");
			QuestionEntity question = (QuestionEntity)info.get("question");

			soloDiaryInfoList.add(SoloDiaryInfo.of(soloDiary, question));
		}
		return FindSoloDiaryResult.findBySoloDiary(evalInfoList, soloDiaryInfoList);
	}

	@Transactional
	@Override
	public void createSoloDiary(SoloDiaryCreateCommand command) {
		Long userId = getUserId();
		// userExhPresence의 exhid와 userid 확인
		Boolean isPresent = userExhPresenceRepository.existsByUserExhPresenceId(
			UserExhPresenceId.builder().userId(userId).exhId(command.getExhId()).build());

		if (!isPresent) {
			throw new ArtDiaryException(MessageType.NOT_FOUND);
		}
		if (command.getInitEval()) {
			// 평가 없는 경우 추가
			List<ExhEvalChoiceEntity> newVisitEvalChoiceList = new ArrayList<>();

			for (EvalChoiceInfo info : command.getEvalChoiceInfoList()) {
				ExhEvalChoiceEntity newVisitEvalChoice = ExhEvalChoiceEntity.builder()
					.exhEvalChoiceId(ExhEvalChoiceId.builder()
						.optionId(info.getOptionId())
						.userId(userId)
						.exhId(command.getExhId())
						.build())
					.build();
				newVisitEvalChoiceList.add(newVisitEvalChoice);
			}
			exhEvalChoiceRepository.saveAll(newVisitEvalChoiceList);
		}
		List<SoloDiaryEntity> newSoloDiaryList = new ArrayList<>();
		// soloDiary 추가
		for (SoloDiaryForCreateInfo info : command.getSoloDiaryInfoList()) {
			SoloDiaryEntity newSoloDiary = SoloDiaryEntity.builder()
				.userId(userId)
				.exhId(command.getExhId())
				.questionId(info.getQuestionId())
				.answer(info.getAnswer())
				.writeDate(info.getWriteDate())
				.isPublic(info.getIsPublic())
				.build();
			newSoloDiaryList.add(newSoloDiary);
		}
		soloDiaryRepository.saveAll(newSoloDiaryList);
	}

	@Transactional
	@Override
	public void updateSoloDiary(SoloDiaryUpdateCommand command) {
		Long userId = getUserId();
		// userExhPresence의 exhid와 userid 확인
		Boolean isPresent = userExhPresenceRepository.existsByUserExhPresenceId(
			UserExhPresenceId.builder().userId(userId).exhId(command.getExhId()).build());

		if (!isPresent) {
			throw new ArtDiaryException(MessageType.NOT_FOUND);
		}
		// soloDiary 조회
		SoloDiaryEntity soloDiary = soloDiaryRepository.findBySoloDiaryIdAndUserIdAndExhId(command.getSoloDiaryId(),
			userId, command.getExhId()).orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

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
	public void deleteSoloDiary(Long exhId, Long soloDiaryId) {
		Long userId = getUserId();
		// userExhPresence의 exhid와 userid 확인
		Boolean isPresent = userExhPresenceRepository.existsByUserExhPresenceId(
			UserExhPresenceId.builder().userId(userId).exhId(exhId).build());

		if (!isPresent) {
			throw new ArtDiaryException(MessageType.NOT_FOUND);
		}
		// soloDiary 조회
		SoloDiaryEntity soloDiary = soloDiaryRepository.findBySoloDiaryIdAndUserIdAndExhId(soloDiaryId, userId, exhId)
			.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

		soloDiaryRepository.delete(soloDiary);
	}

	@Transactional
	@Override
	public void updateEvaluationList(EvalChoiceUpdateCommand command) {
		Long userId = getUserId();
		// 1) userExhPresence의 exhid와 userid 확인
		Boolean isPresent = userExhPresenceRepository.existsByUserExhPresenceId(
			UserExhPresenceId.builder().userId(userId).exhId(command.getExhId()).build());

		if (!isPresent) {
			throw new ArtDiaryException(MessageType.NOT_FOUND);
		}

		// 2) 기존 선택 불러오기
		List<ExhEvalChoiceEntity> existing = exhEvalChoiceRepository.findByExhEvalChoiceIdUserIdAndExhEvalChoiceIdExhId(
			userId, command.getExhId());

		// 3) 추가 및 삭제 처리
		Set<Integer> requestedSet = new HashSet<>(command.getOptionIdList());
		Set<Integer> currentSet = existing.stream()
			.map(entity -> entity.getExhEvalChoiceId().getOptionId())
			.collect(Collectors.toSet());

		// 추가할 것(요청 - 현재)
		Set<Integer> toAdd = new HashSet<>(requestedSet);
		toAdd.removeAll(currentSet);

		// 제거할 것(현재 - 요청)
		Set<Integer> toRemove = new HashSet<>(currentSet);
		toRemove.removeAll(requestedSet);

		List<ExhEvalChoiceEntity> toSave = new ArrayList<>();
		List<ExhEvalChoiceEntity> toDelete = new ArrayList<>();

		// 추가
		for (Integer optionId : toAdd) {
			toSave.add(ExhEvalChoiceEntity.builder()
				.exhEvalChoiceId(
					ExhEvalChoiceId.builder().optionId(optionId).userId(userId).exhId(command.getExhId()).build())
				.build());
		}
		// 삭제
		for (ExhEvalChoiceEntity entity : existing) {
			if (toRemove.contains(entity.getExhEvalChoiceId().getOptionId())) {
				toDelete.add(entity);
			}
		}
		// 5) 저장/삭제
		if (!toDelete.isEmpty()) {
			exhEvalChoiceRepository.deleteAll(toDelete);
		}
		if (!toSave.isEmpty()) {
			exhEvalChoiceRepository.saveAll(toSave);
		}
	}

	private Long getUserId() {
		return getCurrentUserEntity().getUserId();
	}
}

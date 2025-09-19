package klieme.artdiary.mate.service;

import static klieme.artdiary.common.SecurityUtil.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import klieme.artdiary.common.api.ArtDiaryException;
import klieme.artdiary.common.api.MessageType;
import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.mate.data_access.repository.MateRepository;
import klieme.artdiary.record_data_access.repository.VisitExhRepository;
import klieme.artdiary.solo.data_access.entity.EvalFactorEntity;
import klieme.artdiary.solo.data_access.entity.EvalOptionEntity;
import klieme.artdiary.solo.data_access.entity.QuestionEntity;
import klieme.artdiary.solo.data_access.entity.SoloDiaryEntity;
import klieme.artdiary.solo.data_access.entity.UserExhPresenceId;
import klieme.artdiary.solo.data_access.repository.ExhEvalChoiceRepository;
import klieme.artdiary.solo.data_access.repository.SoloDiaryRepository;
import klieme.artdiary.solo.data_access.repository.UserExhPresenceRepository;
import klieme.artdiary.solo.model.EvalInfo;
import klieme.artdiary.solo.model.SoloDiaryInfo;

@Service
public class MateExhService implements MateExhReadUseCase {
	private final MateRepository mateRepository;
	private final VisitExhRepository visitExhRepository;
	private final SoloDiaryRepository soloDiaryRepository;
	private final UserExhPresenceRepository userExhPresenceRepository;
	private final ExhEvalChoiceRepository exhEvalChoiceRepository;

	@Autowired
	public MateExhService(MateRepository mateRepository, VisitExhRepository visitExhRepository,
		SoloDiaryRepository soloDiaryRepository, UserExhPresenceRepository userExhPresenceRepository,
		ExhEvalChoiceRepository exhEvalChoiceRepository) {
		this.exhEvalChoiceRepository = exhEvalChoiceRepository;
		this.mateRepository = mateRepository;
		this.visitExhRepository = visitExhRepository;
		this.soloDiaryRepository = soloDiaryRepository;
		this.userExhPresenceRepository = userExhPresenceRepository;
	}

	@Override
	public List<FindMateExhsResult> getMateExhsList(Long mateId) {
		// 내 친구가 맞는지 확인 - exh_mate 확인
		Boolean isMate = mateRepository.existsByFromUserIdAndToUserId(getUserId(), mateId);

		if (!isMate) {
			throw new ArtDiaryException(MessageType.NOT_FOUND);
		}
		List<Map<String, Object>> visitExhList = visitExhRepository.getVisitExhListWithExhInfo(mateId);
		List<FindMateExhsResult> result = new ArrayList<>();

		for (Map<String, Object> info : visitExhList) {
			ExhEntity exhibition = (ExhEntity)info.get("exhibition");
			LocalDate visitDate = (LocalDate)info.get("visitDate");

			result.add(FindMateExhsResult.findMateExhs(exhibition, visitDate));
		}
		return result;
	}

	@Override
	public FindMateDiaryResult getMateDiaryList(MateDiaryFindQuery query) {
		// 내 친구가 맞는지 확인 - exh_mate 확인
		Boolean isMate = mateRepository.existsByFromUserIdAndToUserId(getUserId(), query.getMateId());

		if (!isMate) {
			throw new ArtDiaryException(MessageType.NOT_FOUND);
		}
		// userExhPresence의 exhid와 userid 확인
		Boolean isPresent = userExhPresenceRepository.existsByUserExhPresenceId(
			UserExhPresenceId.builder().userId(query.getMateId()).exhId(query.getExhId()).build());

		if (!isPresent) {
			throw new ArtDiaryException(MessageType.NOT_FOUND);
		}

		List<EvalInfo> evalInfoList = new ArrayList<>(); // 평가 정보 목록
		List<SoloDiaryInfo> soloDiaryInfoList = new ArrayList<>(); // 기록 목록
		// 평가 정보 가져오기
		List<Map<String, Object>> evalChoices = exhEvalChoiceRepository.getChoices(query.getExhId(),
			query.getMateId()); // 수정

		for (Map<String, Object> info : evalChoices) {
			EvalFactorEntity evalFactor = (EvalFactorEntity)info.get("evalFactor");
			EvalOptionEntity evalOption = (EvalOptionEntity)info.get("evalOption");

			evalInfoList.add(EvalInfo.of(evalFactor, evalOption));
		}

		// soloDiary에서 exhid와 userid에 해당하는 것 모두 가져오기 - question 필요
		List<Map<String, Object>> diaryListWithQuestion = soloDiaryRepository.getSoloDiaryListWithQuestion(
			query.getExhId(), query.getMateId(), true);

		for (Map<String, Object> info : diaryListWithQuestion) {
			SoloDiaryEntity soloDiary = (SoloDiaryEntity)info.get("soloDiary");
			QuestionEntity question = (QuestionEntity)info.get("question");

			soloDiaryInfoList.add(SoloDiaryInfo.of(soloDiary, question));
		}
		return FindMateDiaryResult.findMateDiary(evalInfoList, soloDiaryInfoList);
	}

	private Long getUserId() {
		return getCurrentUserEntity().getUserId();
	}
}

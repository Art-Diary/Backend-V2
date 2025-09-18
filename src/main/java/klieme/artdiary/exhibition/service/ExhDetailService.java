package klieme.artdiary.exhibition.service;

import static klieme.artdiary.common.FormatDate.*;
import static klieme.artdiary.common.SecurityUtil.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import klieme.artdiary.common.api.ArtDiaryException;
import klieme.artdiary.common.api.MessageType;
import klieme.artdiary.common.image.S3ImageTransfer;
import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.exhibition.data_access.repository.CategoryRepository;
import klieme.artdiary.exhibition.data_access.repository.ExhCategoryLinkRepository;
import klieme.artdiary.exhibition.data_access.repository.ExhRepository;
import klieme.artdiary.exhibition.dto.EvalInfoForExh;
import klieme.artdiary.exhibition.dto.SoloDiaryListForExh;
import klieme.artdiary.exhibition.info.StoredListOfDate;
import klieme.artdiary.gathering.data_access.entity.GatheringEntity;
import klieme.artdiary.gathering.data_access.repository.GatheringRepository;
import klieme.artdiary.record_data_access.entity.ExhVisitEntity;
import klieme.artdiary.record_data_access.repository.ExhVisitRepository;
import klieme.artdiary.record_data_access.repository.VisitExhRepository;
import klieme.artdiary.solo.data_access.entity.EvalFactorEntity;
import klieme.artdiary.solo.data_access.entity.EvalOptionEntity;
import klieme.artdiary.solo.data_access.entity.QuestionEntity;
import klieme.artdiary.solo.data_access.entity.SoloDiaryEntity;
import klieme.artdiary.solo.data_access.repository.EvalFactorRepository;
import klieme.artdiary.solo.data_access.repository.EvalOptionRepository;
import klieme.artdiary.solo.data_access.repository.ExhEvalChoiceRepository;
import klieme.artdiary.solo.data_access.repository.SoloDiaryRepository;
import klieme.artdiary.user.data_access.entity.UserEntity;

@Service
public class ExhDetailService implements ExhDetailReadUseCase, ExhDetailOperationUseCase {

	private final ExhRepository exhRepository;
	private final ExhVisitRepository exhVisitRepository;
	private final GatheringRepository gatheringRepository;
	private final CategoryRepository categoryRepository;
	private final ExhCategoryLinkRepository exhCategoryLinkRepository;
	private final S3ImageTransfer s3ImageTransfer;
	private final SoloDiaryRepository soloDiaryRepository;
	private final ExhEvalChoiceRepository exhEvalChoiceRepository;
	private final EvalFactorRepository evalFactorRepository;
	private final EvalOptionRepository evalOptionRepository;
	private final VisitExhRepository visitExhRepository;

	@Autowired
	public ExhDetailService(ExhRepository exhRepository, ExhVisitRepository exhVisitRepository,
		GatheringRepository gatheringRepository, CategoryRepository categoryRepository,
		ExhCategoryLinkRepository exhCategoryLinkRepository, S3ImageTransfer s3ImageTransfer,
		SoloDiaryRepository soloDiaryRepository, ExhEvalChoiceRepository exhEvalChoiceRepository,
		EvalFactorRepository evalFactorRepository, EvalOptionRepository evalOptionRepository,
		VisitExhRepository visitExhRepository) {
		this.exhRepository = exhRepository;
		this.exhVisitRepository = exhVisitRepository;
		this.gatheringRepository = gatheringRepository;
		this.categoryRepository = categoryRepository;
		this.exhCategoryLinkRepository = exhCategoryLinkRepository;
		this.s3ImageTransfer = s3ImageTransfer;
		this.soloDiaryRepository = soloDiaryRepository;
		this.exhEvalChoiceRepository = exhEvalChoiceRepository;
		this.evalFactorRepository = evalFactorRepository;
		this.evalOptionRepository = evalOptionRepository;
		this.visitExhRepository = visitExhRepository;
	}

	@Override
	public FindExhResult getExhDetailInfo(Long exhId) {
		Long userId = getUserId();
		// 관심 전시회 여부를 포함한 전시회 세부 정보 조회 -> exh entity
		Map<String, Object> exhDetailInfo = exhRepository.getExhDetailInfoWithIsLike(userId, exhId);
		ExhEntity exh = (ExhEntity)exhDetailInfo.get("exhibition");
		Boolean isLikeExh = (Boolean)exhDetailInfo.get("isLikeExh");
		// 기록 3개 포함 + 기록 총 개수도 필요 -> solo diary
		List<Map<String, Object>> soloDiaryList = soloDiaryRepository.getSoloDiaryListAndUserInfo(exhId, userId);
		int size = Math.min(soloDiaryList.size(), 3);
		List<Map<String, Object>> firstThree = soloDiaryList.subList(0, size);
		List<SoloDiaryListForExh> soloDiaries = new ArrayList<>();

		for (Map<String, Object> info : firstThree) {
			SoloDiaryEntity soloDiary = (SoloDiaryEntity)info.get("soloDiary");
			QuestionEntity question = (QuestionEntity)info.get("question");
			UserEntity user = (UserEntity)info.get("user");

			soloDiaries.add(SoloDiaryListForExh.builder()
				.soloDiaryId(soloDiary.getSoloDiaryId())
				.questionId(question.getQuestionId())
				.question(question.getQuestionText())
				.answer(soloDiary.getAnswer())
				.writeDate(changeDateTimeFormat(soloDiary.getWriteDate()))
				.userId(user.getUserId())
				.nickname(user.getNickname())
				.profile(user.getProfile())
				.isPublic(soloDiary.getIsPublic())
				.build());
		}
		// 평가도 필요 -> exh eval (각 항목 당 가장 많이 선택한 옵션 조회)
		List<EvalFactorEntity> evalFactorList = evalFactorRepository.findAll();
		List<EvalInfoForExh> evalInfos = new ArrayList<>();

		for (EvalFactorEntity factor : evalFactorList) {
			EvalOptionEntity option = evalOptionRepository.getFactorOptionInfoAboutExh(factor.getFactorId(), exhId);

			if (option == null) {
				continue;
			}
			evalInfos.add(EvalInfoForExh.builder()
				.factorId(factor.getFactorId())
				.factorCode(factor.getCode())
				.factorName(factor.getName())
				.optionId(option.getOptionId())
				.optionCode(option.getCode())
				.optionName(option.getName())
				.optionIcon(option.getIcon())
				.build());
		}
		Boolean isEvalFinished = exhEvalChoiceRepository.existsByExhEvalChoiceIdUserIdAndExhEvalChoiceIdExhId(userId,
			exhId);
		Boolean isVisitedExh = visitExhRepository.existsByExhIdAndUserId(exhId, userId);

		return FindExhResult.findByExh(exh, isLikeExh, (long)soloDiaryList.size(), isEvalFinished, isVisitedExh,
			soloDiaries, evalInfos);
	}

	@Override
	public List<FindSoloDiaryResult> getAllOfExhIdDiaries(Long exhId) {
		List<FindSoloDiaryResult> results = new ArrayList<>();
		List<Map<String, Object>> diaryList = soloDiaryRepository.getSoloDiaryListAndUserInfo(exhId, getUserId());

		for (Map<String, Object> item : diaryList) {
			SoloDiaryEntity soloDiary = (SoloDiaryEntity)item.get("soloDiary");
			QuestionEntity question = (QuestionEntity)item.get("question");
			UserEntity user = (UserEntity)item.get("user");

			results.add(FindSoloDiaryResult.findBySoloDiary(soloDiary, question, user));
		}
		return results;
	}

	@Override
	public FindStoredDateResult getStoredDateOfExhsByGatherId(StoredDateFindQuery query) {

		//NEW getStoredDateOfExhs
		// userId: getUserId(), exhId: query.getExhId(), gatherId: query.getGatherId()
		Long userId = getUserId();
		List<StoredListOfDate> dateList = new ArrayList<>();
		List<ExhVisitEntity> entities;
		GatheringEntity gathering = null;

		// 전시회 아이디 검증
		exhRepository.findByExhId(query.getExhId()).orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));

		if (query.getGatherId() == null) {
			//혼자 다녀온 전시회이면 ExhVisit 테이블에서 날짜리스트 가져오기
			entities = exhVisitRepository.findByUserIdAndExhId(userId,
				query.getExhId());

		} else {
			//그룹에서 다녀온 전시회
			gathering = gatheringRepository.findByGatheringId(query.getGatherId())
				.orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
			entities = exhVisitRepository.getGroupVisitedDateListOfExh(userId,
				query.getGatherId(),
				query.getExhId());
		}
		for (ExhVisitEntity entity : entities) {
			if (entity.getVisitDate() == null) { // 날짜 모름일 때는 건너뜀.
				continue;
			}
			dateList.add(StoredListOfDate.builder()
				.exhVisitId(entity.getExhVisitId())
				.visitDate(changeDateFormat(entity.getVisitDate()))
				.build());

		}
		return FindStoredDateResult.findByStoredDate(query.getExhId(), gathering, dateList);

	}

	// @Transactional
	// @Override
	// public FindExhResult updateExhDetailInfo(ExhUpdateCommand command) {
	//     ExhEntity exhEntity = exhRepository.findByExhId(command.getExhId())
	//        .orElseThrow(() -> new ArtDiaryException(MessageType.NOT_FOUND));
	//     String uploadImageUrl = null;
	//
	//     if (command.getPoster() != null && !Objects.equals(command.getPoster().getOriginalFilename(),
	//        exhEntity.getPoster())) {
	//        uploadImageUrl = s3ImageTransfer.uploadImageToStorage(
	//           S3ImageTransfer.UploadQuery.builder()
	//              .type(ImageType.REG_EXH)
	//              .image(command.getPoster())
	//              .exhId(command.getExhId())
	//              .build());
	//     }
	//     // 카테고리
	//     List<CategoryEntity> categoryList = categoryRepository.findAll();
	//     List<ExhCategoryLinkEntity> oldList = exhCategoryLinkRepository.findByExhCategoryLinkIdExhId(
	//        command.getExhId());
	//     List<String> fieldList = new ArrayList<>(Arrays.asList(command.getArt().split(","))); // 수정된 카테고리
	//
	//     // 삭제도 필요
	//     for (ExhCategoryLinkEntity old : oldList) {
	//        Long categoryId = old.getExhCategoryLinkId().getCategoryId();
	//        // 여전히 있는지 확인
	//        CategoryEntity category = categoryList.stream()
	//           .filter(c -> categoryId.equals(c.getCategoryId()))
	//           .findAny()
	//           .orElse(null);
	//        if (category == null) {
	//           continue;
	//        }
	//        // -> 없으면 삭제
	//        if (!fieldList.contains(category.getName())) {
	//           exhCategoryLinkRepository.delete(old);
	//        }
	//        // -> 있으면 그냥 넘기기
	//     }
	//     // 추가
	//     for (String field : fieldList) {
	//        CategoryEntity category = categoryList.stream()
	//           .filter(c -> field.equals(c.getName()))
	//           .findAny()
	//           .orElse(null);
	//
	//        if (category == null) {
	//           continue;
	//        }
	//        exhCategoryLinkRepository.save(ExhCategoryLinkEntity.builder()
	//           .exhCategoryLinkId(ExhCategoryLinkId.builder()
	//              .exhId(command.getExhId())
	//              .categoryId(category.getCategoryId())
	//              .build())
	//           .build());
	//     }
	//     ExhEntity updatedExh = ExhEntity.builder()
	//        .exhName(command.getExhName())
	//        .gallery(command.getGallery())
	//        .startDate(command.getExhPeriodStart())
	//        .endDate(command.getExhPeriodEnd())
	//        .painter(command.getPainter())
	//        .fee(command.getFee())
	//        .intro(command.getIntro())
	//        .homepageLink(command.getUrl())
	//        .poster(uploadImageUrl)
	//        .source(command.getSource())
	//        .build();
	//
	//     exhEntity.updateExhEntity(updatedExh);
	//     exhRepository.save(exhEntity);
	//     return FindExhResult.findByExh(exhEntity, null, command.getArt());
	// }

	private Long getUserId() {
		return getCurrentUserEntity().getUserId();
	}
}
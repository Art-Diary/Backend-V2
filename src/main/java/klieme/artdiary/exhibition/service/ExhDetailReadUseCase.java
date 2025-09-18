package klieme.artdiary.exhibition.service;

import static klieme.artdiary.common.FormatDate.*;

import java.util.List;

import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.exhibition.dto.EvalInfoForExh;
import klieme.artdiary.exhibition.dto.SoloDiaryListForExh;
import klieme.artdiary.solo.data_access.entity.QuestionEntity;
import klieme.artdiary.solo.data_access.entity.SoloDiaryEntity;
import klieme.artdiary.user.data_access.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public interface ExhDetailReadUseCase {
	FindExhResult getExhDetailInfo(Long exhId);

	List<FindSoloDiaryResult> getAllOfExhIdDiaries(Long exhId);

	@Getter
	@ToString
	@Builder
	class FindExhResult {
		private final Long exhId;
		private final String exhName;
		private final String gallery;
		private final String startDate;
		private final String endDate;
		private final String poster;
		private final String painter;
		private final Integer fee;
		private final String intro;
		private final String homepageLink;
		private final String source;
		private final Boolean isLikeExh;
		private final Long soloDiaryCount;
		private final Boolean isEvalFinished;
		private final Boolean isVisitedExh;
		private final List<SoloDiaryListForExh> soloDiaries;
		private final List<EvalInfoForExh> evalInfos;
		// 전시회에 대해 평가한 이력이 있는지에 대한 bool 변수 추가

		public static FindExhResult findByExh(ExhEntity exh, Boolean isLikeExh, Long soloDiaryCount,
			Boolean isEvalFinished, Boolean isVisitedExh, List<SoloDiaryListForExh> soloDiaries,
			List<EvalInfoForExh> evalInfos) {
			return FindExhResult.builder()
				.exhId(exh.getExhId())
				.exhName(exh.getExhName())
				.gallery(exh.getGallery())
				.startDate(changeDateFormat(exh.getStartDate()))
				.endDate(changeDateFormat(exh.getEndDate()))
				.poster(exh.getPoster())
				.painter(exh.getPainter())
				.fee(exh.getFee())
				.intro(exh.getIntro())
				.homepageLink(exh.getHomepageLink())
				.source(exh.getSource())
				.isLikeExh(isLikeExh)
				.soloDiaryCount(soloDiaryCount)
				.isEvalFinished(isEvalFinished)
				.isVisitedExh(isVisitedExh)
				.soloDiaries(soloDiaries)
				.evalInfos(evalInfos)
				.build();
		}
	}

	@Getter
	@ToString
	@Builder
	class FindSoloDiaryResult {
		private final Long soloDiaryId;
		private final Long questionId;
		private final String question;
		private final String answer;
		private final String writeDate;
		private final Boolean isPublic;
		private final Long userId; // 작성자. 탈퇴한 경우면 null
		private final String nickname; // 작성자. 탈퇴한 경우면 null
		private final String profile; // 작성자. 탈퇴한 경우면 null

		public static FindSoloDiaryResult findBySoloDiary(SoloDiaryEntity soloDiary, QuestionEntity question,
			UserEntity user) {
			return FindSoloDiaryResult.builder()
				.soloDiaryId(soloDiary.getSoloDiaryId())
				.questionId(question.getQuestionId())
				.question(question.getQuestionText())
				.answer(soloDiary.getAnswer())
				.writeDate(changeDateTimeFormat(soloDiary.getWriteDate()))
				.isPublic(soloDiary.getIsPublic())
				.userId(user.getUserId())
				.nickname(user.getNickname())
				.profile(user.getProfile())
				.build();
		}
	}
}
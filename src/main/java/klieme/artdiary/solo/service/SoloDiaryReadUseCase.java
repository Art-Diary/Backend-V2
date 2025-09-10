package klieme.artdiary.solo.service;

import static klieme.artdiary.common.FormatDate.*;

import java.time.LocalDate;
import java.util.List;

import klieme.artdiary.solo.data_access.entity.QuestionEntity;
import klieme.artdiary.solo.data_access.entity.SoloDiaryEntity;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface SoloDiaryReadUseCase {
	List<FindSoloDiaryResult> getMyDiaries(Long visitExhId);

	@EqualsAndHashCode
	@Getter
	@ToString
	@Builder
	class MyDiariesFindQuery {
		private final Long exhId;
		private final Boolean forget;
		private final LocalDate visitDate;
		private final Long gatherId;
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

		public static FindSoloDiaryResult findBySoloDiary(SoloDiaryEntity soloDiary, QuestionEntity question) {
			return FindSoloDiaryResult.builder()
				.soloDiaryId(soloDiary.getSoloDiaryId())
				.questionId(question.getQuestionId())
				.question(question.getQuestionText())
				.answer(soloDiary.getAnswer())
				.writeDate(changeDateFormat(soloDiary.getWriteDate()))
				.isPublic(soloDiary.getIsPublic())
				.build();
		}
	}
}

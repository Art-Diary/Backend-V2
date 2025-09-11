package klieme.artdiary.solo.model;

import static klieme.artdiary.common.FormatDate.*;

import klieme.artdiary.solo.data_access.entity.QuestionEntity;
import klieme.artdiary.solo.data_access.entity.SoloDiaryEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class SoloDiaryInfo {
	private final Long soloDiaryId;
	private final Long questionId;
	private final String question;
	private final String answer;
	private final String writeDate;
	private final Boolean isPublic;

	public static SoloDiaryInfo of(SoloDiaryEntity soloDiary, QuestionEntity question) {
		return SoloDiaryInfo.builder()
			.soloDiaryId(soloDiary.getSoloDiaryId())
			.questionId(question.getQuestionId())
			.question(question.getQuestionText())
			.answer(soloDiary.getAnswer())
			.writeDate(changeDateFormat(soloDiary.getWriteDate()))
			.isPublic(soloDiary.getIsPublic())
			.build();
	}
}

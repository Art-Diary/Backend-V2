package klieme.artdiary.solo.service;

import java.util.List;

import klieme.artdiary.solo.data_access.entity.QuestionEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public interface QuestionReadUseCase {
	List<FindQuestionResult> getQuestionList();

	@Getter
	@ToString
	@Builder
	class FindQuestionResult {
		private final Long questionId;
		private final String questionText;

		public static FindQuestionResult of(QuestionEntity question) {
			return FindQuestionResult.builder()
				.questionId(question.getQuestionId())
				.questionText(question.getQuestionText())
				.build();
		}
	}
}

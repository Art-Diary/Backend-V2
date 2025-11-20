package klieme.artdiary.qna.service;

import java.time.LocalDate;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface QnaOperationUseCase {
	void createQuestion(QnaCreateCommand command);

	void updateQnaContent(QnaUpdateCommand command);

	void deleteQuestion(Long qnaId);

	void answerQnaByAdmin(QnaAnswerUpdateCommand command);

	@EqualsAndHashCode
	@Builder
	@Getter
	@ToString
	class QnaCreateCommand {
		private final String title;
		private final String body;
		private final LocalDate writeDate;
	}

	@EqualsAndHashCode
	@Builder
	@Getter
	@ToString
	class QnaUpdateCommand {
		private final Long qnaId;
		private final String title;
		private final String body;
		private final LocalDate writeDate;
	}

	@EqualsAndHashCode
	@Builder
	@Getter
	@ToString
	class QnaAnswerUpdateCommand {
		private final Long qnaId;
		private final String answer;
		private final LocalDate answerDate;
	}
}

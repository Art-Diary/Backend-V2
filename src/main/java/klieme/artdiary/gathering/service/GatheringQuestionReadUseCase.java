package klieme.artdiary.gathering.service;

import java.util.List;

import klieme.artdiary.gathering.data_access.entity.GatheringQuestionEntity;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface GatheringQuestionReadUseCase {
	List<FindGatheringQuestionResult> getGatheringExhQuestionList(GatheringQuestionFindQuery query);

	@EqualsAndHashCode
	@Getter
	@ToString
	@Builder
	class GatheringQuestionFindQuery {
		private final Long gatheringId;
		private final Long exhId;
	}

	@Getter
	@ToString
	@Builder
	class FindGatheringQuestionResult {
		private final Long gatheringQuestionId;
		private final String question;

		public static FindGatheringQuestionResult of(GatheringQuestionEntity gatheringQuestion) {
			return FindGatheringQuestionResult.builder()
				.gatheringQuestionId(gatheringQuestion.getGatheringQuestionId())
				.question(gatheringQuestion.getQuestionText())
				.build();
		}
	}
}

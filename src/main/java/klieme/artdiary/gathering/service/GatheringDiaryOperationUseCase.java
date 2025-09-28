package klieme.artdiary.gathering.service;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface GatheringDiaryOperationUseCase {
	void createGatheringDiary(GatheringDiaryCreateCommand command);

	void updateGatheringDiary(GatheringDiaryUpdateCommand command);

	void deleteGatheringDiary(GatheringDiaryDeleteCommand command);

	@EqualsAndHashCode
	@Builder
	@Getter
	@ToString
	class GatheringDiaryCreateCommand {
		private final Long gatheringId;
		private final Long exhId;
		private final Long questionId;
		private final String content;
		private final LocalDateTime writeDate;
	}

	@EqualsAndHashCode
	@Builder
	@Getter
	@ToString
	class GatheringDiaryUpdateCommand {
		private final Long gatheringId;
		private final Long exhId;
		private final Long questionId;
		private final Long gatheringDiaryId;
		private final String content;
		private final LocalDateTime writeDate;
	}

	@EqualsAndHashCode
	@Builder
	@Getter
	@ToString
	class GatheringDiaryDeleteCommand {
		private final Long gatheringId;
		private final Long exhId;
		private final Long questionId;
		private final Long gatheringDiaryId;
	}
}

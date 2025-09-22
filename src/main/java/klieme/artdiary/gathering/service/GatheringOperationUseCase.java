package klieme.artdiary.gathering.service;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface GatheringOperationUseCase {

	GatheringReadUseCase.FindGatheringResult createGathering(GatheringCreateCommand command);

	List<GatheringReadUseCase.FindGatheringMemberResult> addGatheringMate(GatheringMateCreateCommand command);

	void deleteMyGathering(Long gatheringId);

	void addExhAboutGathering(ExhGatheringCreateCommand command);

	@EqualsAndHashCode
	@Builder
	@Getter
	@ToString
	class GatheringCreateCommand {
		private final String gatheringName;
	}

	@EqualsAndHashCode
	@Builder
	@Getter
	@ToString
	class GatheringMateCreateCommand {
		private final Long gatheringId;
		private final Long userId;
	}

	@EqualsAndHashCode
	@Builder
	@Getter
	@ToString
	class ExhGatheringCreateCommand {
		private final Long gatheringId;
		private final Long exhId;
		private final LocalDate visitDate;
	}
}

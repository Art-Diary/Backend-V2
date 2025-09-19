package klieme.artdiary.mate.service;

import java.util.List;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface MateOperationUseCase {

	List<MateReadUseCase.FindMateResult> addNewMate(MateCreateCommand command);

	@EqualsAndHashCode
	@Builder
	@Getter
	@ToString
	class MateCreateCommand {
		private final Long toUserId;
	}
}

package klieme.artdiary.solo.service;

import java.time.LocalDate;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface MyExhOperationUseCase {
	void createVisitExh(VisitExhCreateCommand command);

	@EqualsAndHashCode
	@Builder
	@Getter
	@ToString
	class VisitExhCreateCommand {
		private final Long exhId;
		private final LocalDate visitDate;
	}
}

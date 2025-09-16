package klieme.artdiary.like_exh.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface LikeExhOperationUseCase {
	void createLikeExh(LikeExhCommand command);

	void deleteLikeExh(LikeExhCommand command);

	// void deleteLikeExh(List<LikeExhCommand> commands);

	@EqualsAndHashCode
	@Builder
	@Getter
	@ToString
	class LikeExhCommand {
		private final Long exhId;
	}
}

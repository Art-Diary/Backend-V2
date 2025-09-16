package klieme.artdiary.like_exh.service;

import java.util.List;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface LikeExhOperationUseCase {
	LikeExhReadUseCase.FindLikeExhResult createLikeExh(LikeExhCreateCommand command);

	void deleteLikeExh(List<LikeExhCreateCommand> commands);

	@EqualsAndHashCode
	@Builder
	@Getter
	@ToString
	class LikeExhCreateCommand {
		private final Long exhId;
	}
}

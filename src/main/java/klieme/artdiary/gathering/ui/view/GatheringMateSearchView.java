package klieme.artdiary.gathering.ui.view;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.gathering.service.GatheringReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GatheringMateSearchView {
	private final List<GatheringReadUseCase.FindGatheringMemberResult> alreadyMate;
	private final List<GatheringReadUseCase.FindGatheringMemberResult> notMate;

	@Builder
	public GatheringMateSearchView(GatheringReadUseCase.FindIsGatheringMemberResult result) {
		this.alreadyMate = result.getAlreadyMate();
		this.notMate = result.getNotMate();
	}
}

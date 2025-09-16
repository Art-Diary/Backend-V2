package klieme.artdiary.gathering.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.gathering.service.GatheringReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GatheringView {
	private final Long gatheringId;
	private final String gatheringName;

	@Builder
	public GatheringView(GatheringReadUseCase.FindGatheringResult result) {
		this.gatheringId = result.getGatheringId();
		this.gatheringName = result.getGatheringName();
	}
}

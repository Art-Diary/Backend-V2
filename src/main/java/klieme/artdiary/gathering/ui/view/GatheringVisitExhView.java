package klieme.artdiary.gathering.ui.view;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.gathering.info.VisitExhInfo;
import klieme.artdiary.gathering.service.GatheringReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GatheringVisitExhView {
	private final Integer day;
	private final List<VisitExhInfo> exhibitions;

	@Builder
	public GatheringVisitExhView(GatheringReadUseCase.FindGatheringVisitExhResult result) {
		this.day = result.getDay();
		this.exhibitions = result.getExhibitions();
	}
}

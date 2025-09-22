package klieme.artdiary.gathering.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.gathering.service.GatheringReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GatheringNotVisitExhView {
	private final Long exhId;
	private final String exhName;
	private final String gallery;
	private final String poster;
	private final String startDate;
	private final String endDate;

	@Builder
	public GatheringNotVisitExhView(GatheringReadUseCase.FindGatheringNotVisitExhResult result) {
		this.exhId = result.getExhId();
		this.exhName = result.getExhName();
		this.gallery = result.getGallery();
		this.poster = result.getPoster();
		this.startDate = result.getStartDate();
		this.endDate = result.getEndDate();
	}
}

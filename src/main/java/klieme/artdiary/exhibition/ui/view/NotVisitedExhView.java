package klieme.artdiary.exhibition.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.exhibition.service.ExhReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotVisitedExhView {
	private final Long exhId;
	private final String exhName;
	private final String gallery;
	private final String startDate;
	private final String endDate;
	private final String poster;

	@Builder
	public NotVisitedExhView(ExhReadUseCase.FindNotVisitedExhResult result) {
		this.exhId = result.getExhId();
		this.exhName = result.getExhName();
		this.gallery = result.getGallery();
		this.startDate = result.getStartDate();
		this.endDate = result.getEndDate();
		this.poster = result.getPoster();
	}
}

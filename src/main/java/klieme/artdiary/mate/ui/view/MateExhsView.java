package klieme.artdiary.mate.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.mate.service.MateExhReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MateExhsView {
	private final Long exhId;
	private final String exhName;
	private final String gallery;
	private final String poster;
	private final String visitDate;

	@Builder
	public MateExhsView(MateExhReadUseCase.FindMateExhsResult result) {
		this.exhId = result.getExhId();
		this.exhName = result.getExhName();
		this.gallery = result.getGallery();
		this.poster = result.getPoster();
		this.visitDate = result.getVisitDate();
	}
}

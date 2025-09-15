package klieme.artdiary.solo.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.solo.service.MyExhReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MyVisitExhsView {
	private final Long exhId;
	private final String exhName;
	private final String gallery;
	private final String poster;
	private final String visitDate;

	@Builder
	public MyVisitExhsView(MyExhReadUseCase.FindMyVisitExhsResult result) {
		this.exhId = result.getExhId();
		this.gallery = result.getGallery();
		this.exhName = result.getExhName();
		this.poster = result.getPoster();
		this.visitDate = result.getVisitDate();
	}

}

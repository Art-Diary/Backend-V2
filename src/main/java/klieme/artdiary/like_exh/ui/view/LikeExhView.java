package klieme.artdiary.like_exh.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.like_exh.service.LikeExhReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LikeExhView {
	private final Long exhId;
	private final String exhName;
	private final String gallery;
	private final String exhPeriodStart;
	private final String exhPeriodEnd;
	private final String poster;
	private final Boolean favoriteExh;

	@Builder
	public LikeExhView(LikeExhReadUseCase.FindLikeExhResult result) {
		this.exhId = result.getExhId();
		this.exhName = result.getExhName();
		this.gallery = result.getGallery();
		this.exhPeriodStart = result.getExhPeriodStart();
		this.exhPeriodEnd = result.getExhPeriodEnd();
		this.poster = result.getPoster();
		this.favoriteExh = result.getFavoriteExh();
	}
}

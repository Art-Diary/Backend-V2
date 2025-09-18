package klieme.artdiary.exhibition.ui.view;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.exhibition.dto.EvalInfoForExh;
import klieme.artdiary.exhibition.dto.SoloDiaryListForExh;
import klieme.artdiary.exhibition.service.ExhDetailReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExhDetailView {
	private final Long exhId;
	private final String exhName;
	private final String gallery;
	private final String startDate;
	private final String endDate;
	private final String poster;
	private final String painter;
	private final Integer fee;
	private final String intro;
	private final String homepageLink;
	private final String source;
	private final Boolean isLikeExh;
	private final Long soloDiaryCount;
	private final Boolean isEvalFinished;
	private final Boolean isVisitedExh;
	private final List<SoloDiaryListForExh> soloDiaries;
	private final List<EvalInfoForExh> evalInfos;

	@Builder
	public ExhDetailView(ExhDetailReadUseCase.FindExhResult result) {
		this.exhId = result.getExhId();
		this.exhName = result.getExhName();
		this.gallery = result.getGallery();
		this.startDate = result.getStartDate();
		this.endDate = result.getEndDate();
		this.poster = result.getPoster();
		this.painter = result.getPainter();
		this.fee = result.getFee();
		this.intro = result.getIntro();
		this.homepageLink = result.getHomepageLink();
		this.source = result.getSource();
		this.isLikeExh = result.getIsLikeExh();
		this.soloDiaryCount = result.getSoloDiaryCount();
		this.isEvalFinished = result.getIsEvalFinished();
		this.isVisitedExh = result.getIsVisitedExh();
		this.soloDiaries = result.getSoloDiaries();
		this.evalInfos = result.getEvalInfos();
	}
}
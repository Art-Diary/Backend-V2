package klieme.artdiary.gathering.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import klieme.artdiary.gathering.service.GatheringDiaryReadUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GatheringDiaryView {
	private final Long gatheringDiaryId;
	private final String content;
	private final String writeDate;
	private final Long userId;
	private final String nickname;
	private final String profile;

	@Builder
	public GatheringDiaryView(GatheringDiaryReadUseCase.FindGatheringDiaryResult result) {
		this.gatheringDiaryId = result.getGatheringDiaryId();
		this.content = result.getContent();
		this.writeDate = result.getWriteDate();
		this.userId = result.getUserId();
		this.nickname = result.getNickname();
		this.profile = result.getProfile();
	}
}

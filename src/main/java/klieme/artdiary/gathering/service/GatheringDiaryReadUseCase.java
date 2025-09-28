package klieme.artdiary.gathering.service;

import static klieme.artdiary.common.FormatDate.*;

import java.util.List;

import klieme.artdiary.gathering.data_access.entity.GatheringDiaryEntity;
import klieme.artdiary.user.data_access.entity.UserEntity;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface GatheringDiaryReadUseCase {
	List<FindGatheringDiaryResult> getGatheringDiaryList(GatheringDiaryFindQuery query);

	@EqualsAndHashCode
	@Getter
	@ToString
	@Builder
	class GatheringDiaryFindQuery {
		private final Long gatheringId;
		private final Long exhId;
		private final Long questionId;
	}

	@Getter
	@ToString
	@Builder
	class FindGatheringDiaryResult {
		private final Long gatheringDiaryId;
		private final String content;
		private final String writeDate;
		private final Long userId;
		private final String nickname;
		private final String profile;

		public static FindGatheringDiaryResult of(GatheringDiaryEntity gatheringDiary, UserEntity user) {
			return FindGatheringDiaryResult.builder()
				.gatheringDiaryId(gatheringDiary.getGatheringDiaryId())
				.content(gatheringDiary.getContent())
				.writeDate(changeDateTimeFormat(gatheringDiary.getWriteDate()))
				.userId(user.getUserId())
				.nickname(user.getNickname())
				.profile(user.getProfile())
				.build();
		}
	}
}

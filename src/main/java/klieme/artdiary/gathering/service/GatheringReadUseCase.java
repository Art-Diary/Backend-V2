package klieme.artdiary.gathering.service;

import static klieme.artdiary.common.FormatDate.*;

import java.time.LocalDate;
import java.util.List;

import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.gathering.data_access.entity.GatheringEntity;
import klieme.artdiary.gathering.info.ExhibitionInfo;
import klieme.artdiary.gathering.info.MateInfo;
import klieme.artdiary.gathering.info.VisitExhInfo;
import klieme.artdiary.user.data_access.entity.UserEntity;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface GatheringReadUseCase {

	List<FindGatheringResult> getGatheringList();

	FindGatheringDetailInfoResult getGatheringDetailInfo(Long gatheringId);

	FindIsGatheringMemberResult searchNicknameNotInGathering(GatheringNicknameFindQuery query);

	List<FindGatheringVisitExhResult> getGatheringVisitDateList(GatheringVisitExhFindQuery query);

	List<FindGatheringNotVisitExhResult> getGatheringNotVisitedExhListWithDate(GatheringNotVisitExhFindQuery query);

	@EqualsAndHashCode
	@Getter
	@ToString
	@Builder
	class GatheringNicknameFindQuery {
		private final Long gatheringId;
		private final String nickname;
	}

	@EqualsAndHashCode
	@Getter
	@ToString
	@Builder
	class GatheringVisitExhFindQuery {
		private final Long gatheringId;
		private final Integer year;
		private final Integer month;
	}

	@EqualsAndHashCode
	@Getter
	@ToString
	@Builder
	class GatheringNotVisitExhFindQuery {
		private final Long gatheringId;
		private final LocalDate date;
	}

	@Getter
	@ToString
	@Builder
	class FindGatheringResult {
		private final Long gatheringId;
		private final String gatheringName;

		public static FindGatheringResult findByGathering(GatheringEntity entity) {
			return FindGatheringResult.builder()
				.gatheringId(entity.getGatheringId())
				.gatheringName(entity.getGatheringName())
				.build();
		}
	}

	@Getter
	@ToString
	@Builder
	class FindIsGatheringMemberResult {
		private final List<FindGatheringMemberResult> alreadyMate;
		private final List<FindGatheringMemberResult> notMate;

		public static FindIsGatheringMemberResult findByGatheringMate(List<FindGatheringMemberResult> alreadyMate,
			List<FindGatheringMemberResult> notMate) {
			return FindIsGatheringMemberResult.builder()
				.alreadyMate(alreadyMate)
				.notMate(notMate)
				.build();
		}
	}

	@Getter
	@ToString
	@Builder
	class FindGatheringMemberResult {
		private final Long userId;
		private final String nickname;
		private final String profile;
		private final String artField;

		public static FindGatheringMemberResult findByGatheringMates(UserEntity user) {
			return FindGatheringMemberResult.builder()
				.userId(user.getUserId())
				.nickname(user.getNickname())
				.profile(user.getProfile())
				.artField(user.getArtField())
				.build();
		}
	}

	@Getter
	@ToString
	@Builder
	class FindGatheringDetailInfoResult {
		private final Long gatheringId;
		private final String gatheringName;
		private final List<MateInfo> mates;
		private final List<ExhibitionInfo> exhibitions;

		public static FindGatheringDetailInfoResult findByGatheringDetailInfo(GatheringEntity gathering,
			List<MateInfo> mates,
			List<ExhibitionInfo> exhibitions) {
			return FindGatheringDetailInfoResult.builder()
				.gatheringId(gathering.getGatheringId())
				.gatheringName(gathering.getGatheringName())
				.mates(mates)
				.exhibitions(exhibitions)
				.build();
		}
	}

	@Getter
	@ToString
	@Builder
	class FindGatheringVisitExhResult {
		private final Integer day;
		private final List<VisitExhInfo> exhibitions;

		public static FindGatheringVisitExhResult findByGatheringVisitExh(Integer day,
			List<VisitExhInfo> exhibitions) {
			return FindGatheringVisitExhResult.builder()
				.day(day)
				.exhibitions(exhibitions)
				.build();
		}
	}

	@Getter
	@ToString
	@Builder
	class FindGatheringNotVisitExhResult {
		private final Long exhId;
		private final String exhName;
		private final String gallery;
		private final String poster;
		private final String startDate;
		private final String endDate;

		public static FindGatheringNotVisitExhResult findByGatheringNotVisitExhResult(ExhEntity exh) {
			return FindGatheringNotVisitExhResult.builder()
				.exhId(exh.getExhId())
				.exhName(exh.getExhName())
				.gallery(exh.getGallery())
				.poster(exh.getPoster())
				.startDate(changeDateFormat(exh.getStartDate()))
				.endDate(changeDateFormat(exh.getEndDate()))
				.build();
		}
	}
}

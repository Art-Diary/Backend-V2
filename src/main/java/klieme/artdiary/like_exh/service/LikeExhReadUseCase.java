package klieme.artdiary.like_exh.service;

import static klieme.artdiary.common.FormatDate.*;

import java.util.List;

import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.like_exh.data_access.entity.LikeExhEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public interface LikeExhReadUseCase {

	List<FindLikeExhResult> getLikeExhs();

	@Getter
	@ToString
	@Builder
	class FindLikeExhResult {
		private final Long exhId;
		private final String exhName;
		private final String gallery;
		private final String exhPeriodStart;
		private final String exhPeriodEnd;
		private final String poster;
		private final Boolean favoriteExh;

		public static FindLikeExhResult findByLikeExh(LikeExhEntity favoriteExh) {
			return FindLikeExhResult.builder()
				.exhId(favoriteExh.getLikeExhId().getExhId())
				.build();
		}

		public static FindLikeExhResult findByLikeExhDetail(ExhEntity exh) {
			return FindLikeExhResult.builder()
				.exhId(exh.getExhId())
				.exhName(exh.getExhName())
				.gallery(exh.getGallery())
				.exhPeriodStart(changeDateFormat(exh.getExhPeriodStart()))
				.exhPeriodEnd(changeDateFormat(exh.getExhPeriodEnd()))
				.poster(exh.getPoster())
				.favoriteExh(true)
				.build();
		}
	}
}

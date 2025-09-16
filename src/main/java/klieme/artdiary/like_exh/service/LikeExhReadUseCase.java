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
		private final String startDate;
		private final String endDate;
		private final String poster;
		private final Boolean isLikeExh;

		public static FindLikeExhResult findByLikeExh(LikeExhEntity likeExh) {
			return FindLikeExhResult.builder()
				.exhId(likeExh.getLikeExhId().getExhId())
				.build();
		}

		public static FindLikeExhResult findByLikeExhDetail(ExhEntity exh) {
			return FindLikeExhResult.builder()
				.exhId(exh.getExhId())
				.exhName(exh.getExhName())
				.gallery(exh.getGallery())
				.startDate(changeDateFormat(exh.getStartDate()))
				.endDate(changeDateFormat(exh.getEndDate()))
				.poster(exh.getPoster())
				.isLikeExh(true)
				.build();
		}
	}
}

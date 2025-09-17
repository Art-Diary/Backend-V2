package klieme.artdiary.exhibition.service;

import static klieme.artdiary.common.FormatDate.*;

import java.time.LocalDate;
import java.util.List;

import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import klieme.artdiary.exhibition.enums.ExhField;
import klieme.artdiary.exhibition.enums.ExhPrice;
import klieme.artdiary.exhibition.enums.ExhState;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface ExhReadUseCase {

	List<FindLiteExhInfoResult> getExhList(ExhListFindQuery query);

	List<FindLiteExhInfoResult> getNotVisitedExhListWithDate(ExhListFindQuery query);

	List<FindLiteExhInfoResult> getExhListBySearchName(String searchName);

	@EqualsAndHashCode
	@Getter
	@ToString
	@Builder
	class ExhListFindQuery {
		private final String keyword;
		private final List<ExhField> fieldList;
		private final ExhPrice price;
		private final List<ExhState> stateList;
		private final LocalDate date;
	}

	@EqualsAndHashCode
	@Getter
	@ToString
	@Builder
	class StoredDateFindQuery {
		private final Long exhId;
		private final Long gatherId;
	}

	@Getter
	@ToString
	@Builder
	class FindLiteExhInfoResult {
		private final Long exhId;
		private final String exhName;
		private final String gallery;
		private final String startDate;
		private final String endDate;
		private final String poster;
		private final Boolean isLikeExh;

		public static FindLiteExhInfoResult findByNotVisitedExh(ExhEntity exh) {
			return FindLiteExhInfoResult.builder()
				.exhId(exh.getExhId())
				.exhName(exh.getExhName())
				.gallery(exh.getGallery())
				.startDate(changeDateFormat(exh.getStartDate()))
				.endDate(changeDateFormat(exh.getEndDate()))
				.poster(exh.getPoster())
				.build();
		}

		public static FindLiteExhInfoResult findByExhWithLike(ExhEntity exh, Boolean isLikeExh) {
			return FindLiteExhInfoResult.builder()
				.exhId(exh.getExhId())
				.exhName(exh.getExhName())
				.gallery(exh.getGallery())
				.startDate(changeDateFormat(exh.getStartDate()))
				.endDate(changeDateFormat(exh.getEndDate()))
				.poster(exh.getPoster())
				.isLikeExh(isLikeExh)
				.build();
		}
	}
}

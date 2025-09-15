package klieme.artdiary.solo.service;

import static klieme.artdiary.common.FormatDate.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public interface MyExhReadUseCase {

	List<FindMyVisitExhsResult> getMyVisitExhsList() throws IOException;

	@Getter
	@ToString
	@Builder
	class FindMyVisitExhsResult {
		private final Long exhId;
		private final String exhName;
		private final String gallery;
		private final String poster;
		private final String visitDate;

		@Builder
		public static FindMyVisitExhsResult findMyVisitExhs(ExhEntity entity, LocalDate visitDate) {
			return FindMyVisitExhsResult.builder()
				.exhId(entity.getExhId())
				.exhName(entity.getExhName())
				.gallery(entity.getGallery())
				.poster(entity.getPoster())
				.visitDate(changeDateFormat(visitDate))
				.build();
		}
	}
}

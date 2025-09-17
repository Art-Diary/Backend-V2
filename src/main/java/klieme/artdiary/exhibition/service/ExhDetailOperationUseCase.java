package klieme.artdiary.exhibition.service;

import java.time.LocalDate;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface ExhDetailOperationUseCase {

	// ExhDetailReadUseCase.FindExhResult updateExhDetailInfo(ExhUpdateCommand command);

	@EqualsAndHashCode
	@Builder
	@Getter
	@ToString
	class ExhUpdateCommand {
		private final Long exhId;
		private final String exhName;
		private final String gallery;
		private final LocalDate exhPeriodStart;
		private final LocalDate exhPeriodEnd;
		private final String painter;
		private final Integer fee;
		private final String intro;
		private final String url;
		private final MultipartFile poster;
		private final String art;
		private final String source;
	}
}

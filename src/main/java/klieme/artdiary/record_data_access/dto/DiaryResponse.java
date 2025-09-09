package klieme.artdiary.record_data_access.dto;

import java.time.LocalDate;

import com.querydsl.core.annotations.QueryProjection;

import klieme.artdiary.exhibition.data_access.entity.ExhEntity;
import lombok.Data;

@Data
public class DiaryResponse {
	private Double sumOfRate;
	private Long countOfDiary;
	private ExhEntity exh;
	private LocalDate lastVisitDate;

	public DiaryResponse() {}

	@QueryProjection
	public DiaryResponse(Double sumOfRate, Long countOfDiary, ExhEntity exh, LocalDate lastVisitDate) {
		this.sumOfRate = sumOfRate;
		this.countOfDiary = countOfDiary;
		this.exh = exh;
		this.lastVisitDate = lastVisitDate;
	}
}

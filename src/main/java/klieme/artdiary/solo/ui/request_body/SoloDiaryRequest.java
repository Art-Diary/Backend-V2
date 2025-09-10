package klieme.artdiary.solo.ui.request_body;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SoloDiaryRequest {
	@NotNull
	private Long questionId;
	@NotBlank
	private String answer;
	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate writeDate;
	@NotNull
	private Boolean isPublic;
}

package klieme.artdiary.solo.ui.request_body;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

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
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime writeDate;
	@NotNull
	private Boolean isPublic;
}

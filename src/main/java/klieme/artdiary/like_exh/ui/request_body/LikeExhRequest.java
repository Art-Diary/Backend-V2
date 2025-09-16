package klieme.artdiary.like_exh.ui.request_body;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LikeExhRequest {
	@NotNull
	public Long exhId;
}

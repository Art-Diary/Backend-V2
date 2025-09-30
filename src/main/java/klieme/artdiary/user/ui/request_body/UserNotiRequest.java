package klieme.artdiary.user.ui.request_body;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class UserNotiRequest {
	@NotNull
	private Boolean setNoti;
}

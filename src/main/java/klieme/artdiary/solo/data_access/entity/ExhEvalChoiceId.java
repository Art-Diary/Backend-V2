package klieme.artdiary.solo.data_access.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExhEvalChoiceId implements Serializable {
	@Column(name = "option_id", nullable = false)
	private Integer optionId;
	@Column(name = "user_id", nullable = false)
	private Long userId;
	@Column(name = "exh_id", nullable = false)
	private Long exhId;
	// @Column(nullable = false)
	// private UserExhPresenceId userExhPresenceId;

	@Builder
	public ExhEvalChoiceId(Integer optionId, Long userId, Long exhId) {
		this.optionId = optionId;
		this.userId = userId;
		this.exhId = exhId;
	}
}

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
public class UserExhPresenceId implements Serializable {
	@Column(name = "user_id", nullable = false)
	private Long userId;
	@Column(name = "exh_id", nullable = false)
	private Long exhId;

	@Builder
	public UserExhPresenceId(Long userId, Long exhId) {
		this.userId = userId;
		this.exhId = exhId;
	}
}

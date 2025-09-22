package klieme.artdiary.gathering.data_access.entity;

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
public class GatheringExhPresenceId implements Serializable {
	@Column(name = "user_id", nullable = false)
	private Long userId;
	@Column(name = "gathering_id", nullable = false)
	private Long gatheringId;
	@Column(name = "exh_id", nullable = false)
	private Long exhId;

	@Builder
	public GatheringExhPresenceId(Long userId, Long gatheringId, Long exhId) {
		this.userId = userId;
		this.gatheringId = gatheringId;
		this.exhId = exhId;
	}
}

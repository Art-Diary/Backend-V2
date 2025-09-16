package klieme.artdiary.gathering.data_access.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GatheringMemberId {
	@Column(name = "user_id", nullable = false)
	private Long userId;
	@Column(name = "gathering_id", nullable = false)
	private Long gatheringId;

	@Builder
	public GatheringMemberId(Long userId, Long gatheringId) {
		this.userId = userId;
		this.gatheringId = gatheringId;
	}
}

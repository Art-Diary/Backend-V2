package klieme.artdiary.user.data_access.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserNotificationSettingId {
	@Column(name = "user_id", nullable = false)
	private Long userId;
	@Column(name = "noti_id", nullable = false)
	private Long notiId;

	@Builder
	public UserNotificationSettingId(Long userId, Long notiId) {
		this.userId = userId;
		this.notiId = notiId;
	}
}

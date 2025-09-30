package klieme.artdiary.user.data_access.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_notification_setting")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class UserNotificationSettingEntity {
	@EmbeddedId
	private UserNotificationSettingId userNotificationSettingId;
	@Column(nullable = false)
	private Boolean state;

	@Builder
	public UserNotificationSettingEntity(UserNotificationSettingId userNotificationSettingId, Boolean state) {
		this.userNotificationSettingId = userNotificationSettingId;
		this.state = state;
	}

	public void updateState(Boolean state) {
		this.state = state;
	}
}

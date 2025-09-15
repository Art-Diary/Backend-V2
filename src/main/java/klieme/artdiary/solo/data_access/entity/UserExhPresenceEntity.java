package klieme.artdiary.solo.data_access.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_exh_presence")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class UserExhPresenceEntity {
	@EmbeddedId
	private UserExhPresenceId userExhPresenceId;

	@Builder
	public UserExhPresenceEntity(UserExhPresenceId userExhPresenceId) {
		this.userExhPresenceId = userExhPresenceId;
	}
}
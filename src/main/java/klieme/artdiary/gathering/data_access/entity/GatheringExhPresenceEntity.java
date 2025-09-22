package klieme.artdiary.gathering.data_access.entity;

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
@Table(name = "gathering_exh_presence")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class GatheringExhPresenceEntity {
	@EmbeddedId
	private GatheringExhPresenceId gatheringExhPresenceId;

	@Builder
	public GatheringExhPresenceEntity(GatheringExhPresenceId gatheringExhPresenceId) {
		this.gatheringExhPresenceId = gatheringExhPresenceId;
	}
}
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
@Table(name = "gathering_member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class GatheringMemberEntity {
	@EmbeddedId
	private GatheringMemberId gatheringMemberId;

	@Builder
	public GatheringMemberEntity(GatheringMemberId gatheringMemberId) {
		this.gatheringMemberId = gatheringMemberId;
	}
}

package klieme.artdiary.record_data_access.entity;

import java.time.LocalDate;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "visit_exh")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class VisitExhEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "visit_exh_id", nullable = false)
	private Long visitExhId;
	@Column(name = "exh_id", nullable = false)
	private Long exhId;
	@Column(name = "user_id", nullable = false)
	private Long userId;
	@Column(name = "gathering_id")
	private Long gatheringId;
	@Column(name = "visit_date", nullable = false)
	private LocalDate visitDate;

	@Builder
	public VisitExhEntity(Long visitExhId, Long userId, Long gatheringId, Long exhId, LocalDate visitDate) {
		this.visitExhId = visitExhId;
		this.userId = userId;
		this.gatheringId = gatheringId;
		this.exhId = exhId;
		this.visitDate = visitDate;
	}
}

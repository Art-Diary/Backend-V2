package klieme.artdiary.solo.data_access.entity;

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
@Table(name = "visit_eval_choice")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class VisitEvalChoiceEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "choice_id", nullable = false)
	private Long choiceId;
	@Column(name = "visit_exh_id", nullable = false)
	private Long visitExhId;
	@Column(name = "factor_id", nullable = false)
	private Integer factorId;
	@Column(name = "option_id", nullable = false)
	private Integer optionId;

	@Builder
	public VisitEvalChoiceEntity(Long choiceId, Long visitExhId, Integer factorId, Integer optionId) {
		this.choiceId = choiceId;
		this.visitExhId = visitExhId;
		this.factorId = factorId;
		this.optionId = optionId;
	}

	public void updateOptionId(Integer optionId) {
		this.optionId = optionId;
	}
}

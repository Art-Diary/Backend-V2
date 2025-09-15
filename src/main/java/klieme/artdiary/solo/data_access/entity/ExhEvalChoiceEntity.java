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
@Table(name = "exh_eval_choice")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class ExhEvalChoiceEntity {
	@EmbeddedId
	private ExhEvalChoiceId exhEvalChoiceId;

	@Builder
	public ExhEvalChoiceEntity(ExhEvalChoiceId exhEvalChoiceId) {
		this.exhEvalChoiceId = exhEvalChoiceId;
	}
}

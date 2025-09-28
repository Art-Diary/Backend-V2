package klieme.artdiary.gathering.data_access.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "gathering_question")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class GatheringQuestionEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "gathering_question_id", nullable = false)
	private Long gatheringQuestionId;
	@Column(name = "gathering_id", nullable = false)
	private Long gatheringId;
	@Column(name = "exh_id", nullable = false)
	private Long exhId;
	@Column(name = "question_text", nullable = false)
	private String questionText;

	public void updateQuestionText(String questionText) {
		this.questionText = questionText;
	}
}

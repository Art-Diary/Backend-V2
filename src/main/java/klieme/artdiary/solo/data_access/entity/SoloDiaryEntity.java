package klieme.artdiary.solo.data_access.entity;

import java.time.LocalDateTime;

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
@Table(name = "solo_diary")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class SoloDiaryEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "solo_diary_id", nullable = false)
	private Long soloDiaryId;
	@Column(name = "visit_exh_id", nullable = false)
	private Long visitExhId;
	@Column(name = "question_id", nullable = false)
	private Long questionId;
	@Column(nullable = false)
	private String answer;
	@Column(name = "write_date", nullable = false)
	private LocalDateTime writeDate;
	@Column(name = "is_public", nullable = false)
	private Boolean isPublic;

	@Builder
	public SoloDiaryEntity(Long soloDiaryId, Long visitExhId, Long questionId, String answer, Boolean isPublic,
		LocalDateTime writeDate) {
		this.soloDiaryId = soloDiaryId;
		this.visitExhId = visitExhId;
		this.questionId = questionId;
		this.answer = answer;
		this.isPublic = isPublic;
		this.writeDate = writeDate;
	}

	public void updateSoloDiary(SoloDiaryEntity soloDiary) {
		this.questionId = soloDiary.getQuestionId();
		this.answer = soloDiary.getAnswer();
		this.writeDate = soloDiary.getWriteDate();
		this.isPublic = soloDiary.getIsPublic();
	}
}

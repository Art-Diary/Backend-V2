package klieme.artdiary.gathering.data_access.entity;

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
@Table(name = "gathering_diary")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class GatheringDiaryEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "gathering_diary_id", nullable = false)
	private Long gatheringDiaryId;
	@Column(name = "gathering_question_id", nullable = false)
	private Long gatheringQuestionId;
	@Column(name = "user_id", nullable = false)
	private Long userId;
	@Column(name = "gathering_id", nullable = false)
	private Long gatheringId;
	@Column(name = "exh_id", nullable = false)
	private Long exhId;
	@Column(nullable = false)
	private String content;
	@Column(name = "write_date", nullable = false)
	private LocalDateTime writeDate;

	@Builder
	public GatheringDiaryEntity(Long gatheringDiaryId, Long gatheringQuestionId, Long userId, Long gatheringId,
		Long exhId, String content, LocalDateTime writeDate) {
		this.gatheringDiaryId = gatheringDiaryId;
		this.gatheringQuestionId = gatheringQuestionId;
		this.userId = userId;
		this.gatheringId = gatheringId;
		this.exhId = exhId;
		this.content = content;
		this.writeDate = writeDate;
	}

	public void updateGatheringDiary(String content, LocalDateTime writeDate) {
		this.content = content;
		this.writeDate = writeDate;
	}
}

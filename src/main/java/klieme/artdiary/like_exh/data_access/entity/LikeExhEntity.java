package klieme.artdiary.like_exh.data_access.entity;

import java.time.LocalDateTime;

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
@Table(name = "like_exh")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class LikeExhEntity {
	@EmbeddedId
	private LikeExhId likeExhId;
	@Column(name = "init_date", nullable = false)
	private LocalDateTime initDate;

	@Builder
	public LikeExhEntity(LikeExhId likeExhId, LocalDateTime initDate) {
		this.likeExhId = likeExhId;
		this.initDate = initDate;
	}
}

package klieme.artdiary.exhibition.data_access.entity;

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
@Table(name = "exhibition")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class ExhEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "exh_id", nullable = false)
	private Long exhId;
	@Column(name = "exh_name", nullable = false)
	private String exhName;
	@Column(nullable = false)
	private String gallery;
	@Column(name = "start_date", nullable = false)
	private LocalDate startDate;
	@Column(name = "end_date", nullable = false)
	private LocalDate endDate;
	private String painter;
	@Column(nullable = false)
	private Integer fee;
	private String intro;
	@Column(name = "homepage_link")
	private String homepageLink;
	@Column(nullable = false)
	private String poster;
	@Column(name = "art_field", nullable = false)
	private String artField;
	@Column(nullable = false)
	private String source;

	@Builder
	public ExhEntity(Long exhId, String exhName, String gallery, LocalDate startDate, LocalDate endDate, String painter,
		Integer fee, String intro, String homepageLink, String poster, String artField, String source) {
		this.exhId = exhId;
		this.exhName = exhName;
		this.gallery = gallery;
		this.startDate = startDate;
		this.endDate = endDate;
		this.painter = painter;
		this.fee = fee;
		this.intro = intro;
		this.homepageLink = homepageLink;
		this.poster = poster;
		this.artField = artField;
		this.source = source;
	}

	public void updateExhEntity(ExhEntity exhEntity) {
		if (exhEntity.getExhName() != null)
			this.exhName = exhEntity.getExhName();
		if (exhEntity.getGallery() != null)
			this.gallery = exhEntity.getGallery();
		if (exhEntity.getStartDate() != null)
			this.startDate = exhEntity.getStartDate();
		if (exhEntity.getEndDate() != null)
			this.endDate = exhEntity.getEndDate();
		this.painter = exhEntity.getPainter();
		if (exhEntity.getFee() != null)
			this.fee = exhEntity.getFee();
		this.intro = exhEntity.getIntro();
		this.homepageLink = exhEntity.getHomepageLink();
		if (exhEntity.getPoster() != null)
			this.poster = exhEntity.getPoster();
		if (exhEntity.getArtField() != null)
			this.artField = exhEntity.getArtField();
		if (exhEntity.getSource() != null)
			this.source = exhEntity.getSource();
	}
}

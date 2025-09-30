package klieme.artdiary.user.data_access.entity;

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
@Table(name = "notification_type")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class NotificationTypeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "noti_id", nullable = false)
	private Long notiId;
	@Column(nullable = false)
	private String code;
	@Column(nullable = false)
	private String name;
	@Column(name = "sub_text")
	private String subText;

	@Builder
	public NotificationTypeEntity(Long notiId, String code, String name, String subText) {
		this.notiId = notiId;
		this.code = code;
		this.name = name;
		this.subText = subText;
	}
}

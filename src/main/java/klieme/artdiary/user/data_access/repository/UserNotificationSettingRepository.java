package klieme.artdiary.user.data_access.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import klieme.artdiary.user.data_access.entity.UserNotificationSettingEntity;
import klieme.artdiary.user.data_access.entity.UserNotificationSettingId;

@Repository
public interface UserNotificationSettingRepository
	extends JpaRepository<UserNotificationSettingEntity, UserNotificationSettingId>, UserNotificationSettingRepoCustom {
	Optional<UserNotificationSettingEntity> findByUserNotificationSettingId(
		UserNotificationSettingId userNotificationSettingId);
}

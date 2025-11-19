package klieme.artdiary.user.data_access.repository;

import java.util.List;
import java.util.Map;

import klieme.artdiary.user.data_access.entity.UserEntity;

public interface UserNotificationSettingRepoCustom {
	List<Map<String, Object>> getUserNotificationSettingList(Long userId);

	List<UserEntity> getPushAlarmGatheringMemberNotificationInfoList(Long gatheringId, Long notiId);
}

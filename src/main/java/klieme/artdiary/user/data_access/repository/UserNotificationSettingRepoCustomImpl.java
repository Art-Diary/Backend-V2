package klieme.artdiary.user.data_access.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import klieme.artdiary.user.data_access.entity.NotificationTypeEntity;
import klieme.artdiary.user.data_access.entity.QNotificationTypeEntity;
import klieme.artdiary.user.data_access.entity.QUserNotificationSettingEntity;
import klieme.artdiary.user.data_access.entity.UserNotificationSettingEntity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserNotificationSettingRepoCustomImpl implements UserNotificationSettingRepoCustom {
	private final JPAQueryFactory query;

	@Override
	public List<Map<String, Object>> getUserNotificationSettingList(Long userId) {
		QUserNotificationSettingEntity userNotificationSetting = QUserNotificationSettingEntity.userNotificationSettingEntity;
		QNotificationTypeEntity notificationType = QNotificationTypeEntity.notificationTypeEntity;

		List<Tuple> tuples = query
			.select(notificationType, userNotificationSetting)
			.from(userNotificationSetting)
			.leftJoin(notificationType)
			.on(userNotificationSetting.userNotificationSettingId.notiId.eq(notificationType.notiId))
			.fetchJoin()
			.where(userNotificationSetting.userNotificationSettingId.userId.eq(userId))
			.fetch();
		List<Map<String, Object>> result = new ArrayList<>();

		for (Tuple tuple : tuples) {
			Map<String, Object> row = new HashMap<>();
			row.put("notificationType", tuple.get(0, NotificationTypeEntity.class));
			row.put("userNotificationSetting", tuple.get(1, UserNotificationSettingEntity.class));
			result.add(row);
		}
		return result;
	}
}

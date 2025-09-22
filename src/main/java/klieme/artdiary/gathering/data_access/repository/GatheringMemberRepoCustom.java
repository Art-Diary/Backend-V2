package klieme.artdiary.gathering.data_access.repository;

import java.util.List;
import java.util.Map;

import klieme.artdiary.gathering.data_access.entity.GatheringEntity;
import klieme.artdiary.user.data_access.entity.UserEntity;

public interface GatheringMemberRepoCustom {
	List<GatheringEntity> getGatheringListByRecentVisitDate(Long userId);

	List<Map<String, Object>> getGatheringMateListForSearch(Long gatherId, Long userId, String nickname);

	List<UserEntity> getGatheringMateList(Long gatherId);
}

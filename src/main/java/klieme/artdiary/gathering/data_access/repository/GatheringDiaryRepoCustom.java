package klieme.artdiary.gathering.data_access.repository;

import java.util.List;
import java.util.Map;

public interface GatheringDiaryRepoCustom {
	List<Map<String, Object>> getGatheringDiaryListWithUser(Long gatherId, Long exhId, Long questionId);
}

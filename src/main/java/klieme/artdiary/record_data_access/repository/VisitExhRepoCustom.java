package klieme.artdiary.record_data_access.repository;

import java.util.List;
import java.util.Map;

public interface VisitExhRepoCustom {
	List<Map<String, Object>> getVisitExhListWithExhInfo(Long userId, Long gatheringId);
}

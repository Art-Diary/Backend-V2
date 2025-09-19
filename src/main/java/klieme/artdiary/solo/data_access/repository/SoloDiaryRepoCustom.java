package klieme.artdiary.solo.data_access.repository;

import java.util.List;
import java.util.Map;

public interface SoloDiaryRepoCustom {
	List<Map<String, Object>> getSoloDiaryListWithQuestion(Long exhId, Long userId, Boolean isForMate);

	List<Map<String, Object>> getSoloDiaryListAndUserInfo(Long exhId, Long userId);
}

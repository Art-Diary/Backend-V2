package klieme.artdiary.solo.data_access.repository;

import java.util.List;
import java.util.Map;

public interface VisitEvalChoiceRepoCustom {
	List<Map<String, Object>> getChoices(Long visitExhId);
}

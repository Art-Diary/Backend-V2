package klieme.artdiary.solo.data_access.repository;

import java.util.List;
import java.util.Map;

import klieme.artdiary.solo.data_access.entity.EvalOptionEntity;

public interface EvalOptionRepoCustom {
	List<Map<String, Object>> getFactorOptionInfoList();

	EvalOptionEntity getFactorOptionInfoAboutExh(Integer factorId, Long exhId);
}
package klieme.artdiary.like_exh.data_access.repository;

import java.util.List;
import java.util.Map;

import klieme.artdiary.exhibition.data_access.entity.ExhEntity;

public interface LikeExhRepoCustom {
	List<Map<String, Object>> getLikeExhWithUserAndExh(Long notiId);

	List<ExhEntity> getLikeExhByUserId(Long userId);
}

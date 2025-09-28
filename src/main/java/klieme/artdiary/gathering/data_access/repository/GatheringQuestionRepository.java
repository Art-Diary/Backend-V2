package klieme.artdiary.gathering.data_access.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import klieme.artdiary.gathering.data_access.entity.GatheringQuestionEntity;

@Repository
public interface GatheringQuestionRepository extends JpaRepository<GatheringQuestionEntity, Long> {
	List<GatheringQuestionEntity> findByGatheringIdAndExhId(Long gatheringId, Long exhId);

	Optional<GatheringQuestionEntity> findByGatheringQuestionIdAndGatheringIdAndExhId(Long gatheringQuestionId,
		Long gatheringId, Long exhId);
}

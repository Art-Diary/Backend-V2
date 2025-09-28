package klieme.artdiary.gathering.data_access.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import klieme.artdiary.gathering.data_access.entity.GatheringDiaryEntity;

@Repository
public interface GatheringDiaryRepository extends JpaRepository<GatheringDiaryEntity, Long>, GatheringDiaryRepoCustom {
	Optional<GatheringDiaryEntity> findByGatheringDiaryIdAndGatheringQuestionIdAndUserIdAndGatheringIdAndExhId(
		Long gatheringDiaryId, Long gatheringQuestionId, Long userId, Long gatheringId, Long exhId);
}

package klieme.artdiary.solo.data_access.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import klieme.artdiary.solo.data_access.entity.SoloDiaryEntity;

@Repository
public interface SoloDiaryRepository extends JpaRepository<SoloDiaryEntity, Long>, SoloDiaryRepoCustom {
	Optional<SoloDiaryEntity> findBySoloDiaryIdAndUserIdAndExhId(Long soloDiaryId, Long useId, Long exhId);
}

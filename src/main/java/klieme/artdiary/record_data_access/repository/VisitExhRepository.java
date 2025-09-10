package klieme.artdiary.record_data_access.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import klieme.artdiary.record_data_access.entity.VisitExhEntity;

@Repository
public interface VisitExhRepository extends JpaRepository<VisitExhEntity, Long>, VisitExhRepoCustom {
	Optional<VisitExhEntity> findByVisitExhIdAndUserId(Long visitExhId, Long userId);
}

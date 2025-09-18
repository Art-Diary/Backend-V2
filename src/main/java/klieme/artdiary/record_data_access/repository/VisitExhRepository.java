package klieme.artdiary.record_data_access.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import klieme.artdiary.record_data_access.entity.VisitExhEntity;

@Repository
public interface VisitExhRepository extends JpaRepository<VisitExhEntity, Long>, VisitExhRepoCustom {
	Boolean existsByExhIdAndUserIdAndGatheringIdAndVisitDate(Long exhId, Long userId, Long gatheringId,
		LocalDate visitDate);

	Boolean existsByExhIdAndUserId(Long exhId, Long userId);
}

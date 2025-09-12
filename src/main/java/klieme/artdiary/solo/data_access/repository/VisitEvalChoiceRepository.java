package klieme.artdiary.solo.data_access.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import klieme.artdiary.solo.data_access.entity.VisitEvalChoiceEntity;

@Repository
public interface VisitEvalChoiceRepository
	extends JpaRepository<VisitEvalChoiceEntity, Long>, VisitEvalChoiceRepoCustom {
	List<VisitEvalChoiceEntity> findByVisitExhId(Long visitExhId);
}

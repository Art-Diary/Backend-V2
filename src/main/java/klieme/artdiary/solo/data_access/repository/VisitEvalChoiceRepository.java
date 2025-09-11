package klieme.artdiary.solo.data_access.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import klieme.artdiary.solo.data_access.entity.VisitEvalChoiceEntity;

@Repository
public interface VisitEvalChoiceRepository
	extends JpaRepository<VisitEvalChoiceEntity, Long>, VisitEvalChoiceRepoCustom {
}

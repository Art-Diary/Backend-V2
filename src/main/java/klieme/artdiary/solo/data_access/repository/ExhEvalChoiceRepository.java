package klieme.artdiary.solo.data_access.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import klieme.artdiary.solo.data_access.entity.ExhEvalChoiceEntity;
import klieme.artdiary.solo.data_access.entity.ExhEvalChoiceId;

@Repository
public interface ExhEvalChoiceRepository
	extends JpaRepository<ExhEvalChoiceEntity, ExhEvalChoiceId>, ExhEvalChoiceRepoCustom {
	List<ExhEvalChoiceEntity> findByExhEvalChoiceIdUserIdAndExhEvalChoiceIdExhId(Long userId, Long exhId);
}

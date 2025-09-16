package klieme.artdiary.like_exh.data_access.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import klieme.artdiary.like_exh.data_access.entity.LikeExhEntity;
import klieme.artdiary.like_exh.data_access.entity.LikeExhId;

@Repository
public interface LikeExhRepository extends JpaRepository<LikeExhEntity, LikeExhId>, LikeExhRepoCustom {
	Optional<LikeExhEntity> findByLikeExhId(LikeExhId likeExhId);
}

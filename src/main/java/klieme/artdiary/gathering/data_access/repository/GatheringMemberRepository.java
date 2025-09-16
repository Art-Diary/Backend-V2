package klieme.artdiary.gathering.data_access.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import klieme.artdiary.gathering.data_access.entity.GatheringMemberEntity;
import klieme.artdiary.gathering.data_access.entity.GatheringMemberId;

@Repository
public interface GatheringMemberRepository
	extends JpaRepository<GatheringMemberEntity, GatheringMemberId>, GatheringMemberRepoCustom {
	Optional<GatheringMemberEntity> findByGatheringMemberId(GatheringMemberId gatheringMemberId);

	List<GatheringMemberEntity> findByGatheringMemberIdGatheringId(Long gatheringId);
}

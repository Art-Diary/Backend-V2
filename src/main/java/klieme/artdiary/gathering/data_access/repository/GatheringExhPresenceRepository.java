package klieme.artdiary.gathering.data_access.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import klieme.artdiary.gathering.data_access.entity.GatheringExhPresenceEntity;
import klieme.artdiary.gathering.data_access.entity.GatheringExhPresenceId;

@Repository
public interface GatheringExhPresenceRepository
	extends JpaRepository<GatheringExhPresenceEntity, GatheringExhPresenceId> {
}

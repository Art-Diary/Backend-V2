package klieme.artdiary.solo.data_access.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import klieme.artdiary.solo.data_access.entity.UserExhPresenceEntity;
import klieme.artdiary.solo.data_access.entity.UserExhPresenceId;

@Repository
public interface UserExhPresenceRepository extends JpaRepository<UserExhPresenceEntity, UserExhPresenceId> {
	Boolean existsByUserExhPresenceId(UserExhPresenceId userExhPresenceId);
}

package nl.tudelft.sem.TAs.repositories;

import nl.tudelft.sem.TAs.entities.TA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TARepository extends JpaRepository<TA, UUID> {
    Optional<TA> findByStudentIdAndCourseId(UUID studentId, UUID courseId);
    List<TA> findAllByStudentId(UUID studentId);
}

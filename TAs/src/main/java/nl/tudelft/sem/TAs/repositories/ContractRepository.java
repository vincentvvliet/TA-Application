package nl.tudelft.sem.TAs.repositories;

import nl.tudelft.sem.TAs.entities.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContractRepository extends JpaRepository<Contract, UUID> {
    Optional<Contract> getContractByStudentIdAndCourseId(UUID studentId, UUID courseId);
}

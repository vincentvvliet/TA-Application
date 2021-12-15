package nl.tudelft.sem.Course.repositories;

import nl.tudelft.sem.Course.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {
    List<Course> findByStartDateBetween(LocalDate currentDate, LocalDate selectionPeriod);

}

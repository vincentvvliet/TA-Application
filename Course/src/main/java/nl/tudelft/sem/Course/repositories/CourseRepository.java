package nl.tudelft.sem.Course.repositories;

import nl.tudelft.sem.Course.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {
    List<Course> findByStartDateBetween(LocalDate currentDate, LocalDate selectionPeriod);

    @Query(value = "SELECT c.id " +
                   "FROM Course c " +
                   "WHERE (c.startDate <= ?1 AND c.endDate >= ?1) " +  // course overlaps with start date
                   "OR (c.startDate <= ?2 AND c.endDate >= ?2) " +     // course overlaps with end date
                   "OR (c.startDate >= ?1 AND c.endDate <= ?2)")       // course in middle of specified course
    List<UUID> findOverlappingCourses(LocalDate startDate, LocalDate endDate);

}

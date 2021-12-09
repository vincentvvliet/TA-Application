package nl.tudelft.sem.Course.repositories;

import nl.tudelft.sem.Course.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {
    List<Course> findByStart_dateBetween(LocalDate openDate, LocalDate closeDate);

    @Query("select a from Course a where :currentDate <= a.startDate")
    List<Course> findAllByStart_dateIsBefore( @Param("currentDate") LocalDate currentDate);
}

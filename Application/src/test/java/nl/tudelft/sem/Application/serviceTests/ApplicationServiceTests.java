package nl.tudelft.sem.Application.serviceTests;

import nl.tudelft.sem.Application.entities.Application;
import nl.tudelft.sem.Application.repositories.ApplicationRepository;
import nl.tudelft.sem.Application.services.ApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static reactor.core.publisher.Mono.when;

@SpringBootTest
class ApplicationServiceTests {

	@Mock
	ApplicationRepository applicationRepository;

	@InjectMocks
	ApplicationService applicationService;

	UUID courseId = UUID.randomUUID();
	Application app1 = new Application(courseId, UUID.randomUUID());
	Application app2 = 	new Application(courseId, UUID.randomUUID());
	Application app_different_course = 	new Application(UUID.randomUUID(), UUID.randomUUID());

	@Test
	void requestApplicationsByCourseId_twoApplications() {
		// Assign
		Mockito.when(applicationRepository.findApplicationsByCourseId(courseId)).thenReturn(
			List.of(app1, app2)
		);
		// Act
		List<Application> result = applicationService.getApplicationsByCourse(courseId);
		// Assert
		assertEquals(List.of(app1, app2), result);
		assertFalse(result.contains(app_different_course));
	}

	@Test
	void requestApplicationsByCourseId_noApplications() {
		// Assign
		Mockito.when(applicationRepository.findApplicationsByCourseId(courseId)).thenReturn(
			List.of()
		);
		// Act
		List<Application> result = applicationService.getApplicationsByCourse(courseId);
		// Assert
		assertTrue(result.isEmpty());
	}

}

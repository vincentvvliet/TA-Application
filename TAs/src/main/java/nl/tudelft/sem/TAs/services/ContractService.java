package nl.tudelft.sem.TAs.services;

import nl.tudelft.sem.TAs.entities.Contract;
import nl.tudelft.sem.TAs.repositories.ContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@Service
public class ContractService {
    @Autowired
    ContractRepository contractRepository;

    /**Sends notification to user microservice with a contract for a TA.
     *
     * @param studentId of recipient of notification.
     * @param courseId of course student is TAing.
     * @param port of user microservice.
     */
    public boolean sendContractNotification(UUID studentId, UUID courseId, int port) throws Exception {
        Optional<Contract> c = contractRepository.getContractByStudentIdAndCourseId(studentId, courseId);
        if (c.isEmpty()) {
            return false;
        }
        String message = "You have been hired for a TA position. Here is your contract: " + c.get().toString();
        WebClient webClient = WebClient.create("http://localhost:" + port); // 47111
        Mono<Boolean> accepted = webClient.post()
                .uri("/notification/createNotification" + studentId + "/" + message)
                .retrieve()
                .bodyToMono(Boolean.class);
        if (accepted.blockOptional().isEmpty() || !accepted.blockOptional().get()) {
            return false;
        }
        return true;

    }
}

package nl.tudelft.sem.Application.services;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class ApplicationService {

    public int getRatingForTA(UUID TAid){
        WebClient webClient = WebClient.create("http://localhost:47110");
        Mono<Integer> rating = webClient.get()
                .uri("/getRating/" + TAid)
                .retrieve()
                .bodyToMono(Integer.class);
        return rating.block();
    }
}

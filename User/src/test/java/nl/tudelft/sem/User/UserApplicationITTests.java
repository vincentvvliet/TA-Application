package nl.tudelft.sem.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.User.entities.User;
import nl.tudelft.sem.User.repositories.UserRepository;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserApplicationITTests {

    @Autowired
    private MockMvc mockMvc;

//    @Autowired
//    private UserRepository userRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    User user;

    public static MockWebServer mockBackEnd;

    @BeforeAll
    static void setupBackend() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @BeforeEach
    public void setup() {
        user = new User();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @Configuration
    @Import(PortData.class)
    @Getter
    @NoArgsConstructor
    public static class TestPorts{
        private int applicationPort = mockBackEnd.getPort();
        private int coursePort = mockBackEnd.getPort();
        private int UserPort = mockBackEnd.getPort();
        private int TAPort =  mockBackEnd.getPort();
    }

    @Test
    void test() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID applicationId = UUID.randomUUID();

        MvcResult result = mockMvc.perform(get("/acceptApplication/" + userId + "/" + applicationId))
                .andExpect(status().is(404)).andReturn();

//        mockBackEnd.enqueue(new MockResponse()
//                .setBody(new ObjectMapper().writeValueAsString(""))
//                .addHeader("Content-Type", "application/json"));
    }

}

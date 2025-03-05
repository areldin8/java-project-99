package hexlet.code.controllers;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;


import hexlet.code.util.ModelGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.instancio.Instancio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public final class UserControllerTest {

    private static final String URL_PATH = "/api/users";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper om;

    private User testUser;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor userToken;
    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor adminToken;

    @BeforeEach
    public void setUp() {
        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);
        userToken = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
        adminToken = jwt().jwt(builder -> builder.subject("hexlet@example.com"));
    }

    @Test
    public void testIndex() throws Exception {
        var request = get(URL_PATH).with(userToken);
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray();
    }

    @Test
    public void testShow() throws Exception {
        var request = get(URL_PATH + "/" + testUser.getId()).with(userToken);
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("id").isNotNull(),
                v -> v.node("email").isEqualTo(testUser.getEmail()),
                v -> v.node("firstName").isEqualTo(testUser.getFirstName()),
                v -> v.node("lastName").isEqualTo(testUser.getLastName()),
                v -> v.node("password").isAbsent()
        );
    }

    @Test
    public void testCreate() throws Exception {
        var testUser1 = Instancio.of(modelGenerator.getUserModel()).create();
        var request = post(URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(testUser1));
        var result = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("id").isNotNull(),
                v -> v.node("email").isEqualTo(testUser1.getEmail()),
                v -> v.node("firstName").isEqualTo(testUser1.getFirstName()),
                v -> v.node("lastName").isEqualTo(testUser1.getLastName()),
                v -> v.node("password").isAbsent()
        );
    }

    @Test
    public void testInvalidCreate() throws Exception {
        var testUser1 = Instancio.of(modelGenerator.getUserModel()).create();
        testUser1.setEmail("badexample@com");
        var testUser2 = Instancio.of(modelGenerator.getUserModel()).create();
        testUser2.setPasswordDigest(null);

        var request1 = post(URL_PATH).with(userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(testUser1));
        var request2 = post(URL_PATH).with(userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(testUser2));

        mockMvc.perform(request1)
                .andExpect(status().isBadRequest());
        mockMvc.perform(request2)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdate() throws Exception {
        var dto = new HashMap<String, String>();
        dto.put("email", "example@gmail.com");
        dto.put("firstName", "newFirstName");
        dto.put("lastName", "newLastName");
        dto.put("password", "secret");

        var request = put(URL_PATH + "/" + testUser.getId()).with(userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("id").isNotNull(),
                v -> v.node("email").isEqualTo(dto.get("email")),
                v -> v.node("firstName").isEqualTo(dto.get("firstName")),
                v -> v.node("lastName").isEqualTo(dto.get("lastName")),
                v -> v.node("password").isAbsent()
        );
    }

    @Test
    public void testPartialUpdate() throws Exception {
        var dto = new HashMap<String, String>();
        dto.put("firstName", "newFirstName");
        dto.put("lastName", "newLastName");

        var request = put(URL_PATH + "/" + testUser.getId()).with(userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        var updatedUser = userRepository.findById(testUser.getId())
                .orElseThrow();

        assertThatJson(body).and(
                v -> v.node("id").isNotNull(),
                v -> v.node("email").isEqualTo(testUser.getEmail()),
                v -> v.node("firstName").isEqualTo(dto.get("firstName")),
                v -> v.node("lastName").isEqualTo(dto.get("lastName")),
                v -> v.node("password").isAbsent()
        );
        assertThat(updatedUser.getPassword()).isEqualTo(testUser.getPassword());
    }

    @Test
    public void testDelete() throws Exception {
        var testUser1 = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser1);
        var user1Token = jwt().jwt(builder -> builder.subject(testUser1.getEmail()));

        var request = delete(URL_PATH + "/" + testUser1.getId()).with(user1Token);
        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        assertFalse(userRepository.existsById(testUser1.getId()));
    }

    @Test
    public void testSecurity() throws Exception {
        var requestIndex = get(URL_PATH);
        var requestShow = get(URL_PATH + "/" + testUser.getId());
        var requestShowNoContent = get(URL_PATH + "/100");
        var requestUpdate = put(URL_PATH + "/" + testUser.getId());
        var requestDelete = delete(URL_PATH + "/" + testUser.getId());
        var unauthorizedRequests = List.of(
                requestIndex, requestShow, requestShowNoContent, requestUpdate, requestDelete
        );
        for (var req : unauthorizedRequests) {
            mockMvc.perform(req)
                    .andExpect(status().isUnauthorized());
        }
    }

    @Test
    public void testAuthority() throws Exception {
        var testUser2 = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser2);
        var user2Token = jwt().jwt(builder -> builder.subject(testUser2.getEmail()));

        var dto = new HashMap<String, String>();
        dto.put("firstName", "newFirstName");
        dto.put("lastName", "newLastName");

        var requestUpdate = put(URL_PATH + "/" + testUser.getId()).with(user2Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));
        var requestDelete = delete(URL_PATH + "/" + testUser.getId()).with(user2Token);
        var forbiddenRequests = List.of(requestUpdate, requestDelete);
        for (var req : forbiddenRequests) {
            mockMvc.perform(req)
                    .andExpect(status().isForbidden());
        }

        mockMvc.perform(put(URL_PATH + "/" + testUser2.getId()).with(adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                .andExpect(status().isOk());
        mockMvc.perform(delete(URL_PATH + "/" + testUser2.getId()).with(adminToken))
                .andExpect(status().isNoContent());
    }
}

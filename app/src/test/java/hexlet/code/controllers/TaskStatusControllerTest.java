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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hexlet.code.model.User;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.UserRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.util.ModelGenerator;

import org.instancio.Instancio;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public final class TaskStatusControllerTest {

    private static final String URL_PATH = "/api/task_statuses";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private ObjectMapper om;

    private User testUser;
    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor userToken;

    private TaskStatus testTaskStatus;

    @BeforeEach
    public void setUp() {
        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);
        userToken = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
        testTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        taskStatusRepository.save(testTaskStatus);
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
        var request = get(URL_PATH + "/" + testTaskStatus.getId()).with(userToken);
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("id").isNotNull(),
                v -> v.node("name").isEqualTo(testTaskStatus.getName()),
                v -> v.node("slug").isEqualTo(testTaskStatus.getSlug())
        );
    }

    @Test
    public void testCreate() throws Exception {
        var testTaskStatus1 = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        var request = post(URL_PATH)
                .contentType(MediaType.APPLICATION_JSON).with(userToken)
                .content(om.writeValueAsString(testTaskStatus1));
        var result = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("id").isNotNull(),
                v -> v.node("name").isEqualTo(testTaskStatus1.getName()),
                v -> v.node("slug").isEqualTo(testTaskStatus1.getSlug())
        );
    }

    @Test
    public void testInvalidCreate() throws Exception {
        var testTaskStatus1 = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        testTaskStatus1.setName("");
        var testTaskStatus2 = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        testTaskStatus2.setSlug("");

        var request1 = post(URL_PATH).with(userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(testTaskStatus1));
        var request2 = post(URL_PATH).with(userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(testTaskStatus2));

        mockMvc.perform(request1)
                .andExpect(status().isBadRequest());
        mockMvc.perform(request2)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdate() throws Exception {
        var dto = new HashMap<String, String>();
        dto.put("name", "newName");
        dto.put("slug", "new_slug");

        var request = put(URL_PATH + "/" + testTaskStatus.getId()).with(userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("id").isNotNull(),
                v -> v.node("name").isEqualTo(dto.get("name")),
                v -> v.node("slug").isEqualTo(dto.get("slug"))
        );
    }

    @Test
    public void testPartialUpdate() throws Exception {
        var dto = new HashMap<String, String>();
        dto.put("name", "newNamePartial");

        var request = put(URL_PATH + "/" + testTaskStatus.getId()).with(userToken)
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
                v -> v.node("name").isEqualTo(dto.get("name")),
                v -> v.node("slug").isEqualTo(testTaskStatus.getSlug())
        );
        assertThat(updatedUser.getPassword()).isEqualTo(testUser.getPassword());
    }

    @Test
    public void testDelete() throws Exception {
        var testTaskStatus1 = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        taskStatusRepository.save(testTaskStatus1);

        var request = delete(URL_PATH + "/" + testTaskStatus1.getId()).with(userToken);
        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        assertFalse(taskStatusRepository.existsById(testTaskStatus1.getId()));
    }

    @Test
    public void testSecurity() throws Exception {
        var requestIndex = get(URL_PATH);
        var requestShow = get(URL_PATH + "/" + testTaskStatus.getId());
        var requestShowNoContent = get(URL_PATH + "/100");
        var requestUpdate = put(URL_PATH + "/" + testTaskStatus.getId());
        var requestDelete = delete(URL_PATH + "/" + testTaskStatus.getId());
        var unauthorizedRequests = List.of(
                requestIndex, requestShow, requestShowNoContent, requestUpdate, requestDelete
        );
        for (var req : unauthorizedRequests) {
            mockMvc.perform(req)
                    .andExpect(status().isUnauthorized());
        }
    }
}

package hexlet.code.controllers;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
        .JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {

    private static final String URL_PATH = "/api/tasks";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskMapper taskMapper;

    private User testUser;
    private JwtRequestPostProcessor userToken;

    private TaskStatus testTaskStatus;

    private Task testTask;

    @BeforeEach
    public void setUp() {
        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);
        userToken = jwt().jwt(builder -> builder.subject(testUser.getEmail()));

        testTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        taskStatusRepository.save(testTaskStatus);

        testTask = Instancio.of(modelGenerator.getTaskModel()).create();
        testTask.setAssignee(testUser);
        testTask.setTaskStatus(testTaskStatus);
        taskRepository.save(testTask);
    }

    @Test
    public void testLabels() throws Exception {
        var label = Instancio.of(modelGenerator.getLabelModel()).create();
        labelRepository.save(label);

        var task = Instancio.of(modelGenerator.getTaskCreateDTOModel()).create();
        task.setAssigneeId(testUser.getId());
        task.setStatus(testTaskStatus.getSlug());

        var labelSet = Set.of(label.getId());
        task.setLabelIds(labelSet);

        var request = post(URL_PATH)
                .contentType(MediaType.APPLICATION_JSON).with(userToken)
                .content(om.writeValueAsString(task));
        var result = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).node("taskLabelIds").isEqualTo(labelSet);
    }

    @Test
    public void testIndex() throws Exception {
        var request = get(URL_PATH).with(userToken);
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray().isNotEmpty();
    }

    @Test
    public void testShow() throws Exception {
        var request = get(URL_PATH + "/" + testTask.getId()).with(userToken);
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("id").isNotNull(),
                v -> v.node("index").isEqualTo(testTask.getIndex()),
                v -> v.node("createdAt").isNotNull(),
                v -> v.node("assignee_id").isPresent(),
                v -> v.node("title").isEqualTo(testTask.getName()),
                v -> v.node("content").isEqualTo(testTask.getDescription()),
                v -> v.node("status").isNotNull()
        );
    }

    @Test
    public void testCreate() throws Exception {
        var taskCreateDTO = Instancio.of(modelGenerator.getTaskCreateDTOModel()).create();
        taskCreateDTO.setAssigneeId(testUser.getId());
        taskCreateDTO.setStatus(testTaskStatus.getSlug());
        var request = post(URL_PATH)
                .contentType(MediaType.APPLICATION_JSON).with(userToken)
                .content(om.writeValueAsString(taskCreateDTO));
        var result = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("id").isNotNull(),
                v -> v.node("index").isEqualTo(taskCreateDTO.getIndex()),
                v -> v.node("createdAt").isNotNull(),
                v -> v.node("assignee_id").isPresent(),
                v -> v.node("title").isEqualTo(taskCreateDTO.getName()),
                v -> v.node("content").isEqualTo(taskCreateDTO.getDescription()),
                v -> v.node("status").isNotNull()
        );
    }

    @Test
    public void testPartialUpdate() throws Exception {
        var dto = new HashMap<String, String>();
        dto.put("title", "newTitle");
        dto.put("content", "newContent");

        var request = put(URL_PATH + "/" + testTask.getId()).with(userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("id").isNotNull(),
                v -> v.node("title").isEqualTo(dto.get("title")),
                v -> v.node("content").isEqualTo(dto.get("content"))
        );
    }

    @Test
    public void testDelete() throws Exception {
        var testTask1 = Instancio.of(modelGenerator.getTaskModel()).create();
        testTask1.setTaskStatus(testTaskStatus);
        taskRepository.save(testTask1);

        var request = delete(URL_PATH + "/" + testTask1.getId()).with(userToken);
        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        assertFalse(taskRepository.existsById(testTask1.getId()));
    }

    @Test
    public void testSecurity() throws Exception {
        var requestIndex = get(URL_PATH);
        var requestShow = get(URL_PATH + "/" + testTask.getId());
        var requestShowNoContent = get(URL_PATH + "/100");
        var requestUpdate = put(URL_PATH + "/" + testTask.getId());
        var requestDelete = delete(URL_PATH + "/" + testTask.getId());
        var unauthorizedRequests = List.of(
                requestIndex, requestShow, requestShowNoContent, requestUpdate, requestDelete
        );
        for (var req : unauthorizedRequests) {
            mockMvc.perform(req)
                    .andExpect(status().isUnauthorized());
        }
    }

    @Test
    public void testTaskSpecification() throws Exception {
        taskRepository.deleteAll();
        var labelBug = labelRepository.findByName("bug").orElseThrow();
        var labelFeature = labelRepository.findByName("feature").orElseThrow();
        var statusDraft = taskStatusRepository.findBySlug("draft").orElseThrow();
        var statusToBeFixed = taskStatusRepository.findBySlug("to_be_fixed").orElseThrow();
        var admin = userRepository.findByEmail("hexlet@example.com").orElseThrow();

        var task1 = Instancio.of(modelGenerator.getTaskModel()).create();
        task1.setTaskStatus(statusDraft);
        task1.setLabels(Set.of(labelFeature));
        task1.setAssignee(testUser);
        taskRepository.save(task1);

        var task2 = Instancio.of(modelGenerator.getTaskModel()).create();
        task2.setTaskStatus(statusToBeFixed);
        task2.setLabels(Set.of(labelBug));
        task2.setAssignee(testUser);
        taskRepository.save(task2);

        var task3 = Instancio.of(modelGenerator.getTaskModel()).create();
        task3.setName("TEST");
        task3.setTaskStatus(statusDraft);
        task3.setLabels(Set.of(labelFeature));
        task3.setAssignee(admin);
        taskRepository.save(task3);

        String specification1 = "assigneeId=%s".formatted(testUser.getId());
        var request1 = get(URL_PATH + "?" + specification1).with(userToken);
        var result1 = mockMvc.perform(request1)
                .andExpect(status().isOk())
                .andReturn();
        var body1 = result1.getResponse().getContentAsString();
        var expected1 = List.of(
                taskMapper.map(task1),
                taskMapper.map(task2)
        );
        assertThatJson(body1).isEqualTo(expected1);

        String specification2 = "status=%s".formatted(statusToBeFixed.getSlug());
        var request2 = get(URL_PATH + "?" + specification2).with(userToken);
        var result2 = mockMvc.perform(request2)
                .andExpect(status().isOk())
                .andReturn();
        var body2 = result2.getResponse().getContentAsString();
        var expected2 = List.of(
                taskMapper.map(task2)
        );
        assertThatJson(body2).isEqualTo(expected2);

        String specification3 = "labelId=%s".formatted(labelFeature.getId());
        var request3 = get(URL_PATH + "?" + specification3).with(userToken);
        var result3 = mockMvc.perform(request3)
                .andExpect(status().isOk())
                .andReturn();
        var body3 = result3.getResponse().getContentAsString();
        var expected3 = List.of(
                taskMapper.map(task1),
                taskMapper.map(task3)
        );
        assertThatJson(body3).isEqualTo(expected3);

        String specification4 = "titleCont=TEST";
        var request4 = get(URL_PATH + "?" + specification4).with(userToken);
        var result4 = mockMvc.perform(request4)
                .andExpect(status().isOk())
                .andReturn();
        var body4 = result4.getResponse().getContentAsString();
        var expected4 = List.of(
                taskMapper.map(task3)
        );
        assertThatJson(body4).isEqualTo(expected4);

        var request5 = get(URL_PATH + "?" + specification1 + "&" + specification4).with(userToken);
        var result5 = mockMvc.perform(request5)
                .andExpect(status().isOk())
                .andReturn();
        var body5 = result5.getResponse().getContentAsString();
        assertThatJson(body5).isArray().isEmpty();
    }
}

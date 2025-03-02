package hexlet.code.controllers;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import hexlet.code.model.Label;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
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

@SpringBootTest
@AutoConfigureMockMvc
public class LabelControllerTest {

    private static final String URL_PATH = "/api/labels";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private ObjectMapper om;

    private User testUser;
    private JwtRequestPostProcessor userToken;

    private Label testLabel;

    @BeforeEach
    public void setUp() {
        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);
        userToken = jwt().jwt(builder -> builder.subject(testUser.getEmail()));

        testLabel = Instancio.of(modelGenerator.getLabelModel()).create();
        labelRepository.save(testLabel);
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
        var request = get(URL_PATH + "/" + testLabel.getId()).with(userToken);
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("id").isNotNull(),
                v -> v.node("name").isEqualTo(testLabel.getName()),
                v -> v.node("createdAt").isNotNull()
        );
    }

    @Test
    public void testCreate() throws Exception {
        var label = Instancio.of(modelGenerator.getLabelModel()).create();
        var request = post(URL_PATH)
                .contentType(MediaType.APPLICATION_JSON).with(userToken)
                .content(om.writeValueAsString(label));
        var result = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("id").isNotNull(),
                v -> v.node("name").isEqualTo(label.getName()),
                v -> v.node("createdAt").isNotNull()
        );
    }

    @Test
    public void testUpdate() throws Exception {
        var dto = new HashMap<String, String>();
        dto.put("name", "newName");

        var request = put(URL_PATH + "/" + testLabel.getId()).with(userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("id").isNotNull(),
                v -> v.node("name").isEqualTo(dto.get("name")),
                v -> v.node("createdAt").isNotNull()
        );
    }

    @Test
    public void testDelete() throws Exception {
        var label = Instancio.of(modelGenerator.getLabelModel()).create();
        labelRepository.save(label);

        var request = delete(URL_PATH + "/" + label.getId()).with(userToken);
        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        assertFalse(labelRepository.existsById(label.getId()));
    }

    @Test
    public void testSecurity() throws Exception {
        var requestIndex = get(URL_PATH);
        var requestShow = get(URL_PATH + "/" + testLabel.getId());
        var requestShowNoContent = get(URL_PATH + "/100");
        var requestUpdate = put(URL_PATH + "/" + testLabel.getId());
        var requestDelete = delete(URL_PATH + "/" + testLabel.getId());
        var unauthorizedRequests = List.of(
                requestIndex, requestShow, requestShowNoContent, requestUpdate, requestDelete
        );
        for (var req : unauthorizedRequests) {
            mockMvc.perform(req)
                    .andExpect(status().isUnauthorized());
        }
    }
}



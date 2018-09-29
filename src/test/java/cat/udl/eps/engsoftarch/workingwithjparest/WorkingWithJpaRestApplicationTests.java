package cat.udl.eps.engsoftarch.workingwithjparest;

import cat.udl.eps.engsoftarch.workingwithjparest.domain.Tag;
import cat.udl.eps.engsoftarch.workingwithjparest.domain.TagHierarchy;
import cat.udl.eps.engsoftarch.workingwithjparest.repositories.TagHierarchyRepository;
import cat.udl.eps.engsoftarch.workingwithjparest.repositories.TagRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = WorkingWithJpaRestApplication.class )
@AutoConfigureMockMvc
public class WorkingWithJpaRestApplicationTests {

    private final Logger logger = LoggerFactory.getLogger(WorkingWithJpaRestApplicationTests.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TagHierarchyRepository tagHierarchyRepository;

    @Autowired
    private TagRepository tagRepository;

    private Tag aTag;
    private TagHierarchy aTagHierarchy;

    @Before
    public void setUp() {
        aTag = new Tag();
        aTag.setName("tag");
        aTagHierarchy = new TagHierarchy();
        aTagHierarchy.setName("tagHierarchy");
    }

    @Test
    public void contextLoads() {
    }

    @Test
    public void createIsolatedNewTagRoundabout() throws Exception {

        String tagUri = getLocation(
                doPostMatching("/tags",
                        MediaType.APPLICATION_JSON,
                        toJSON(aTag),
                        status().isCreated()));

        Tag receivedTag = getEntity(
                doGetMatching(tagUri, status().isOk()), Tag.class);

        assertThat(receivedTag).isEqualTo(aTag);
    }

    @Test
    public void createIsolatedNewTagHierarchyRoundabout() throws Exception {

        String tagHierarchyUri = getLocation(
                doPostMatching("/tagHierarchies",
                        MediaType.APPLICATION_JSON,
                        toJSON(aTagHierarchy),
                        status().isCreated()));

        TagHierarchy receivedTagHierarchy = getEntity(
                doGetMatching(tagHierarchyUri, status().isOk()), TagHierarchy.class);

        assertThat(receivedTagHierarchy).isEqualTo(aTagHierarchy);
    }

    @Test
    @Transactional
    public void linkTagToTagHierarchyFromTheOwningSide() throws Exception {

        String tagHierarchyUri = getLocation(
                doPostMatching("/tagHierarchies",
                        MediaType.APPLICATION_JSON,
                        toJSON(aTagHierarchy),
                        status().isCreated()));

        String tagUri = getLocation(
                doPostMatching("/tags",
                        MediaType.APPLICATION_JSON,
                        toJSON(aTag),
                        status().isCreated()));

        mockMvc.perform(
                put(tagUri + "/definedIn/")
                    .contentType("text/uri-list")
                    .content(tagHierarchyUri))
                .andExpect(status().isNoContent())
                .andDo(print());

        assertLinkedInRepositories(getId(tagUri), getId(tagHierarchyUri));
    }

    @Test
    @Transactional
    public void linkTagToTagHierarchyFromTheNonOwningSide() throws Exception {

        String tagHierarchyUri = getLocation(
                doPostMatching("/tagHierarchies",
                        MediaType.APPLICATION_JSON,
                        toJSON(aTagHierarchy),
                        status().isCreated()));

        String tagUri = getLocation(
                doPostMatching("/tags",
                        MediaType.APPLICATION_JSON,
                        toJSON(aTag),
                        status().isCreated()));

        doPostMatching(tagHierarchyUri + "/defines",
                new MediaType("text", "uri-list"),
                tagUri,
                status().isNoContent());

        // When defines is not initialized in TagHierarchy constructor status is 500

        assertLinkedInRepositories(getId(tagUri), getId(tagHierarchyUri));
    }

    @Test
    @Transactional
    public void linkTagToTagHierarchyAtCreationOfTag() throws Exception {

        String newTagHierarchyUri = getLocation(
                doPostMatching("/tagHierarchies",
                        MediaType.APPLICATION_JSON,
                        toJSON(aTagHierarchy),
                        status().isCreated()));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", "tag");
        jsonObject.put("definedIn", newTagHierarchyUri);

        String newTagUri = getLocation(
                doPostMatching("/tags",
                        MediaType.APPLICATION_JSON,
                        jsonObject.toString(),
                        status().isCreated()));

        assertLinkedInRepositories(getId(newTagUri), getId(newTagHierarchyUri));
    }

    private MvcResult doGetMatching(String uri, ResultMatcher expecting) throws Exception {
        return mockMvc.perform(
                get(uri))
                .andExpect(expecting)
                .andDo(print())
                .andReturn();
    }

    private MvcResult doPostMatching(String uri, MediaType mediaType, String content, ResultMatcher expecting) throws Exception {
        return mockMvc.perform(
                post(uri)
                        .contentType(mediaType)
                        .content(content))
                .andExpect(expecting)
                .andDo(print())
                .andReturn();
    }

    private String getLocation(MvcResult mvcResult) {
        return mvcResult.getResponse().getHeader("Location");
    }

    private static int getId(String uri) {
        return Integer.parseInt(uri.substring(uri.lastIndexOf("/") + 1));
    }

    private <T> T getEntity(MvcResult mvcResult, Class<T> clazz) throws java.io.IOException {
        return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), clazz);
    }

    private String toJSON(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    private void assertLinkedInRepositories(Integer idTag, Integer idTagHierarchy) {
        assertThat(tagRepository.findById(idTag))
                .hasValueSatisfying(tag ->
                        assertThat(tagHierarchyRepository.findById(idTagHierarchy))
                                .hasValueSatisfying(tagHierarchy -> {
                                    assertThat(tag.getDefinedIn()).isEqualTo(tagHierarchy);
                                    assertThat(tagHierarchy.getDefines()).contains(tag);
                                }));
    }

    private void assertNotLinkedInRepository(Integer idTag) {
        assertThat(tagRepository.findById(idTag))
                .hasValueSatisfying(tag ->
                        assertThat(tag.getDefinedIn()).isNull());
    }
}

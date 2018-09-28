package cat.udl.eps.engsoftarch.workingwithjparest;

import cat.udl.eps.engsoftarch.workingwithjparest.domain.Tag;
import cat.udl.eps.engsoftarch.workingwithjparest.domain.TagHierarchy;
import cat.udl.eps.engsoftarch.workingwithjparest.repositories.TagHierarchyRepository;
import cat.udl.eps.engsoftarch.workingwithjparest.repositories.TagRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
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

    @Test
    public void contextLoads() {
    }

    @Test
    public void createIsolatedNewTagRoundabout() throws Exception {

        Tag newTag = new Tag();
        newTag.setName("tag");

        JSONObject jsonObjectTag = new JSONObject();
        jsonObjectTag.put("name", "tag");

        MvcResult mvcResultPost =
                mockMvc.perform(
                            post("/tags")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonObjectTag.toString()))
                        .andExpect(status().isCreated())
                        .andDo(print())
                        .andReturn();

        String newTagUri = mvcResultPost.getResponse().getHeader("Location");

        MvcResult mvcResultGet =
                mockMvc.perform(get(newTagUri))
                        .andExpect(status().isOk())
                        .andDo(print())
                        .andReturn();

        String getTagBody = mvcResultGet.getResponse().getContentAsString();

        Tag receivedTag = objectMapper.readValue(getTagBody, Tag.class);

        assertThat(receivedTag).isEqualTo(newTag);
    }

    @Test
    public void createIsolatedNewTagHierarchyRoundabout() throws Exception {

        TagHierarchy newTagHierarchy = new TagHierarchy();
        newTagHierarchy.setName("tagHierarchy");

        JSONObject jsonObjectTagHierarchy = new JSONObject();
        jsonObjectTagHierarchy.put("name", "tagHierarchy");

        MvcResult mvcResultPost =
                mockMvc.perform(
                            post("/tagHierarchies")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonObjectTagHierarchy.toString()))
                        .andExpect(status().isCreated())
                        .andDo(print())
                        .andReturn();

        String newTagHierarchyUri = mvcResultPost.getResponse().getHeader("Location");

        MvcResult mvcResultGet =
                mockMvc.perform(get(newTagHierarchyUri))
                        .andExpect(status().isOk())
                        .andDo(print())
                        .andReturn();

        String getTagHierarchyBody = mvcResultGet.getResponse().getContentAsString();

        TagHierarchy receivedTagHierarchy = objectMapper.readValue(getTagHierarchyBody, TagHierarchy.class);

        assertThat(receivedTagHierarchy).isEqualTo(newTagHierarchy);
    }

    @Test
    public void linkTagToTagHierarhyFromTheOneSide() throws Exception {

        TagHierarchy newTagHierarchy = new TagHierarchy();
        newTagHierarchy.setName("tagHierarchy");

        JSONObject jsonObjectTagHierarchy = new JSONObject();
        jsonObjectTagHierarchy.put("name", "tagHierarchy");

        MvcResult mvcResultPostTagHierarchy =
                mockMvc.perform(
                        post("/tagHierarchies")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonObjectTagHierarchy.toString()))
                        .andExpect(status().isCreated())
                        .andDo(print())
                        .andReturn();

        String newTagHierarchyUri = mvcResultPostTagHierarchy.getResponse().getHeader("Location");

        Tag newTag = new Tag();
        newTag.setName("tag");

        JSONObject jsonObjectTag = new JSONObject();
        jsonObjectTag.put("name", "tag");

        MvcResult mvcResultPostTag =
                mockMvc.perform(
                        post("/tags")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonObjectTag.toString()))
                        .andExpect(status().isCreated())
                        .andDo(print())
                        .andReturn();

        String newTagUri = mvcResultPostTag.getResponse().getHeader("Location");

        mockMvc.perform(
                put(newTagUri+"/definedIn/")
                    .contentType("text/uri-list")
                    .content(newTagHierarchyUri))
                .andExpect(status().isNoContent())
                .andDo(print());

        Integer id = Integer.parseInt(newTagUri.substring(newTagUri.lastIndexOf("/")+1));

        assertThat(tagRepository.findById(id))
                .hasValueSatisfying(tag -> {
                    TagHierarchy th = tag.getDefinedIn();
                    logger.info("The tag {} is defined in {}", tag.getName(), th.getName());
                    assertThat(th).isNotNull();
                });

    }

    @Test
    public void linkTagToTagHierarhyFromTheManySide() throws Exception {
        TagHierarchy newTagHierarchy = new TagHierarchy();
        newTagHierarchy.setName("tagHierarchy");

        JSONObject jsonObjectTagHierarchy = new JSONObject();
        jsonObjectTagHierarchy.put("name", "tagHierarchy");

        MvcResult mvcResultPostTagHierarchy =
                mockMvc.perform(
                        post("/tagHierarchies")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonObjectTagHierarchy.toString()))
                        .andExpect(status().isCreated())
                        .andDo(print())
                        .andReturn();

        String newTagHierarchyUri = mvcResultPostTagHierarchy.getResponse().getHeader("Location");

        Tag newTag = new Tag();
        newTag.setName("tag");

        JSONObject jsonObjectTag = new JSONObject();
        jsonObjectTag.put("name", "tag");

        MvcResult mvcResultPostTag =
                mockMvc.perform(
                        post("/tags")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonObjectTag.toString()))
                        .andExpect(status().isCreated())
                        .andDo(print())
                        .andReturn();

        String newTagUri = mvcResultPostTag.getResponse().getHeader("Location");

        mockMvc.perform(
                post(newTagHierarchyUri+"/defines/")
                    .contentType("text/uri-list")
                    .content(newTagUri))
                .andExpect(status().isNoContent())
                .andDo(print());

        Integer id = Integer.parseInt(newTagUri.substring(newTagUri.lastIndexOf("/")+1));

        assertThat(tagRepository.findById(id))
                .hasValueSatisfying(tag -> assertThat(tag.getDefinedIn()).isNotNull());

    }

    @Test
    public void linkTagToTagHierarhyAtCreation() throws Exception {

        TagHierarchy newTagHierarchy = new TagHierarchy();
        newTagHierarchy.setName("tagHierarchy");

        JSONObject jsonObjectTagHierarchy = new JSONObject();
        jsonObjectTagHierarchy.put("name", "tagHierarchy");

        MvcResult mvcResultPostTagHierarchy =
                mockMvc.perform(
                        post("/tagHierarchies")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonObjectTagHierarchy.toString()))
                        .andExpect(status().isCreated())
                        .andDo(print())
                        .andReturn();

        String newTagHierarchyUri = mvcResultPostTagHierarchy.getResponse().getHeader("Location");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", "tag");
        jsonObject.put("definedIn", newTagHierarchyUri);

        MvcResult mvcResultPostTag =
                mockMvc.perform(
                        post("/tags")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonObject.toString()))
                        .andExpect(status().isCreated())
                        .andDo(print())
                        .andReturn();

        String newTagUri = mvcResultPostTag.getResponse().getHeader("Location");

        Integer id = Integer.parseInt(newTagUri.substring(newTagUri.lastIndexOf("/")+1));

        assertThat(tagRepository.findById(id))
                .hasValueSatisfying(tag -> assertThat(tag.getDefinedIn()).isNotNull());

    }
}

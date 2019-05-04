package com.spingular.chat.web.rest;

import com.spingular.chat.SpingularChatApp;

import com.spingular.chat.domain.ChatRoom;
import com.spingular.chat.repository.ChatRoomRepository;
import com.spingular.chat.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import java.util.List;


import static com.spingular.chat.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@Link ChatRoomResource} REST controller.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpingularChatApp.class)
public class ChatRoomResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESC = "AAAAAAAAAA";
    private static final String UPDATED_DESC = "BBBBBBBBBB";

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private Validator validator;

    private MockMvc restChatRoomMockMvc;

    private ChatRoom chatRoom;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ChatRoomResource chatRoomResource = new ChatRoomResource(chatRoomRepository);
        this.restChatRoomMockMvc = MockMvcBuilders.standaloneSetup(chatRoomResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ChatRoom createEntity() {
        ChatRoom chatRoom = new ChatRoom()
            .name(DEFAULT_NAME)
            .desc(DEFAULT_DESC);
        return chatRoom;
    }

    @Before
    public void initTest() {
        chatRoomRepository.deleteAll();
        chatRoom = createEntity();
    }

    @Test
    public void createChatRoom() throws Exception {
        int databaseSizeBeforeCreate = chatRoomRepository.findAll().size();

        // Create the ChatRoom
        restChatRoomMockMvc.perform(post("/api/chat-rooms")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(chatRoom)))
            .andExpect(status().isCreated());

        // Validate the ChatRoom in the database
        List<ChatRoom> chatRoomList = chatRoomRepository.findAll();
        assertThat(chatRoomList).hasSize(databaseSizeBeforeCreate + 1);
        ChatRoom testChatRoom = chatRoomList.get(chatRoomList.size() - 1);
        assertThat(testChatRoom.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testChatRoom.getDesc()).isEqualTo(DEFAULT_DESC);
    }

    @Test
    public void createChatRoomWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = chatRoomRepository.findAll().size();

        // Create the ChatRoom with an existing ID
        chatRoom.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restChatRoomMockMvc.perform(post("/api/chat-rooms")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(chatRoom)))
            .andExpect(status().isBadRequest());

        // Validate the ChatRoom in the database
        List<ChatRoom> chatRoomList = chatRoomRepository.findAll();
        assertThat(chatRoomList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    public void getAllChatRooms() throws Exception {
        // Initialize the database
        chatRoomRepository.save(chatRoom);

        // Get all the chatRoomList
        restChatRoomMockMvc.perform(get("/api/chat-rooms?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(chatRoom.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())));
    }
    
    @Test
    public void getChatRoom() throws Exception {
        // Initialize the database
        chatRoomRepository.save(chatRoom);

        // Get the chatRoom
        restChatRoomMockMvc.perform(get("/api/chat-rooms/{id}", chatRoom.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(chatRoom.getId()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.desc").value(DEFAULT_DESC.toString()));
    }

    @Test
    public void getNonExistingChatRoom() throws Exception {
        // Get the chatRoom
        restChatRoomMockMvc.perform(get("/api/chat-rooms/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateChatRoom() throws Exception {
        // Initialize the database
        chatRoomRepository.save(chatRoom);

        int databaseSizeBeforeUpdate = chatRoomRepository.findAll().size();

        // Update the chatRoom
        ChatRoom updatedChatRoom = chatRoomRepository.findById(chatRoom.getId()).get();
        updatedChatRoom
            .name(UPDATED_NAME)
            .desc(UPDATED_DESC);

        restChatRoomMockMvc.perform(put("/api/chat-rooms")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedChatRoom)))
            .andExpect(status().isOk());

        // Validate the ChatRoom in the database
        List<ChatRoom> chatRoomList = chatRoomRepository.findAll();
        assertThat(chatRoomList).hasSize(databaseSizeBeforeUpdate);
        ChatRoom testChatRoom = chatRoomList.get(chatRoomList.size() - 1);
        assertThat(testChatRoom.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testChatRoom.getDesc()).isEqualTo(UPDATED_DESC);
    }

    @Test
    public void updateNonExistingChatRoom() throws Exception {
        int databaseSizeBeforeUpdate = chatRoomRepository.findAll().size();

        // Create the ChatRoom

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChatRoomMockMvc.perform(put("/api/chat-rooms")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(chatRoom)))
            .andExpect(status().isBadRequest());

        // Validate the ChatRoom in the database
        List<ChatRoom> chatRoomList = chatRoomRepository.findAll();
        assertThat(chatRoomList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteChatRoom() throws Exception {
        // Initialize the database
        chatRoomRepository.save(chatRoom);

        int databaseSizeBeforeDelete = chatRoomRepository.findAll().size();

        // Delete the chatRoom
        restChatRoomMockMvc.perform(delete("/api/chat-rooms/{id}", chatRoom.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database is empty
        List<ChatRoom> chatRoomList = chatRoomRepository.findAll();
        assertThat(chatRoomList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ChatRoom.class);
        ChatRoom chatRoom1 = new ChatRoom();
        chatRoom1.setId("id1");
        ChatRoom chatRoom2 = new ChatRoom();
        chatRoom2.setId(chatRoom1.getId());
        assertThat(chatRoom1).isEqualTo(chatRoom2);
        chatRoom2.setId("id2");
        assertThat(chatRoom1).isNotEqualTo(chatRoom2);
        chatRoom1.setId(null);
        assertThat(chatRoom1).isNotEqualTo(chatRoom2);
    }
}
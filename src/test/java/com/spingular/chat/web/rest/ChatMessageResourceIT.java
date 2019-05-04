package com.spingular.chat.web.rest;

import com.spingular.chat.SpingularChatApp;

import com.spingular.chat.domain.ChatMessage;
import com.spingular.chat.repository.ChatMessageRepository;
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

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;


import static com.spingular.chat.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@Link ChatMessageResource} REST controller.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpingularChatApp.class)
public class ChatMessageResourceIT {

    private static final Long DEFAULT_SENDER = 1L;
    private static final Long UPDATED_SENDER = 2L;

    private static final Long DEFAULT_RECIVER = 1L;
    private static final Long UPDATED_RECIVER = 2L;

    private static final String DEFAULT_MESSAGE_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE_TEXT = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_MESSAGE_TIME = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_MESSAGE_TIME = LocalDate.now(ZoneId.systemDefault());

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private Validator validator;

    private MockMvc restChatMessageMockMvc;

    private ChatMessage chatMessage;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ChatMessageResource chatMessageResource = new ChatMessageResource(chatMessageRepository);
        this.restChatMessageMockMvc = MockMvcBuilders.standaloneSetup(chatMessageResource)
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
    public static ChatMessage createEntity() {
        ChatMessage chatMessage = new ChatMessage()
            .sender(DEFAULT_SENDER)
            .reciver(DEFAULT_RECIVER)
            .messageText(DEFAULT_MESSAGE_TEXT)
            .messageTime(DEFAULT_MESSAGE_TIME);
        return chatMessage;
    }

    @Before
    public void initTest() {
        chatMessageRepository.deleteAll();
        chatMessage = createEntity();
    }

    @Test
    public void createChatMessage() throws Exception {
        int databaseSizeBeforeCreate = chatMessageRepository.findAll().size();

        // Create the ChatMessage
        restChatMessageMockMvc.perform(post("/api/chat-messages")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(chatMessage)))
            .andExpect(status().isCreated());

        // Validate the ChatMessage in the database
        List<ChatMessage> chatMessageList = chatMessageRepository.findAll();
        assertThat(chatMessageList).hasSize(databaseSizeBeforeCreate + 1);
        ChatMessage testChatMessage = chatMessageList.get(chatMessageList.size() - 1);
        assertThat(testChatMessage.getSender()).isEqualTo(DEFAULT_SENDER);
        assertThat(testChatMessage.getReciver()).isEqualTo(DEFAULT_RECIVER);
        assertThat(testChatMessage.getMessageText()).isEqualTo(DEFAULT_MESSAGE_TEXT);
        assertThat(testChatMessage.getMessageTime()).isEqualTo(DEFAULT_MESSAGE_TIME);
    }

    @Test
    public void createChatMessageWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = chatMessageRepository.findAll().size();

        // Create the ChatMessage with an existing ID
        chatMessage.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restChatMessageMockMvc.perform(post("/api/chat-messages")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(chatMessage)))
            .andExpect(status().isBadRequest());

        // Validate the ChatMessage in the database
        List<ChatMessage> chatMessageList = chatMessageRepository.findAll();
        assertThat(chatMessageList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    public void getAllChatMessages() throws Exception {
        // Initialize the database
        chatMessageRepository.save(chatMessage);

        // Get all the chatMessageList
        restChatMessageMockMvc.perform(get("/api/chat-messages?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(chatMessage.getId())))
            .andExpect(jsonPath("$.[*].sender").value(hasItem(DEFAULT_SENDER.intValue())))
            .andExpect(jsonPath("$.[*].reciver").value(hasItem(DEFAULT_RECIVER.intValue())))
            .andExpect(jsonPath("$.[*].messageText").value(hasItem(DEFAULT_MESSAGE_TEXT.toString())))
            .andExpect(jsonPath("$.[*].messageTime").value(hasItem(DEFAULT_MESSAGE_TIME.toString())));
    }
    
    @Test
    public void getChatMessage() throws Exception {
        // Initialize the database
        chatMessageRepository.save(chatMessage);

        // Get the chatMessage
        restChatMessageMockMvc.perform(get("/api/chat-messages/{id}", chatMessage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(chatMessage.getId()))
            .andExpect(jsonPath("$.sender").value(DEFAULT_SENDER.intValue()))
            .andExpect(jsonPath("$.reciver").value(DEFAULT_RECIVER.intValue()))
            .andExpect(jsonPath("$.messageText").value(DEFAULT_MESSAGE_TEXT.toString()))
            .andExpect(jsonPath("$.messageTime").value(DEFAULT_MESSAGE_TIME.toString()));
    }

    @Test
    public void getNonExistingChatMessage() throws Exception {
        // Get the chatMessage
        restChatMessageMockMvc.perform(get("/api/chat-messages/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateChatMessage() throws Exception {
        // Initialize the database
        chatMessageRepository.save(chatMessage);

        int databaseSizeBeforeUpdate = chatMessageRepository.findAll().size();

        // Update the chatMessage
        ChatMessage updatedChatMessage = chatMessageRepository.findById(chatMessage.getId()).get();
        updatedChatMessage
            .sender(UPDATED_SENDER)
            .reciver(UPDATED_RECIVER)
            .messageText(UPDATED_MESSAGE_TEXT)
            .messageTime(UPDATED_MESSAGE_TIME);

        restChatMessageMockMvc.perform(put("/api/chat-messages")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedChatMessage)))
            .andExpect(status().isOk());

        // Validate the ChatMessage in the database
        List<ChatMessage> chatMessageList = chatMessageRepository.findAll();
        assertThat(chatMessageList).hasSize(databaseSizeBeforeUpdate);
        ChatMessage testChatMessage = chatMessageList.get(chatMessageList.size() - 1);
        assertThat(testChatMessage.getSender()).isEqualTo(UPDATED_SENDER);
        assertThat(testChatMessage.getReciver()).isEqualTo(UPDATED_RECIVER);
        assertThat(testChatMessage.getMessageText()).isEqualTo(UPDATED_MESSAGE_TEXT);
        assertThat(testChatMessage.getMessageTime()).isEqualTo(UPDATED_MESSAGE_TIME);
    }

    @Test
    public void updateNonExistingChatMessage() throws Exception {
        int databaseSizeBeforeUpdate = chatMessageRepository.findAll().size();

        // Create the ChatMessage

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChatMessageMockMvc.perform(put("/api/chat-messages")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(chatMessage)))
            .andExpect(status().isBadRequest());

        // Validate the ChatMessage in the database
        List<ChatMessage> chatMessageList = chatMessageRepository.findAll();
        assertThat(chatMessageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteChatMessage() throws Exception {
        // Initialize the database
        chatMessageRepository.save(chatMessage);

        int databaseSizeBeforeDelete = chatMessageRepository.findAll().size();

        // Delete the chatMessage
        restChatMessageMockMvc.perform(delete("/api/chat-messages/{id}", chatMessage.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database is empty
        List<ChatMessage> chatMessageList = chatMessageRepository.findAll();
        assertThat(chatMessageList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ChatMessage.class);
        ChatMessage chatMessage1 = new ChatMessage();
        chatMessage1.setId("id1");
        ChatMessage chatMessage2 = new ChatMessage();
        chatMessage2.setId(chatMessage1.getId());
        assertThat(chatMessage1).isEqualTo(chatMessage2);
        chatMessage2.setId("id2");
        assertThat(chatMessage1).isNotEqualTo(chatMessage2);
        chatMessage1.setId(null);
        assertThat(chatMessage1).isNotEqualTo(chatMessage2);
    }
}
package com.spingular.chat.web.rest;

import com.spingular.chat.domain.ChatMessage;
import com.spingular.chat.repository.ChatMessageRepository;
import com.spingular.chat.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.spingular.chat.domain.ChatMessage}.
 */
@RestController
@RequestMapping("/api")
public class ChatMessageResource {

    private final Logger log = LoggerFactory.getLogger(ChatMessageResource.class);

    private static final String ENTITY_NAME = "chatMessage";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ChatMessageRepository chatMessageRepository;

    public ChatMessageResource(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    /**
     * {@code POST  /chat-messages} : Create a new chatMessage.
     *
     * @param chatMessage the chatMessage to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new chatMessage, or with status {@code 400 (Bad Request)} if the chatMessage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/chat-messages")
    public ResponseEntity<ChatMessage> createChatMessage(@RequestBody ChatMessage chatMessage) throws URISyntaxException {
        log.debug("REST request to save ChatMessage : {}", chatMessage);
        if (chatMessage.getId() != null) {
            throw new BadRequestAlertException("A new chatMessage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ChatMessage result = chatMessageRepository.save(chatMessage);
        return ResponseEntity.created(new URI("/api/chat-messages/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /chat-messages} : Updates an existing chatMessage.
     *
     * @param chatMessage the chatMessage to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated chatMessage,
     * or with status {@code 400 (Bad Request)} if the chatMessage is not valid,
     * or with status {@code 500 (Internal Server Error)} if the chatMessage couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/chat-messages")
    public ResponseEntity<ChatMessage> updateChatMessage(@RequestBody ChatMessage chatMessage) throws URISyntaxException {
        log.debug("REST request to update ChatMessage : {}", chatMessage);
        if (chatMessage.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ChatMessage result = chatMessageRepository.save(chatMessage);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, chatMessage.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /chat-messages} : get all the chatMessages.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of chatMessages in body.
     */
    @GetMapping("/chat-messages")
    public List<ChatMessage> getAllChatMessages() {
        log.debug("REST request to get all ChatMessages");
        return chatMessageRepository.findAll();
    }

    /**
     * {@code GET  /chat-messages/:id} : get the "id" chatMessage.
     *
     * @param id the id of the chatMessage to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the chatMessage, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/chat-messages/{id}")
    public ResponseEntity<ChatMessage> getChatMessage(@PathVariable String id) {
        log.debug("REST request to get ChatMessage : {}", id);
        Optional<ChatMessage> chatMessage = chatMessageRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(chatMessage);
    }

    /**
     * {@code DELETE  /chat-messages/:id} : delete the "id" chatMessage.
     *
     * @param id the id of the chatMessage to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/chat-messages/{id}")
    public ResponseEntity<Void> deleteChatMessage(@PathVariable String id) {
        log.debug("REST request to delete ChatMessage : {}", id);
        chatMessageRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }
}

package com.spingular.chat.web.rest;

import com.spingular.chat.domain.ChatRoom;
import com.spingular.chat.repository.ChatRoomRepository;
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
 * REST controller for managing {@link com.spingular.chat.domain.ChatRoom}.
 */
@RestController
@RequestMapping("/api")
public class ChatRoomResource {

    private final Logger log = LoggerFactory.getLogger(ChatRoomResource.class);

    private static final String ENTITY_NAME = "chatRoom";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ChatRoomRepository chatRoomRepository;

    public ChatRoomResource(ChatRoomRepository chatRoomRepository) {
        this.chatRoomRepository = chatRoomRepository;
    }

    /**
     * {@code POST  /chat-rooms} : Create a new chatRoom.
     *
     * @param chatRoom the chatRoom to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new chatRoom, or with status {@code 400 (Bad Request)} if the chatRoom has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/chat-rooms")
    public ResponseEntity<ChatRoom> createChatRoom(@RequestBody ChatRoom chatRoom) throws URISyntaxException {
        log.debug("REST request to save ChatRoom : {}", chatRoom);
        if (chatRoom.getId() != null) {
            throw new BadRequestAlertException("A new chatRoom cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ChatRoom result = chatRoomRepository.save(chatRoom);
        return ResponseEntity.created(new URI("/api/chat-rooms/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /chat-rooms} : Updates an existing chatRoom.
     *
     * @param chatRoom the chatRoom to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated chatRoom,
     * or with status {@code 400 (Bad Request)} if the chatRoom is not valid,
     * or with status {@code 500 (Internal Server Error)} if the chatRoom couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/chat-rooms")
    public ResponseEntity<ChatRoom> updateChatRoom(@RequestBody ChatRoom chatRoom) throws URISyntaxException {
        log.debug("REST request to update ChatRoom : {}", chatRoom);
        if (chatRoom.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ChatRoom result = chatRoomRepository.save(chatRoom);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, chatRoom.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /chat-rooms} : get all the chatRooms.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of chatRooms in body.
     */
    @GetMapping("/chat-rooms")
    public List<ChatRoom> getAllChatRooms() {
        log.debug("REST request to get all ChatRooms");
        return chatRoomRepository.findAll();
    }

    /**
     * {@code GET  /chat-rooms/:id} : get the "id" chatRoom.
     *
     * @param id the id of the chatRoom to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the chatRoom, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/chat-rooms/{id}")
    public ResponseEntity<ChatRoom> getChatRoom(@PathVariable String id) {
        log.debug("REST request to get ChatRoom : {}", id);
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(chatRoom);
    }

    /**
     * {@code DELETE  /chat-rooms/:id} : delete the "id" chatRoom.
     *
     * @param id the id of the chatRoom to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/chat-rooms/{id}")
    public ResponseEntity<Void> deleteChatRoom(@PathVariable String id) {
        log.debug("REST request to delete ChatRoom : {}", id);
        chatRoomRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }
}

package com.maxpri.skyengtest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxpri.skyengtest.model.dto.request.MailItemRequest;
import com.maxpri.skyengtest.model.entity.EType;
import com.maxpri.skyengtest.service.MailItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author max_pri
 */

@WebMvcTest(MailItemController.class)
class MailItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MailItemService mailItemService;

    @Test
    void testRegisterMailItem() throws Exception {
        MailItemRequest request = new MailItemRequest(
                EType.LETTER,
                123456,
                321654,
                "Tomsk, Pushkina Str.",
                "Alex"
        );
        doNothing().when(mailItemService).saveMailItem(request);

        mockMvc.perform(post("/api/mail/addMailItem")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Mail successfully added"));
    }

    @Test
    void testMoveMailItem() throws Exception {
        Long mailItemId = 1L;
        int toIndex = 2;
        doNothing().when(mailItemService).moveMailItem(mailItemId, toIndex);

        mockMvc.perform(post("/api/mail/moveMailItem")
                        .param("mailItemId", mailItemId.toString())
                        .param("toIndex", String.valueOf(toIndex)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Mail successfully moved"));
    }

    @Test
    void testAcceptMailItem() throws Exception {
        Long mailItemId = 1L;
        doNothing().when(mailItemService).acceptMailItem(mailItemId);

        mockMvc.perform(post("/api/mail/acceptMailItem")
                        .param("mailItemId", mailItemId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Mail successfully accepted"));
    }

    @Test
    void testReceiveMailItem() throws Exception {
        Long mailItemId = 1L;
        doNothing().when(mailItemService).receiveMailItem(mailItemId);

        mockMvc.perform(post("/api/mail/receiveMailItem")
                        .param("mailItemId", mailItemId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Mail successfully received"));
    }
}
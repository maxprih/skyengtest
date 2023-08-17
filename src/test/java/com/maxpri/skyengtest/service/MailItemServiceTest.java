package com.maxpri.skyengtest.service;

import com.maxpri.skyengtest.exception.InvalidOperationException;
import com.maxpri.skyengtest.exception.NoSuchIndexException;
import com.maxpri.skyengtest.exception.NoSuchMailItemException;
import com.maxpri.skyengtest.exception.NotFinalDestinationException;
import com.maxpri.skyengtest.model.dto.request.MailItemRequest;
import com.maxpri.skyengtest.model.entity.*;
import com.maxpri.skyengtest.repository.MailItemEventRepository;
import com.maxpri.skyengtest.repository.MailItemMovementRepository;
import com.maxpri.skyengtest.repository.MailItemRepository;
import com.maxpri.skyengtest.repository.MailOfficeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author max_pri
 */
@SpringBootTest
class MailItemServiceTest {

    @Autowired
    private MailItemService mailItemService;

    @MockBean
    private MailOfficeRepository mailOfficeRepository;

    @MockBean
    private MailItemRepository mailItemRepository;

    @MockBean
    private MailItemEventRepository mailItemEventRepository;

    @MockBean
    private MailItemMovementRepository mailItemMovementRepository;


    @Test
    @DisplayName("Test saveMailItem good run")
    void testSaveMailItem() {
        MailItemRequest request = new MailItemRequest(
                EType.LETTER,
                123456,
                321654,
                "Tomsk, Pushkina Str.",
                "Alex"
        );

        when(mailOfficeRepository.existsByIndex(request.getStartIndex())).thenReturn(true);
        when(mailOfficeRepository.existsByIndex(request.getReceiverIndex())).thenReturn(true);

        mailItemService.saveMailItem(request);

        verify(mailItemRepository).save(any(MailItem.class));
        verify(mailItemEventRepository).save(any(MailItemEvent.class));
    }

    @Test
    @DisplayName("Test saveMailItem with NoSuchIndexException")
    void testSaveMailItemWithNoSuchIndexException() {
        MailItemRequest request = new MailItemRequest(
                EType.PACKAGE,
                345345342,
                42345423,
                "Moscow, Pushkina Str.",
                "Alena"
        );

        when(mailOfficeRepository.existsByIndex(request.getStartIndex())).thenReturn(false);
        when(mailOfficeRepository.existsByIndex(request.getReceiverIndex())).thenReturn(false);

        assertThrows(NoSuchIndexException.class, () -> mailItemService.saveMailItem(request));

        verify(mailItemRepository, never()).save(any(MailItem.class));
        verify(mailItemEventRepository, never()).save(any(MailItemEvent.class));
    }

    @Test
    @DisplayName("Test moveMailItem good run")
    void testMoveMailItem() {
        Long mailItemId = 1L;
        int toIndex = 2;

        MailItem mailItem = new MailItem();
        mailItem.setStatus(EStatus.AT_POST_OFFICE);
        mailItem.setCurrentIndex(1);

        when(mailOfficeRepository.existsByIndex(toIndex)).thenReturn(true);
        when(mailItemRepository.existsById(mailItemId)).thenReturn(true);
        when(mailItemRepository.findById(mailItemId)).thenReturn(Optional.of(mailItem));

        mailItemService.moveMailItem(mailItemId, toIndex);

        verify(mailItemRepository).save(mailItem);
        verify(mailItemEventRepository).save(any(MailItemEvent.class));
        verify(mailItemMovementRepository).save(any(MailItemMovement.class));

        assertEquals(EStatus.IN_TRANSIT, mailItem.getStatus());
        assertEquals(toIndex, mailItem.getMovingToIndex());
    }

    @Test
    @DisplayName("Test moveMailItem with NoSuchIndexException")
    void testMoveMailItemWithNoSuchIndexException() {
        Long mailItemId = 1L;
        int toIndex = 2;

        when(mailOfficeRepository.existsByIndex(toIndex)).thenReturn(false);

        assertThrows(NoSuchIndexException.class, () -> mailItemService.moveMailItem(mailItemId, toIndex));

        verify(mailItemRepository, never()).save(any(MailItem.class));
        verify(mailItemEventRepository, never()).save(any(MailItemEvent.class));
        verify(mailItemMovementRepository, never()).save(any(MailItemMovement.class));
    }

    @Test
    @DisplayName("Test acceptMailItem good run")
    void testAcceptMailItem() {
        Long mailItemId = 1L;
        MailItem mailItem = new MailItem();
        mailItem.setStatus(EStatus.IN_TRANSIT);
        mailItem.setMovingToIndex(2);

        when(mailItemRepository.existsById(mailItemId)).thenReturn(true);
        when(mailItemRepository.findById(mailItemId)).thenReturn(Optional.of(mailItem));

        mailItemService.acceptMailItem(mailItemId);

        verify(mailItemRepository).save(mailItem);
        verify(mailItemEventRepository).save(any(MailItemEvent.class));

        assertEquals(EStatus.AT_POST_OFFICE, mailItem.getStatus());
        assertEquals(2, mailItem.getCurrentIndex());
        assertNull(mailItem.getMovingToIndex());
    }

    @Test
    @DisplayName("Test acceptMailItem with NoSuchMailItemException")
    void testAcceptMailItemWithNoSuchMailItemException() {
        Long mailItemId = 1L;

        when(mailItemRepository.existsById(mailItemId)).thenReturn(false);

        assertThrows(NoSuchMailItemException.class, () -> mailItemService.acceptMailItem(mailItemId));

        verify(mailItemRepository, never()).save(any(MailItem.class));
        verify(mailItemEventRepository, never()).save(any(MailItemEvent.class));
    }

    @Test
    @DisplayName("Test acceptMailItem with InvalidOperationException")
    void testAcceptMailItemWithInvalidOperationException() {
        Long mailItemId = 1L;
        MailItem mailItem = new MailItem();
        mailItem.setStatus(EStatus.AT_POST_OFFICE);

        when(mailItemRepository.existsById(mailItemId)).thenReturn(true);
        when(mailItemRepository.findById(mailItemId)).thenReturn(Optional.of(mailItem));

        assertThrows(InvalidOperationException.class, () -> mailItemService.acceptMailItem(mailItemId));

        verify(mailItemRepository, never()).save(any(MailItem.class));
        verify(mailItemEventRepository, never()).save(any(MailItemEvent.class));
    }

    @Test
    @DisplayName("Test receiveMailItem good run")
    void testReceiveMailItemDelivered() {
        Long mailItemId = 1L;
        MailItem mailItem = new MailItem();
        mailItem.setCurrentIndex(2);
        mailItem.setReceiverIndex(2);

        when(mailItemRepository.existsById(mailItemId)).thenReturn(true);
        when(mailItemRepository.findById(mailItemId)).thenReturn(Optional.of(mailItem));

        mailItemService.receiveMailItem(mailItemId);

        verify(mailItemRepository).save(mailItem);
        verify(mailItemEventRepository).save(any(MailItemEvent.class));

        assertEquals(EStatus.DELIVERED, mailItem.getStatus());
    }

    @Test
    @DisplayName("Test receiveMailItem with NotFinalDestinationException")
    void testReceiveMailItemNotFinalDestinationException() {
        Long mailItemId = 1L;
        MailItem mailItem = new MailItem();
        mailItem.setCurrentIndex(2);
        mailItem.setReceiverIndex(3);

        when(mailItemRepository.existsById(mailItemId)).thenReturn(true);
        when(mailItemRepository.findById(mailItemId)).thenReturn(Optional.of(mailItem));

        assertThrows(NotFinalDestinationException.class, () -> mailItemService.receiveMailItem(mailItemId));

        verify(mailItemRepository, never()).save(any(MailItem.class));
        verify(mailItemEventRepository, never()).save(any(MailItemEvent.class));
    }

    @Test
    @DisplayName("Test receiveMailItem with NoSuchMailItemException")
    void testReceiveMailItemWithNoSuchMailItemException() {
        Long mailItemId = 1L;

        when(mailItemRepository.existsById(mailItemId)).thenReturn(false);

        assertThrows(NoSuchMailItemException.class, () -> mailItemService.receiveMailItem(mailItemId));

        verify(mailItemRepository, never()).save(any(MailItem.class));
        verify(mailItemEventRepository, never()).save(any(MailItemEvent.class));
    }

    @Test
    @DisplayName("Test getInfo with NoSuchMailItemException")
    void testGetInfoWithNoSuchMailItemException() {
        Long mailItemId = 1L;

        when(mailItemRepository.existsById(mailItemId)).thenReturn(false);

        assertThrows(NoSuchMailItemException.class, () -> mailItemService.getInfo(mailItemId));

        verify(mailItemRepository).existsById(mailItemId);
        verify(mailItemRepository, never()).findById(mailItemId);
    }
}
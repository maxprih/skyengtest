package com.maxpri.skyengtest.service;

import com.maxpri.skyengtest.exception.InvalidOperationException;
import com.maxpri.skyengtest.exception.NoSuchIndexException;
import com.maxpri.skyengtest.exception.NoSuchMailItemException;
import com.maxpri.skyengtest.exception.NotFinalDestinationException;
import com.maxpri.skyengtest.model.dto.request.MailItemRequest;
import com.maxpri.skyengtest.model.dto.response.MailItemHistoryEvent;
import com.maxpri.skyengtest.model.dto.response.MailItemInfoResponse;
import com.maxpri.skyengtest.model.entity.*;
import com.maxpri.skyengtest.repository.MailItemEventRepository;
import com.maxpri.skyengtest.repository.MailItemMovementRepository;
import com.maxpri.skyengtest.repository.MailItemRepository;
import com.maxpri.skyengtest.repository.MailOfficeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author max_pri
 */
@Service
public class MailItemService {
    private final MailItemRepository mailItemRepository;
    private final MailOfficeRepository mailOfficeRepository;
    private final MailItemMovementRepository mailItemMovementRepository;
    private final MailItemEventRepository mailItemEventRepository;

    @Autowired
    public MailItemService(MailItemRepository mailItemRepository, MailOfficeRepository mailOfficeRepository, MailItemMovementRepository mailItemMovementRepository, MailItemEventRepository mailItemEventRepository) {
        this.mailItemRepository = mailItemRepository;
        this.mailOfficeRepository = mailOfficeRepository;
        this.mailItemMovementRepository = mailItemMovementRepository;
        this.mailItemEventRepository = mailItemEventRepository;
    }

    @Transactional
    public void saveMailItem(MailItemRequest mailItemRequest) {
        if (!(mailOfficeRepository.existsByIndex(mailItemRequest.getStartIndex()) ||
                mailOfficeRepository.existsByIndex(mailItemRequest.getReceiverIndex()))) {
            throw new NoSuchIndexException();
        }

        MailItem mailItem = MailItem.builder()
                .type(mailItemRequest.getType())
                .status(EStatus.AT_POST_OFFICE)
                .startIndex(mailItemRequest.getStartIndex())
                .currentIndex(mailItemRequest.getStartIndex())
                .receiverIndex(mailItemRequest.getReceiverIndex())
                .receiverAddress(mailItemRequest.getReceiverAddress())
                .receiverName(mailItemRequest.getReceiverName())
                .build();
        MailItemEvent mailItemEvent = MailItemEvent.builder()
                .mailItem(mailItem)
                .eventType(EEvent.REGISTERED)
                .index(mailItemRequest.getStartIndex())
                .eventDate(LocalDateTime.now())
                .build();

        mailItemRepository.save(mailItem);
        mailItemEventRepository.save(mailItemEvent);
    }

    @Transactional
    public void moveMailItem(Long mailItemId, int toIndex) {
        if (!mailOfficeRepository.existsByIndex(toIndex)) {
            throw new NoSuchIndexException();
        }

        if (!mailItemRepository.existsById(mailItemId)) {
            throw new NoSuchMailItemException();
        }
        MailItem mailItem = mailItemRepository.findById(mailItemId).get();

        if (mailItem.getStatus() != EStatus.AT_POST_OFFICE) {
            throw new InvalidOperationException();
        }

        if (mailItem.getCurrentIndex() == toIndex) {
            throw new InvalidOperationException();
        }
        mailItem.setMovingToIndex(toIndex);
        mailItem.setStatus(EStatus.IN_TRANSIT);

        MailItemMovement mailItemMovement = MailItemMovement.builder()
                .mailItem(mailItem)
                .sourceIndex(mailItem.getCurrentIndex())
                .destinationIndex(toIndex)
                .movementDate(LocalDateTime.now())
                .build();

        MailItemEvent mailItemEvent = MailItemEvent.builder()
                .mailItem(mailItem)
                .eventType(EEvent.SENT)
                .index(mailItem.getCurrentIndex())
                .eventDate(LocalDateTime.now())
                .build();

        mailItemRepository.save(mailItem);
        mailItemEventRepository.save(mailItemEvent);
        mailItemMovementRepository.save(mailItemMovement);

    }

    @Transactional
    public void acceptMailItem(Long mailItemId) {
        if (!mailItemRepository.existsById(mailItemId)) {
            throw new NoSuchMailItemException();
        }

        MailItem mailItem = mailItemRepository.findById(mailItemId).get();

        if (mailItem.getStatus() != EStatus.IN_TRANSIT) {
            throw new InvalidOperationException();
        }

        mailItem.setCurrentIndex(mailItem.getMovingToIndex());
        mailItem.setMovingToIndex(null);
        mailItem.setStatus(EStatus.AT_POST_OFFICE);

        MailItemEvent mailItemEvent = MailItemEvent.builder()
                .mailItem(mailItem)
                .eventType(EEvent.RECEIVED)
                .index(mailItem.getCurrentIndex())
                .eventDate(LocalDateTime.now())
                .build();


        mailItemRepository.save(mailItem);
        mailItemEventRepository.save(mailItemEvent);
    }

    @Transactional
    public void receiveMailItem(Long mailItemId) {
        if (!mailItemRepository.existsById(mailItemId)) {
            throw new NoSuchMailItemException();
        }

        MailItem mailItem = mailItemRepository.findById(mailItemId).get();

        if (mailItem.getCurrentIndex() == mailItem.getReceiverIndex()) {
            mailItem.setStatus(EStatus.DELIVERED);

            MailItemEvent mailItemEvent = MailItemEvent.builder()
                    .mailItem(mailItem)
                    .eventType(EEvent.DELIVERED)
                    .index(mailItem.getCurrentIndex())
                    .eventDate(LocalDateTime.now())
                    .build();


            mailItemRepository.save(mailItem);
            mailItemEventRepository.save(mailItemEvent);
        } else {
            throw new NotFinalDestinationException();
        }
    }

    public MailItemInfoResponse getInfo(Long mailItemId) {
        if (!mailItemRepository.existsById(mailItemId)) {
            throw new NoSuchMailItemException();
        }

        MailItem mailItem = mailItemRepository.findById(mailItemId).get();

        List<MailItemHistoryEvent> history = getHistory(mailItemId);

        return new MailItemInfoResponse(
                mailItem.getType(),
                mailItem.getStatus(),
                mailItem.getStartIndex(),
                mailItem.getReceiverIndex(),
                mailItem.getReceiverAddress(),
                mailItem.getReceiverName(),
                history
        );
    }

    private List<MailItemHistoryEvent> getHistory(Long mailItemId) {
        List<MailItemMovement> movementEvents = mailItemMovementRepository.findByMailItemIdOrderByMovementDate(mailItemId);
        List<MailItemEvent> mailEvents = mailItemEventRepository.findByMailItemIdOrderByEventDate(mailItemId);

        List<MailItemHistoryEvent> history = new ArrayList<>();

        for (MailItemMovement mailItemMovement : movementEvents) {
            history.add(convertMovementToHistoryEvent(mailItemMovement));
        }

        for (MailItemEvent mailItemEvent : mailEvents) {
            history.add(convertEventToHistoryEvent(mailItemEvent));
        }

        history.sort(Comparator.comparing(MailItemHistoryEvent::getDate));

        return history;
    }

    private MailItemHistoryEvent convertEventToHistoryEvent(MailItemEvent mailItemEvent) {
        String description = mailItemEvent.getEventType().getType() + " at " + mailItemEvent.getEventDate().toString();
        return new MailItemHistoryEvent(description, mailItemEvent.getEventDate());
    }

    private MailItemHistoryEvent convertMovementToHistoryEvent(MailItemMovement mailItemMovement) {
        String description = "Moved from index " + mailItemMovement.getSourceIndex() +
                " to index " + mailItemMovement.getDestinationIndex() +
                " at " + mailItemMovement.getMovementDate().toString();
        return new MailItemHistoryEvent(description, mailItemMovement.getMovementDate());
    }
}

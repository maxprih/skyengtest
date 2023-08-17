package com.maxpri.skyengtest.controller;

import com.maxpri.skyengtest.model.dto.request.MailItemRequest;
import com.maxpri.skyengtest.model.dto.response.MailItemInfoResponse;
import com.maxpri.skyengtest.model.dto.response.MessageResponse;
import com.maxpri.skyengtest.service.MailItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * @author max_pri
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/mail")
public class MailItemController {

    private final MailItemService mailItemService;

    @Autowired
    public MailItemController(MailItemService mailItemService) {
        this.mailItemService = mailItemService;
    }

    @PostMapping("/addMailItem")
    public ResponseEntity<MessageResponse> registerMailItem(@RequestBody @Valid MailItemRequest mailItemRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            MessageResponse messageResponse = new MessageResponse("All fields must be specified");
            return new ResponseEntity<>(messageResponse, HttpStatus.BAD_REQUEST);
        }
        mailItemService.saveMailItem(mailItemRequest);

        MessageResponse messageResponse = new MessageResponse("Mail successfully added");
        return new ResponseEntity<>(messageResponse, HttpStatus.CREATED);
    }

    @PostMapping("/moveMailItem")
    public ResponseEntity<MessageResponse> moveMailItem(@RequestParam Long mailItemId, @RequestParam int toIndex) {
        mailItemService.moveMailItem(mailItemId, toIndex);

        MessageResponse messageResponse = new MessageResponse("Mail successfully moved");
        return new ResponseEntity<>(messageResponse, HttpStatus.OK);
    }

    @PostMapping("/acceptMailItem")
    public ResponseEntity<MessageResponse> acceptMailItem(@RequestParam Long mailItemId) {
        mailItemService.acceptMailItem(mailItemId);

        MessageResponse messageResponse = new MessageResponse("Mail successfully accepted");
        return new ResponseEntity<>(messageResponse, HttpStatus.OK);
    }

    @PostMapping("/receiveMailItem")
    public ResponseEntity<MessageResponse> receiveMailItem(@RequestParam Long mailItemId) {
        mailItemService.receiveMailItem(mailItemId);

        MessageResponse messageResponse = new MessageResponse("Mail successfully received");
        return new ResponseEntity<>(messageResponse, HttpStatus.OK);
    }

    @GetMapping("/getInfoMailItem")
    public ResponseEntity<MailItemInfoResponse> moveMailItem(@RequestParam Long mailItemId) {
        MailItemInfoResponse info = mailItemService.getInfo(mailItemId);
        return ResponseEntity.ok(info);
    }
}

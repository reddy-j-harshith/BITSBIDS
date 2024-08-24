package com.OOPS.bits_bids.Controller;

import com.OOPS.bits_bids.Entity.Bid;
import com.OOPS.bits_bids.Entity.Message;
import com.OOPS.bits_bids.Entity.User;
import com.OOPS.bits_bids.Repository.BidRepository;
import com.OOPS.bits_bids.Repository.MessageRepository;
import com.OOPS.bits_bids.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageRepository messageRepository;
    private final BidRepository bidRepository;
    private final UserRepository userRepository;


    @PostMapping("/send/{lenderBitsId}/{bidId}")
    public ResponseEntity<?> sendMessage(@PathVariable String lenderBitsId,
                                         @PathVariable String bidId,
                                         @RequestBody String message){

        User lender = userRepository
                .findByBitsId(lenderBitsId)
                .orElseThrow(() -> new UsernameNotFoundException("Lender Id not found!"));

        Bid bid = bidRepository
                .findById(Long.parseLong(bidId))
                .orElseThrow(() -> new RuntimeException("Bid not found!"));

        User borrower = userRepository
                .findByBitsId(bid.getProduct().getSeller().getBitsId())
                .orElseThrow(() -> new UsernameNotFoundException("borrower Id not found!"));

        Message newMessage = new Message();
        newMessage.setLender(lender);
        newMessage.setBorrower(borrower);
        newMessage.setBid(bid);
        newMessage.setContent(message);

        messageRepository.save(newMessage);

        return ResponseEntity.ok("Message sent successfully!");


    }

    @GetMapping("/get/{lenderBitsId}/{bidsId}")
    public ResponseEntity<?> getMessages(@PathVariable String lenderBitsId,
                                         @PathVariable String bidsId){

        User lender = userRepository
                .findByBitsId(lenderBitsId)
                .orElseThrow(() -> new UsernameNotFoundException("Lender Id not found!"));

        Bid bid = bidRepository
                .findById(Long.parseLong(bidsId))
                .orElseThrow(() -> new RuntimeException("Bid not found!"));

        User borrower = userRepository
                .findByBitsId(bid.getProduct().getSeller().getBitsId())
                .orElseThrow(() -> new UsernameNotFoundException("borrower Id not found!"));

        return ResponseEntity.ok(messageRepository.findByLenderAndBorrowerAndBid(lender, borrower, bid));
    }
}

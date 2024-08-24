package com.OOPS.bits_bids.Controller;

import com.OOPS.bits_bids.DTO.CreditDTO;
import com.OOPS.bits_bids.Entity.User;
import com.OOPS.bits_bids.Repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@RestController
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    @PutMapping("/add-credits")
    public ResponseEntity<?> addCreditsToUser(@Valid @RequestBody CreditDTO creditDTO){

        User user = userRepository.
                findByBitsId(creditDTO.getBitsId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found. Please try again"));

        user.setCredits(user.getCredits() + creditDTO.getAddCredits());
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Credits added successfully.");
    }

    @GetMapping("/test")
    public ResponseEntity<?> getTestAPI3(Principal principal){
        return ResponseEntity.ok("Admin API accessed by " + principal.getName());
    }

}

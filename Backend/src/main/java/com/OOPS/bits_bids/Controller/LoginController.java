package com.OOPS.bits_bids.Controller;

import com.OOPS.bits_bids.DTO.NewUserDTO;
import com.OOPS.bits_bids.Entity.User;
import com.OOPS.bits_bids.Repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class LoginController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Pattern BITS_MAIL_PATTERN = Pattern.compile("(f20\\d{6})@hyderabad\\.bits-pilani\\.ac\\.in");

    @PostMapping("/sign-up")
    public ResponseEntity<?> signIn(@Valid @RequestBody NewUserDTO newUser) {
        try {
            // Extract bits_id from bits_mail
            Matcher matcher = BITS_MAIL_PATTERN.matcher(newUser.getBits_mail());
            if (!matcher.matches()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid bits_mail format.");
            }
            String bitsId = matcher.group(1);

            // Create and save new user
            User user = new User();
            user.setBitsId(bitsId);
            user.setMail(newUser.getBits_mail());
            user.setPassword(passwordEncoder.encode(newUser.getPassword()));
            user.setHostel(newUser.getHostel());
            user.setFirstName(newUser.getFirstName());
            user.setLastName(newUser.getLastName());
            user.setRoles("ROLE_USER");

            userRepository.save(user);

            return ResponseEntity.status(HttpStatus.CREATED).body("New user has been created.");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("The username already exists.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during sign-up.");
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder errors = new StringBuilder();
        ex.getBindingResult().getAllErrors().forEach(error -> errors.append(error.getDefaultMessage()).append("; "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.toString());
    }
}

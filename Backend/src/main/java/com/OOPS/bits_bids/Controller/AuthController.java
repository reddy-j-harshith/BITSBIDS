package com.OOPS.bits_bids.Controller;

import com.OOPS.bits_bids.Config.CustomUserDetailsService;
import com.OOPS.bits_bids.Config.UserConfig;
import com.OOPS.bits_bids.DTO.*;
import com.OOPS.bits_bids.Entity.User;
import com.OOPS.bits_bids.Repository.UserRepository;
import com.OOPS.bits_bids.Response.AuthenticationResponse;
import com.OOPS.bits_bids.Security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    private static final Pattern BITS_MAIL_PATTERN = Pattern.compile("(f20\\d{6})@hyderabad\\.bits-pilani\\.ac\\.in");

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationDTO authenticationDTO) throws Exception {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationDTO.getUsername(), authenticationDTO.getPassword())
            );
        } catch (Exception e) {
            throw new Exception("Incorrect username or password", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationDTO.getUsername());

        final String jwt = jwtUtil.generateToken((UserConfig) userDetails);
        final String refreshToken = jwtUtil.generateRefreshToken((UserConfig) userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwt, refreshToken));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenDTO request) {
        final String refreshToken = request.getRefreshToken();
        final String username = jwtUtil.extractUsername(refreshToken);
        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (jwtUtil.validateToken(refreshToken, (UserConfig) userDetails)) {
            final String jwt = jwtUtil.generateToken((UserConfig) userDetails);
            return ResponseEntity.ok(new AuthenticationResponse(jwt, refreshToken));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signIn(@Valid @RequestBody NewUserDTO newUser) {
        // Extract bits_id from bits_mail
        Matcher matcher = BITS_MAIL_PATTERN.matcher(newUser.getBitsMail());
        if (!matcher.matches()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid bits_mail format.");
        }
        String bitsId = matcher.group(1);

        // Check if a user with the same email already exists
        if (userRepository.findByMail(newUser.getBitsMail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("The email already exists.");
        }

        // Create and save new user
        User user = new User();
        user.setBitsId(bitsId);
        user.setMail(newUser.getBitsMail());
        user.setPassword(passwordEncoder.encode(newUser.getPassword()));
        user.setHostel(newUser.getHostel());
        user.setFirstName(newUser.getFirstName());
        user.setLastName(newUser.getLastName());
        user.setRoles("ROLE_USER");

        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body("New user has been created.");
    }

    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody UpdateDTO updateDTO){

        User user = userRepository.findByBitsId(updateDTO.getBitsId()).orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(updateDTO.getOldPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Old password is incorrect.");
        }

        user.setPassword(passwordEncoder.encode(updateDTO.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("Password updated successfully.");

    }

    @GetMapping("/secure-endpoint")
    @PreAuthorize("hasRole('USER')")
    public String secureEndpoint() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return "Access granted to secure endpoint! Username: " + username;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder errors = new StringBuilder();
        ex.getBindingResult().getAllErrors().forEach(error -> errors.append(error.getDefaultMessage()).append("; "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.toString());
    }
}

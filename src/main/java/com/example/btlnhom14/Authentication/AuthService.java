package com.example.btlnhom14.Authentication;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.btlnhom14.Config.JwtService;
import com.example.btlnhom14.Entities.User;
import com.example.btlnhom14.Repositories.UserRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AuthService {
	@Autowired
	private final PasswordEncoder passwordEncoder;

	@Autowired
	private final UserRepository userRepository;

	@Autowired
	private final AuthenticationManager authenticationManager;

	@Autowired
	private final JwtService jwtService;

	public ResponseEntity<AuthResponse> signUp(SignUpRequest request) {
		if(userRepository.findByUsername(request.getUsername()).isPresent()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(AuthResponse.builder().message("Nguoi dung da ton tai").build());
		}


		User user = new User();
		user.setUsername(request.getUsername());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole("USER");

		userRepository.save(user);
		return ResponseEntity.status(HttpStatus.OK)
				.body(AuthResponse.builder().message("Tao moi user thanh cong").build());
	}

	public ResponseEntity<AuthResponse> authenticate(AuthRequest request) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						request.getUsername(),
						request.getPassword()
				)
		);
		var user = userRepository.findByUsername(request.getUsername()).orElseThrow();
		var jwtToken = jwtService.generateToken((UserDetails) user);
		return ResponseEntity.status(HttpStatus.OK)
				.body(AuthResponse.builder()
						.token(jwtToken)
						.username(user.getUsername())
						.userId(String.valueOf(user.getUserId()))
						.role(user.getRole())
						.build()
				);
	}

	public ResponseEntity<AuthResponse> signUpAdmin(SignUpRequest request) {
		if(userRepository.findByUsername(request.getUsername()).isPresent()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(AuthResponse.builder().message("Admin da ton tai").build());
		}


		User user = new User();
		user.setUsername(request.getUsername());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole("ADMIN");

		userRepository.save(user);
		return ResponseEntity.status(HttpStatus.OK)
				.body(AuthResponse.builder().message("Tao moi admin thanh cong").build());
	}
}


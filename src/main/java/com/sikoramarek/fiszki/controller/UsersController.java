package com.sikoramarek.fiszki.controller;

import com.sikoramarek.fiszki.authentication.UserDetailsServiceImpl;
import com.sikoramarek.fiszki.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.ResponseEntity.ok;

@RestController
public class UsersController extends AbstractController {

	private UserDetailsService userDetailsService;
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	public UsersController(
			BCryptPasswordEncoder bCryptPasswordEncoder,
			UserDetailsServiceImpl userDetailsService) {
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.userDetailsService = userDetailsService;
	}

	@GetMapping("users")
	public ResponseEntity<List<UserModel>> getAllUsers() {
		List<UserModel> allUsers = usersDAO.findAll();
		if (!allUsers.isEmpty()) {
			for (UserModel user : allUsers
			) {
				user.setPassword(null);
			}
			return new ResponseEntity<>(allUsers, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("users/{user_id}")
	public ResponseEntity<UserModel> getUser(@PathVariable("user_id") long userId) {
		Optional<UserModel> user = usersDAO.findById(userId);
		if (user.isPresent()) {
			UserModel userToReturn = user.get();
			userToReturn.setPassword(null);
			return new ResponseEntity<>(userToReturn, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping("users")
	public ResponseEntity<UserModel> newUser(@RequestBody UserModel newUser) {
		if (newUser != null) {
			try {
				newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
				usersDAO.save(newUser);
			} catch (DataAccessException e) {
				e.printStackTrace();
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			}
			return new ResponseEntity<>(newUser, HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@GetMapping("/me")
	public ResponseEntity currentUser(Principal principal, HttpServletRequest request) {
		System.out.println("Sec context: "+SecurityContextHolder.getContext().getAuthentication().getAuthorities());
		System.out.println("principal: "+principal.getName());
		System.out.println("userdetailserv "+userDetailsService.loadUserByUsername(principal.getName()));
		System.out.println("reeeeeeeeeeeeeeeprinc "+request.getUserPrincipal());
//		UserPrincipal userDetails = (UserPrincipal) principal;
//		Map<Object, Object> model = new HashMap<>();
//		model.put("username", principal.getName());
//		model.put("roles", userDetails.getAuthorities()
//				.stream()
//				.map(a -> a.getAuthority())
//				.collect(toList())
//		);
		return ok("ok");
	}

}

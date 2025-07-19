package com.rueloparente.userservice.web;

import com.rueloparente.userservice.domain.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final Map<String, User> userProfiles = new ConcurrentHashMap<>();

  public UserController() {
    userProfiles.put("9d9b5e61-bbe7-4325-8773-a5714ede1eb7",new User("9d9b5e61-bbe7-4325-8773-a5714ede1eb7", "testuser", "testuser@example.com", "Test", "User"));
    userProfiles.put("1ff8b2bf-c079-4c97-8e20-08742451895e", new User("1ff8b2bf-c079-4c97-8e20-08742451895e", "testmanager", "testmanager@example.com", "Test", "Manager"));
  }


  @GetMapping("/me")
  @PreAuthorize("hasRole('user')")
  public ResponseEntity<User> getMyProfile(Authentication authentication) {
    Jwt jwt = (Jwt) authentication.getPrincipal();
    String authenticatedUserId = jwt.getSubject();
    User user = userProfiles.get(authenticatedUserId);
    return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
  }

  @PutMapping("/me")
  @PreAuthorize("hasRole('user')")
  public ResponseEntity<User> updateMyProfile(@RequestBody User updatedUser, Authentication authentication) {
    Jwt jwt = (Jwt) authentication.getPrincipal();
    String authenticatedUserId = jwt.getSubject();

    if (userProfiles.containsKey(authenticatedUserId)) {
      User profileToUpdate = userProfiles.get(authenticatedUserId);
      profileToUpdate.setEmail(updatedUser.getEmail());
      profileToUpdate.setUsername(updatedUser.getUsername());
      profileToUpdate.setFirstName(updatedUser.getFirstName());
      profileToUpdate.setLastName(updatedUser.getLastName());
      userProfiles.put(authenticatedUserId, profileToUpdate);
      return ResponseEntity.ok(profileToUpdate);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('manager')")
  public ResponseEntity<User> getUserProfileByManager(@PathVariable String id) {
    User user = userProfiles.get(id);
    return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('manager')")
  public ResponseEntity<User> updateUserProfileByManager(@PathVariable String id, @RequestBody User updatedUser) {
    if (userProfiles.containsKey(id)) {
      User profileToUpdate = userProfiles.get(id);
      profileToUpdate.setEmail(updatedUser.getEmail());
      profileToUpdate.setUsername(updatedUser.getUsername());
      profileToUpdate.setFirstName(updatedUser.getFirstName());
      profileToUpdate.setLastName(updatedUser.getLastName());
      userProfiles.put(id, profileToUpdate);
      return ResponseEntity.ok(profileToUpdate);
    } else {
      return ResponseEntity.notFound().build();
    }
  }
}

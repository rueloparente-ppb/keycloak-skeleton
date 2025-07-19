package com.rueloparente.helloservice.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hello")
public class HelloController {

  @GetMapping("/user")
  @PreAuthorize("hasRole('user')")
  public String helloUser() {
    return "Hello User";
  }

  @GetMapping("/manager")
  @PreAuthorize("hasRole('manager')")
  public String helloManager() {
    return "Hello Manager";
  }
}
package com.example.AddressBook.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "polar")
public class AddressBookProperties {
  private String greeting;

  public String getGreeting() {
      return greeting;
  }

  public void setGreeting(String greeting) {
    this.greeting = greeting;
  }  
}

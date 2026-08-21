package com.example.AddressBook.integration;

import com.example.AddressBook.dto.ContactRequest;
import com.example.AddressBook.dto.ContactResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ContactIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    //@Test
    void createAndListAndUnique_andDelete() {
        String base = "http://localhost:" + port + "/api";

        ContactRequest r1 = new ContactRequest();
        r1.setName("Alice"); r1.setPhone("+111");

        ResponseEntity<ContactResponse> resp = restTemplate.postForEntity(base + "/address-books/book1/contacts", r1, ContactResponse.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ContactResponse created = resp.getBody();
        assertThat(created).isNotNull();
        assertThat(created.getName()).isEqualTo("Alice");

        // add same contact to another book
        ContactRequest r2 = new ContactRequest(); r2.setName("Alice"); r2.setPhone("+111");
        ResponseEntity<ContactResponse> resp2 = restTemplate.postForEntity(base + "/address-books/book2/contacts", r2, ContactResponse.class);
        assertThat(resp2.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // list book1
        ResponseEntity<ContactResponse[]> listResp = restTemplate.getForEntity(base + "/address-books/book1/contacts", ContactResponse[].class);
        assertThat(listResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listResp.getBody()).hasSize(1);

        // unique across books should return single unique
        ResponseEntity<ContactResponse[]> uniqueResp = restTemplate.getForEntity(base + "/contacts/unique", ContactResponse[].class);
        assertThat(uniqueResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(uniqueResp.getBody()).hasSize(1);

        // delete by id from book1
        restTemplate.delete(base + "/address-books/book1/contacts/" + created.getId());

        ResponseEntity<ContactResponse[]> listAfterDelete = restTemplate.getForEntity(base + "/address-books/book1/contacts", ContactResponse[].class);
        assertThat(listAfterDelete.getBody()).isEmpty();
    }
}

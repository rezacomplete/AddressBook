package com.example.AddressBook.integration;

import com.example.AddressBook.dto.AddressBookResponse;
import com.example.AddressBook.dto.ContactResponse;
import com.example.AddressBook.dto.ContactResponseWithId;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ContactIntegrationTest {

    @LocalServerPort
    private int port;

    @Test
    void createAddressBookAndAddContactAndListContactsAndRemoveContactAndRemoveAddressBook() {
        String base = "http://localhost:" + port + "/api";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 1) create address book
        Map<String, String> abReq = Map.of("name", "IntegrationBook");
        HttpEntity<Map<String, String>> abEntity = new HttpEntity<>(abReq, headers);
        ResponseEntity<AddressBookResponse> abResp = restTemplate.postForEntity(base + "/address-books", abEntity, AddressBookResponse.class);
        assertThat(abResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(abResp.getBody()).isNotNull();
        assertThat(abResp.getBody().getName()).isEqualTo("IntegrationBook");

        // 2) add contact
        Map<String, String> contactReq = Map.of("name", "Alice", "phone", "+1234567890");
        HttpEntity<Map<String, String>> contactEntity = new HttpEntity<>(contactReq, headers);
        ResponseEntity<ContactResponseWithId> contactResp = restTemplate.postForEntity(base + "/address-books/IntegrationBook/contacts", contactEntity, ContactResponseWithId.class);

        assertThat(contactResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(contactResp.getBody()).isNotNull();

        Long contactId1 = contactResp.getBody().getId();
        assertThat(contactId1).isNotNull();

        // 2) add another contact
        contactReq = Map.of("name", "Reza", "phone", "0422032600");
        contactEntity = new HttpEntity<>(contactReq, headers);
        contactResp = restTemplate.postForEntity(base + "/address-books/IntegrationBook/contacts", contactEntity, ContactResponseWithId.class);

        assertThat(contactResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(contactResp.getBody()).isNotNull();

        Long contactId2 = contactResp.getBody().getId();
        assertThat(contactId2).isNotNull();

        // 3) list contacts
        ResponseEntity<ContactResponse[]> listResp = restTemplate.getForEntity(base + "/address-books/IntegrationBook/contacts?page=1&size=1", ContactResponse[].class);
        assertThat(listResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listResp.getBody()).isNotNull();
        assertThat(listResp.getBody()).hasSize(1);

        // 4) delete contact
        ResponseEntity<Void> delContactResp = restTemplate.exchange(base + "/address-books/IntegrationBook/contacts/" + contactId1, HttpMethod.DELETE, null, Void.class);
        assertThat(delContactResp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        delContactResp = restTemplate.exchange(base + "/address-books/IntegrationBook/contacts/" + contactId2, HttpMethod.DELETE, null, Void.class);
        assertThat(delContactResp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // 5) delete address book
        ResponseEntity<Void> delAbResp = restTemplate.exchange(base + "/address-books/IntegrationBook", HttpMethod.DELETE, null, Void.class);
        assertThat(delAbResp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}

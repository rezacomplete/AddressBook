// java
package com.example.AddressBook.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ContactRepositoryPerformanceTest {

    @Autowired
    ContactRepository contactRepository;

    @Autowired
    JdbcTemplate jdbc;

    // adjust sizes to your machine
    private static final int ADDRESS_BOOK_COUNT = 5;
    private static final int CONTACTS_PER_BOOK = 20_000; // total ~100k
    private static final int WARMUP_RUNS = 2;
    private static final int MEASURE_RUNS = 5;

    @BeforeAll
    static void beforeAll() {
        // intentionally left blank; keep for possible global setup
    }

    @Test
    @Transactional
    void measureFindUniqueContactsPerformance() {
        // insert address books
        for (int i = 1; i <= ADDRESS_BOOK_COUNT; i++) {
            jdbc.update("INSERT INTO address_book (name) VALUES (?)", "book-" + i);
        }

        // insert many contacts; create duplicates across address books to exercise the GROUP BY projection
        List<Object[]> batch = new ArrayList<>(ADDRESS_BOOK_COUNT * CONTACTS_PER_BOOK);
        for (int addressBookId = 1; addressBookId <= ADDRESS_BOOK_COUNT; addressBookId++) {
            for (int c = 0; c < CONTACTS_PER_BOOK; c++) {
                // create many duplicate names/phones across address books  by modding
                String name = "name-" + c;
                String phone = "phone-" + c;
                batch.add(new Object[]{name, phone, addressBookId});
                System.out.println(name + " " + phone + " " + addressBookId);
                if (batch.size() >= 2000) {

                    jdbc.batchUpdate("INSERT INTO contact (name, phone, address_book_id) VALUES (?, ?, ?)", batch);
                    batch.clear();
                }
            }
        }
        if (!batch.isEmpty()) jdbc.batchUpdate("INSERT INTO contact (name, phone, address_book_id) VALUES (?, ?, ?)", batch);

        // Warm up - allow JPA/Hibernate to initialize caches and query plan
        for (int i = 0; i < WARMUP_RUNS; i++) {
            contactRepository.findUniqueContacts();
        }

        // Measure several runs and capture timings
        long totalNanos = 0;
        long fastest = Long.MAX_VALUE;
        for (int i = 0; i < MEASURE_RUNS; i++) {
            long start = System.nanoTime();
            contactRepository.findUniqueContacts();
            long elapsed = System.nanoTime() - start;
            totalNanos += elapsed;
            fastest = Math.min(fastest, elapsed);
            System.out.println("Run " + (i + 1) + " elapsed ms: " + (elapsed / 1_000_000));
        }

        long avgMs = (totalNanos / MEASURE_RUNS) / 1_000_000;
        long fastestMs = fastest / 1_000_000;
        System.out.println("Average ms: " + avgMs + ", fastest ms: " + fastestMs);

        // example assertion to fail test if too slow (tweak threshold to your expectations)
        assertTrue(avgMs < 2000, "Average query time is too slow: " + avgMs + "ms");
    }
}

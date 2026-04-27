package com.ezra.notificationengine;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

@EmbeddedKafka(partitions = 1, topics = {"notifications", "loan-notifications"})
@SpringBootTest
@ActiveProfiles("test")
class NotificationEngineApplicationTests {

    @Test
    void contextLoads() {
    }

}

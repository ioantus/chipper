package com.itiutiaiev.chipper.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.auditing.DateTimeProvider
import org.springframework.data.mongodb.config.EnableMongoAuditing

import java.time.OffsetDateTime

@Configuration
@EnableMongoAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
class MongoConfig {

    // Provides data for @CreatedDate and @LastModifiedDate
    @Bean(name = "auditingDateTimeProvider")
    DateTimeProvider dateTimeProvider() {
        {it -> Optional.of(OffsetDateTime.now())}
    }

}
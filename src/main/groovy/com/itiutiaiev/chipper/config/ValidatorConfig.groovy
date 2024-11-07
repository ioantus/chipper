package com.itiutiaiev.chipper.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean

@Configuration
class ValidatorConfig {

    @Bean
    ValidatingMongoEventListener validatingMongoEventListener() {
        new ValidatingMongoEventListener(validator())
    }

    @Bean
    LocalValidatorFactoryBean validator() {
        new LocalValidatorFactoryBean()
    }

}

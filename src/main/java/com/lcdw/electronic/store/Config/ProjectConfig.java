package com.lcdw.electronic.store.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProjectConfig {

    @Bean
    public ModelMapper modelMapper ()
    {

        return new ModelMapper();
    }

    @Bean
    public ObjectMapper objectMapper()
    {

        return new ObjectMapper();
    }
}

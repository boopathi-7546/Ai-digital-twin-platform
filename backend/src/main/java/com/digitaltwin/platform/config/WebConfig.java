package com.digitaltwin.platform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Serves locally-stored uploaded files (resumes, profile pictures) under
 * the /uploads/** URL path. In production this should be fronted by
 * Nginx directly instead of the Spring app; kept here for dev convenience.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.file-storage.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = uploadDir.endsWith("/") ? uploadDir : uploadDir + "/";
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + location);
    }
}

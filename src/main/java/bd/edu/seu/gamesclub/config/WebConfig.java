package bd.edu.seu.gamesclub.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration: serves user-uploaded media from the external upload
 * directory under the public {@code /uploads/**} URL space (matching the URLs
 * produced by {@code MediaUrls}).
 */
@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final Path uploadDir;

    public WebConfig(@Value("${app.upload.base-dir:uploads}") String uploadBaseDir) {
        this.uploadDir = Paths.get(uploadBaseDir).toAbsolutePath().normalize();
        // Ensure the directory exists so the resource handler can serve from it
        // and uploads never fail because of a missing folder.
        try {
            Files.createDirectories(uploadDir);
            log.info("Serving uploaded media from: {}", uploadDir);
        } catch (IOException ex) {
            log.warn("Could not create upload directory {}: {}", uploadDir, ex.getMessage());
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = uploadDir.toUri().toString();
        if (!location.endsWith("/")) {
            location = location + "/";
        }
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location)
                .setCachePeriod(3600);
    }
}

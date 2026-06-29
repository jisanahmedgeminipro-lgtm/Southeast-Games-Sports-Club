package bd.edu.seu.gamesclub.config;

import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration: serves user-uploaded media from the external upload
 * directory under the public {@code /uploads/**} URL space (matching the URLs
 * produced by {@code MediaUrls}).
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String uploadBaseDir;

    public WebConfig(@Value("${app.upload.base-dir:uploads}") String uploadBaseDir) {
        this.uploadBaseDir = uploadBaseDir;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = Paths.get(uploadBaseDir).toAbsolutePath().normalize().toUri().toString();
        if (!location.endsWith("/")) {
            location = location + "/";
        }
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location)
                .setCachePeriod(3600);
    }
}

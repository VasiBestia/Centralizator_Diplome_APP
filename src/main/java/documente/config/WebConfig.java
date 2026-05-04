package documente.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Obținem calea absolută către folderul "uploads" din proiectul tău
        String caleFolderUploads = Paths.get("uploads").toAbsolutePath().toUri().toString();

        // Orice cerere care începe cu /uploads/ va fi redirecționată către folderul fizic
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(caleFolderUploads);
    }
}
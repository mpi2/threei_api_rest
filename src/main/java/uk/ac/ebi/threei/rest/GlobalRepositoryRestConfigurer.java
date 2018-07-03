package uk.ac.ebi.threei.rest;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;

@Configuration
public class GlobalRepositoryRestConfigurer extends RepositoryRestConfigurerAdapter {

    private static final String CORS_BASE_PATTERN = "/**";
	private static final String ALLOWED_ORIGINS = "*";
//	private static final String ALLOWED_HEADERS = null;
//	private static final String ALLOWED_METHODS = null;

	@Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
		System.out.println("adding cors support");
        config.getCorsRegistry()
                  .addMapping(CORS_BASE_PATTERN)
                  .allowedOrigins(ALLOWED_ORIGINS);
//                  .allowedHeaders(ALLOWED_HEADERS)
//                  .allowedMethods(ALLOWED_METHODS);
     }

}
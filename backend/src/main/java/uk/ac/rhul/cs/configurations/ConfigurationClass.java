package uk.ac.rhul.cs.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * General configuration class for database credentials in particular.
 *
 * @author Dan Bee
 */
@Configuration
@PropertySource("classpath:database.properties")
public class ConfigurationClass {
}

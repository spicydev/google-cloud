package dev.mirchi.googlecloud.messaging.init;

import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.spring.autoconfigure.pubsub.GcpPubSubAutoConfiguration;
import com.google.cloud.spring.autoconfigure.pubsub.GcpPubSubProperties;
import com.google.cloud.spring.autoconfigure.spanner.GcpSpannerAutoConfiguration;
import com.google.cloud.spring.data.spanner.core.SpannerTemplate;
import com.google.cloud.spring.data.spanner.core.admin.SpannerDatabaseAdminTemplate;
import com.google.cloud.spring.pubsub.PubSubAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationProperties;
import org.springframework.boot.sql.init.DatabaseInitializationSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(GcpPubSubAutoConfiguration.class)
public class PubSubInitializationConfiguration {

    @Bean
    public PubSubScriptInitializer pubSubScriptInitializer(@Autowired PubSubAdmin pubSubAdmin,
                                                           @Autowired GcpPubSubProperties gcpPubSubProperties,
                                                           @Autowired(required = false) SqlInitializationProperties sqlInitializationProperties) {
        return new PubSubScriptInitializer(pubSubAdmin, gcpPubSubProperties, createFrom(sqlInitializationProperties));
    }

    private static DatabaseInitializationSettings createFrom(SqlInitializationProperties properties) {
        DatabaseInitializationSettings settings = new DatabaseInitializationSettings();
        if (Objects.nonNull(properties)) {
            settings.setSchemaLocations(
                    scriptLocations(properties.getSchemaLocations(), "pubsub/topics-subs", properties.getPlatform()));
            settings.setDataLocations(scriptLocations(properties.getDataLocations(), "pubsub/data", properties.getPlatform()));
            settings.setContinueOnError(properties.isContinueOnError());
            settings.setSeparator(properties.getSeparator());
            settings.setEncoding(properties.getEncoding());
            settings.setMode(properties.getMode());
        }
        return settings;
    }

    private static List<String> scriptLocations(List<String> locations, String fallback, String platform) {
        if (locations != null) {
            return locations;
        }
        List<String> fallbackLocations = new ArrayList<>();
        fallbackLocations.add("optional:classpath*:" + fallback + "-" + platform + ".txt");
        fallbackLocations.add("optional:classpath*:" + fallback + ".txt");
        return fallbackLocations;
    }

}

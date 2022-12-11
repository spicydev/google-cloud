package dev.mirchi.googlecloud.config;

import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import com.google.cloud.spring.autoconfigure.spanner.GcpSpannerAutoConfiguration;
import com.google.cloud.spring.data.spanner.core.admin.SpannerDatabaseAdminTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.sql.init.AbstractScriptDatabaseInitializer;
import org.springframework.boot.sql.init.DatabaseInitializationSettings;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.List;

@Component
@AutoConfigureAfter({GcpSpannerAutoConfiguration.class})
public class SpannerScriptDatabaseInitializer extends AbstractScriptDatabaseInitializer {

    private static final Logger log = LoggerFactory.getLogger(SpannerScriptDatabaseInitializer.class);

    private final SpannerDatabaseAdminTemplate spannerDatabaseAdminTemplate;

    /**
     * Creates a new {@link AbstractScriptDatabaseInitializer} that will initialize the
     * database using the given settings.
     *
     * @param settings initialization settings
     */
    public SpannerScriptDatabaseInitializer(SpannerDatabaseAdminTemplate spannerDatabaseAdminTemplate,
                                            DatabaseInitializationSettings settings) {
        super(settings);
        this.spannerDatabaseAdminTemplate = spannerDatabaseAdminTemplate;
    }

    public SpannerDatabaseAdminTemplate getSpannerDatabaseAdminTemplate() {
        return spannerDatabaseAdminTemplate;
    }

    @Override
    protected void runScripts(List<Resource> resources, boolean continueOnError, String separator, Charset encoding) {
        for (Resource resource : resources) {
            log.info("Database Resources INIT Mode: "+ resource.getFilename());
        }
    }
}

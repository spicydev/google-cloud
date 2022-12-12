package dev.mirchi.googlecloud.config;

import com.google.cloud.NoCredentials;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.spring.data.spanner.core.admin.SpannerDatabaseAdminTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.sql.init.AbstractScriptDatabaseInitializer;
import org.springframework.boot.sql.init.DatabaseInitializationSettings;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;

public class SpannerScriptDatabaseInitializer extends AbstractScriptDatabaseInitializer {

    private static final Logger log = LoggerFactory.getLogger(SpannerScriptDatabaseInitializer.class);

    private final SpannerDatabaseAdminTemplate spannerDatabaseAdminTemplate;

    private final SpannerOptions spannerOptions;

    /**
     * Creates a new {@link AbstractScriptDatabaseInitializer} that will initialize the
     * database using the given settings.
     *
     * @param settings initialization settings
     */
    public SpannerScriptDatabaseInitializer(SpannerDatabaseAdminTemplate spannerDatabaseAdminTemplate,
                                            SpannerOptions spannerOptions,
                                            DatabaseInitializationSettings settings) {
        super(settings);
        this.spannerDatabaseAdminTemplate = spannerDatabaseAdminTemplate;
        this.spannerOptions = spannerOptions;
    }

    public SpannerDatabaseAdminTemplate getDatabaseAdminTemplate() {
        return spannerDatabaseAdminTemplate;
    }

    public SpannerOptions getSpannerOptions() {
        return spannerOptions;
    }

    @Override
	protected boolean isEmbeddedDatabase() {
		try {
			return Objects.nonNull(spannerOptions) && spannerOptions.getCredentials() instanceof NoCredentials;
		}
		catch (Exception ex) {
			log.error("Could not determine if datasource is embedded", ex);
			return false;
		}
	}

    @Override
    protected void runScripts(List<Resource> resources, boolean continueOnError, String separator, Charset encoding) {
        for (Resource resource : resources) {
            log.info("Database Resources INIT Mode: "+ resource.getFilename());
            EncodedResource encodedScript = new EncodedResource(resource, Charset.defaultCharset());
            SpannerScriptUtils.executeSqlScript(spannerDatabaseAdminTemplate, encodedScript, true,
                    true, ScriptUtils.DEFAULT_COMMENT_PREFIXES, ScriptUtils.DEFAULT_STATEMENT_SEPARATOR,
                    ScriptUtils.DEFAULT_BLOCK_COMMENT_START_DELIMITER, ScriptUtils.DEFAULT_BLOCK_COMMENT_END_DELIMITER);
        }
    }
}

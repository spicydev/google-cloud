package dev.mirchi.googlecloud.repository.init;

import com.google.cloud.NoCredentials;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.spring.data.spanner.core.SpannerTemplate;
import com.google.cloud.spring.data.spanner.core.admin.SpannerDatabaseAdminTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.sql.init.AbstractScriptDatabaseInitializer;
import org.springframework.boot.sql.init.DatabaseInitializationSettings;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.jdbc.datasource.init.UncategorizedScriptException;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;

public class SpannerScriptDatabaseInitializer extends AbstractScriptDatabaseInitializer {

    private static final Logger log = LoggerFactory.getLogger(SpannerScriptDatabaseInitializer.class);

    private final SpannerDatabaseAdminTemplate spannerDatabaseAdminTemplate;

    private final SpannerTemplate spannerTemplate;

    private final SpannerOptions spannerOptions;

    private final DatabaseInitializationSettings settings;

    /**
     * Creates a new {@link AbstractScriptDatabaseInitializer} that will initialize the
     * database using the given settings.
     *
     * @param settings initialization settings
     */
    public SpannerScriptDatabaseInitializer(SpannerDatabaseAdminTemplate spannerDatabaseAdminTemplate,
                                            SpannerTemplate spannerTemplate,
                                            SpannerOptions spannerOptions,
                                            DatabaseInitializationSettings settings) {
        super(settings);
        this.spannerDatabaseAdminTemplate = spannerDatabaseAdminTemplate;
        this.spannerTemplate = spannerTemplate;
        this.spannerOptions = spannerOptions;
        this.settings = settings;
    }

    public SpannerDatabaseAdminTemplate getDatabaseAdminTemplate() {
        return spannerDatabaseAdminTemplate;
    }

    public SpannerTemplate getSpannerTemplate() {
        return spannerTemplate;
    }

    public SpannerOptions getSpannerOptions() {
        return spannerOptions;
    }

    public DatabaseInitializationSettings getSettings() {
        return settings;
    }

    @Override
	protected boolean isEmbeddedDatabase() {
		try {
			return Objects.nonNull(getSpannerOptions()) && getSpannerOptions().getCredentials() instanceof NoCredentials;
		}
		catch (Exception ex) {
			log.error("Could not determine if spanner datasource is emulated", ex);
			return false;
		}
	}

    @Override
    protected void runScripts(List<Resource> resources, boolean continueOnError, String separator, Charset encoding) {
        for (Resource resource : resources) {
            if (Objects.isNull(resource)) {
                throw new UncategorizedScriptException(
                        "Failed to execute database script due to NULL resource [" + resource + "]");
            }
            EncodedResource encodedScript = new EncodedResource(resource, Charset.defaultCharset());
            SpannerScriptUtils.executeDdlDmlScript(getDatabaseAdminTemplate(), getSpannerTemplate(), encodedScript, true,
                    true, ScriptUtils.DEFAULT_COMMENT_PREFIXES, ScriptUtils.DEFAULT_STATEMENT_SEPARATOR,
                    ScriptUtils.DEFAULT_BLOCK_COMMENT_START_DELIMITER, ScriptUtils.DEFAULT_BLOCK_COMMENT_END_DELIMITER,
                    getSettings().getSchemaLocations().stream().anyMatch(location -> location.contains(resource.getFilename())));
        }
    }
}

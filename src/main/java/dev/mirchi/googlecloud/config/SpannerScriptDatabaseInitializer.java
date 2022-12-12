package dev.mirchi.googlecloud.config;

import com.google.cloud.spanner.admin.database.v1.DatabaseAdminClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.sql.init.AbstractScriptDatabaseInitializer;
import org.springframework.boot.sql.init.DatabaseInitializationSettings;
import org.springframework.core.io.Resource;
import java.nio.charset.Charset;
import java.util.List;

public class SpannerScriptDatabaseInitializer extends AbstractScriptDatabaseInitializer {

    private static final Logger log = LoggerFactory.getLogger(SpannerScriptDatabaseInitializer.class);

    private final DatabaseAdminClient databaseAdminClient;

    /**
     * Creates a new {@link AbstractScriptDatabaseInitializer} that will initialize the
     * database using the given settings.
     *
     * @param settings initialization settings
     */
    public SpannerScriptDatabaseInitializer(DatabaseAdminClient databaseAdminClient,
                                            DatabaseInitializationSettings settings) {
        super(settings);
        this.databaseAdminClient = databaseAdminClient;
    }

    public DatabaseAdminClient getDatabaseAdminTemplate() {
        return databaseAdminClient;
    }

    @Override
	protected boolean isEmbeddedDatabase() {
		try {
			return false;
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
        }
    }
}

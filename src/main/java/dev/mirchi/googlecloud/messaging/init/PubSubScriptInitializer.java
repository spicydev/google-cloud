package dev.mirchi.googlecloud.messaging.init;

import com.google.cloud.NoCredentials;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.spring.autoconfigure.pubsub.GcpPubSubProperties;
import com.google.cloud.spring.data.spanner.core.SpannerTemplate;
import com.google.cloud.spring.data.spanner.core.admin.SpannerDatabaseAdminTemplate;
import com.google.cloud.spring.pubsub.PubSubAdmin;
import dev.mirchi.googlecloud.repository.init.SpannerScriptUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.sql.init.AbstractScriptDatabaseInitializer;
import org.springframework.boot.sql.init.DatabaseInitializationSettings;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.CannotReadScriptException;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.jdbc.datasource.init.UncategorizedScriptException;

import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;

public class PubSubScriptInitializer extends AbstractScriptDatabaseInitializer {

    private static final Logger logger = LoggerFactory.getLogger(PubSubScriptInitializer.class);

    private final PubSubAdmin pubSubAdmin;

    private final GcpPubSubProperties gcpPubSubProperties;

    private final DatabaseInitializationSettings settings;

    /**
     * Creates a new {@link AbstractScriptDatabaseInitializer} that will initialize the
     * database using the given settings.
     *
     * @param settings initialization settings
     */
    public PubSubScriptInitializer(PubSubAdmin pubSubAdmin,
                                   GcpPubSubProperties gcpPubSubProperties,
                                   DatabaseInitializationSettings settings) {
        super(settings);
        this.pubSubAdmin = pubSubAdmin;
        this.gcpPubSubProperties = gcpPubSubProperties;
        this.settings = settings;
    }

    public PubSubAdmin getPubSubAdmin() {
        return pubSubAdmin;
    }

    public GcpPubSubProperties getGcpPubSubProperties() {
        return gcpPubSubProperties;
    }

    public DatabaseInitializationSettings getSettings() {
        return settings;
    }

    @Override
	protected boolean isEmbeddedDatabase() {
		try {
			return Objects.nonNull(getPubSubAdmin()) && Objects.nonNull(getGcpPubSubProperties().getEmulatorHost());
		}
		catch (Exception ex) {
			logger.error("Could not determine if pub/sub is emulated", ex);
			return false;
		}
	}

    @Override
    protected void runScripts(List<Resource> resources, boolean continueOnError, String separator, Charset encoding) {
        for (Resource resource : resources) {
            if (Objects.isNull(resource) || getSettings().getDataLocations().stream()
                    .anyMatch(location -> location.contains(Objects.requireNonNull(resource.getFilename())))) {
                throw new UncategorizedScriptException(
                        "Failed to execute database script due to invalid resource [" + resource + "]");
            }
            EncodedResource encodedScript = new EncodedResource(resource, Charset.defaultCharset());
            logger.debug("Executing PubSub script from " + encodedScript);
            long startTime = System.currentTimeMillis();

            try (LineNumberReader lnr = new LineNumberReader(encodedScript.getReader())) {
                lnr.lines()
                        .map(line -> line.split("\\|"))
                        .forEach(topicSubArr -> {
                            String topic = topicSubArr[0].trim(), subscription = topicSubArr[1].trim();
                            getPubSubAdmin().createTopic(topic);
                            getPubSubAdmin().createSubscription(subscription, topic);
                        });
            } catch (Exception ex) {
                throw new UncategorizedScriptException(
                        "Failed to execute pub/sub script from resource [" + encodedScript + "]", ex);
            }

            long elapsedTime = System.currentTimeMillis() - startTime;
            logger.info("Executed pub/sub script from " + encodedScript + " in " + elapsedTime + " ms.");
        }
    }
}

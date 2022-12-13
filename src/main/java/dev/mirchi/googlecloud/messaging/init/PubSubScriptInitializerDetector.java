package dev.mirchi.googlecloud.messaging.init;

import org.springframework.boot.sql.init.dependency.AbstractBeansOfTypeDatabaseInitializerDetector;
import org.springframework.core.Ordered;

import java.util.Collections;
import java.util.Set;

public class PubSubScriptInitializerDetector extends AbstractBeansOfTypeDatabaseInitializerDetector {

    static final int PRECEDENCE = Ordered.LOWEST_PRECEDENCE - 100;

    @Override
    protected Set<Class<?>> getDatabaseInitializerBeanTypes() {
        return Collections.singleton(PubSubScriptInitializer.class);
    }

    @Override
    public int getOrder() {
        return PRECEDENCE;
    }
}

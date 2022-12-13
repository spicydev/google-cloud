package dev.mirchi.googlecloud.repository.init;

import com.google.cloud.spanner.Statement;
import com.google.cloud.spring.data.spanner.core.SpannerTemplate;
import com.google.cloud.spring.data.spanner.core.admin.SpannerDatabaseAdminTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.*;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpannerScriptUtils extends ScriptUtils {

    private static final Logger logger = LoggerFactory.getLogger(SpannerScriptUtils.class);

    public static void executeDdlDmlScript(SpannerDatabaseAdminTemplate spannerDatabaseAdminTemplate, SpannerTemplate spannerTemplate,
                                           EncodedResource resource, boolean continueOnError,
                                           boolean ignoreFailedDrops, String[] commentPrefixes, @Nullable String separator,
                                           String blockCommentStartDelimiter, String blockCommentEndDelimiter,
                                           boolean isDdlScript) throws ScriptException {

        try {
            logger.debug("Executing SQL script from " + resource);
            long startTime = System.currentTimeMillis();

            String script;
            try (LineNumberReader lnr = new LineNumberReader(resource.getReader())) {
                script =  readScript(lnr, commentPrefixes, separator, blockCommentEndDelimiter);
            }
            catch (IOException ex) {
                throw new CannotReadScriptException(resource, ex);
            }

            if (separator == null) {
                separator = DEFAULT_STATEMENT_SEPARATOR;
            }
            if (!EOF_STATEMENT_SEPARATOR.equals(separator) &&
                    !containsStatementSeparator(resource, script, separator, commentPrefixes,
                            blockCommentStartDelimiter, blockCommentEndDelimiter)) {
                separator = FALLBACK_STATEMENT_SEPARATOR;
            }

            List<String> statements = new ArrayList<>();
            splitSqlScript(resource, script, separator, commentPrefixes, blockCommentStartDelimiter,
                    blockCommentEndDelimiter, statements);

            int stmtNumber = 0;
            for (String statement : statements) {
                stmtNumber++;
                try {
                    if (isDdlScript) {
                        spannerDatabaseAdminTemplate.executeDdlStrings(Collections.singletonList(statement), true);
                    } else {
                        spannerTemplate.executeDmlStatement(Statement.of(statement));
                    }
                }
                catch (Exception ex) {
                    boolean dropStatement = StringUtils.startsWithIgnoreCase(statement.trim(), "drop");
                    if (continueOnError || (dropStatement && ignoreFailedDrops)) {
                        logger.error(ScriptStatementFailedException.buildErrorMessage(statement, stmtNumber, resource), ex);
                    }
                    else {
                        throw new ScriptStatementFailedException(statement, stmtNumber, resource, ex);
                    }
                }
            }

            long elapsedTime = System.currentTimeMillis() - startTime;
            logger.info("Executed SQL script from " + resource + " in " + elapsedTime + " ms.");
        }
        catch (Exception ex) {
            if (ex instanceof ScriptException) {
                throw (ScriptException) ex;
            }
            throw new UncategorizedScriptException(
                    "Failed to execute database script from resource [" + resource + "]", ex);
        }
    }

    private static boolean containsStatementSeparator(@Nullable EncodedResource resource, String script,
                                                      String separator, String[] commentPrefixes, String blockCommentStartDelimiter,
                                                      String blockCommentEndDelimiter) throws ScriptException {

        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        boolean inEscape = false;

        for (int i = 0; i < script.length(); i++) {
            char c = script.charAt(i);
            if (inEscape) {
                inEscape = false;
                continue;
            }
            // MySQL style escapes
            if (c == '\\') {
                inEscape = true;
                continue;
            }
            if (!inDoubleQuote && (c == '\'')) {
                inSingleQuote = !inSingleQuote;
            }
            else if (!inSingleQuote && (c == '"')) {
                inDoubleQuote = !inDoubleQuote;
            }
            if (!inSingleQuote && !inDoubleQuote) {
                if (script.startsWith(separator, i)) {
                    return true;
                }
                else if (startsWithAny(script, commentPrefixes, i)) {
                    // Skip over any content from the start of the comment to the EOL
                    int indexOfNextNewline = script.indexOf('\n', i);
                    if (indexOfNextNewline > i) {
                        i = indexOfNextNewline;
                    }
                    else {
                        // If there's no EOL, we must be at the end of the script, so stop here.
                        break;
                    }
                }
                else if (script.startsWith(blockCommentStartDelimiter, i)) {
                    // Skip over any block comments
                    int indexOfCommentEnd = script.indexOf(blockCommentEndDelimiter, i);
                    if (indexOfCommentEnd > i) {
                        i = indexOfCommentEnd + blockCommentEndDelimiter.length() - 1;
                    }
                    else {
                        throw new ScriptParseException(
                                "Missing block comment end delimiter: " + blockCommentEndDelimiter, resource);
                    }
                }
            }
        }

        return false;
    }

    private static boolean startsWithAny(String script, String[] prefixes, int offset) {
        for (String prefix : prefixes) {
            if (script.startsWith(prefix, offset)) {
                return true;
            }
        }
        return false;
    }
}

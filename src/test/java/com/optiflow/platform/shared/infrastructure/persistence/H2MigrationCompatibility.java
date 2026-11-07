package com.optiflow.platform.shared.infrastructure.persistence;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.flywaydb.core.api.Location;
import org.flywaydb.core.api.ResourceProvider;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.flywaydb.core.api.resource.LoadableResource;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

/**
 * Test-only adaptation that lets the real Flyway migrations run on H2.
 *
 * <p>Two PostgreSQL features that H2 does not accept are rewritten in memory before each script
 * runs; the result is the same schema:
 *
 * <ul>
 *   <li>The {@code WHERE} clause of a partial {@code CREATE UNIQUE INDEX} is removed: a unique
 *       index already accepts several rows with {@code NULL}.
 *   <li>An {@code ALTER TABLE} with several {@code ADD COLUMN} clauses is split into one
 *       {@code ALTER TABLE} per column.
 * </ul>
 *
 * <p>The migration files used in production are not modified.
 */
@Component
class H2MigrationCompatibility implements FlywayConfigurationCustomizer {

  private static final Pattern PARTIAL_INDEX = Pattern.compile(
      "(CREATE\\s+UNIQUE\\s+INDEX[^;]*?)\\s+WHERE[^;]*;", Pattern.CASE_INSENSITIVE);
  private static final Pattern MULTI_ADD_COLUMN = Pattern.compile(
      "ALTER\\s+TABLE\\s+(\\w+)\\s+(ADD\\s+COLUMN\\s[^;]*);", Pattern.CASE_INSENSITIVE);
  private static final Pattern NEXT_ADD_COLUMN =
      Pattern.compile(",\\s*(?=ADD\\s+COLUMN\\s)", Pattern.CASE_INSENSITIVE);

  @Override
  public void customize(FluentConfiguration configuration) {
    configuration.resourceProvider(new H2ResourceProvider(configuration.getLocations()));
  }

  private record H2ResourceProvider(Location[] locations) implements ResourceProvider {

    @Override
    public LoadableResource getResource(String name) {
      return getResources("", new String[] {""}).stream()
          .filter(resource -> resource.getFilename().equals(name))
          .findFirst()
          .orElse(null);
    }

    @Override
    public Collection<LoadableResource> getResources(String prefix, String[] suffixes) {
      List<LoadableResource> resources = new ArrayList<>();
      PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
      for (Location location : locations) {
        try {
          String pattern = "classpath*:" + location.getPath() + "/*";
          for (Resource resource : resolver.getResources(pattern)) {
            String filename = resource.getFilename();
            if (filename != null
                && filename.startsWith(prefix)
                && Arrays.stream(suffixes).anyMatch(filename::endsWith)) {
              resources.add(new H2Script(location.getPath() + "/" + filename, filename, resource));
            }
          }
        } catch (IOException exception) {
          throw new UncheckedIOException(exception);
        }
      }
      return resources;
    }
  }

  private static final class H2Script extends LoadableResource {

    private final String path;
    private final String filename;
    private final Resource resource;

    private H2Script(String path, String filename, Resource resource) {
      this.path = path;
      this.filename = filename;
      this.resource = resource;
    }

    @Override
    public Reader read() {
      try {
        String sql = resource.getContentAsString(StandardCharsets.UTF_8);
        return new StringReader(toH2(sql));
      } catch (IOException exception) {
        throw new UncheckedIOException(exception);
      }
    }

    private static String toH2(String sql) {
      String withoutPartialIndexes = PARTIAL_INDEX.matcher(sql).replaceAll("$1;");
      return MULTI_ADD_COLUMN.matcher(withoutPartialIndexes).replaceAll(statement -> {
        String table = statement.group(1);
        String statements = NEXT_ADD_COLUMN.splitAsStream(statement.group(2))
            .map(column -> "ALTER TABLE " + table + " " + column.trim() + ";")
            .collect(Collectors.joining(System.lineSeparator()));
        return Matcher.quoteReplacement(statements);
      });
    }

    @Override
    public String getAbsolutePath() {
      return path;
    }

    @Override
    public String getAbsolutePathOnDisk() {
      return path;
    }

    @Override
    public String getFilename() {
      return filename;
    }

    @Override
    public String getRelativePath() {
      return filename;
    }
  }
}

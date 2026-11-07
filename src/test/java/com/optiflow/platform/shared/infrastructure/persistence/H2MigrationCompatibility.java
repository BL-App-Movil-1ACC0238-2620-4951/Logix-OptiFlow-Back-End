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
import java.util.regex.Pattern;
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
 * <p>H2 does not support partial indexes, so the {@code WHERE} clause of a {@code CREATE UNIQUE
 * INDEX} is removed before the script runs. The result is equivalent: a unique index already
 * accepts several rows with {@code NULL}. The migrations used in production are not modified.
 */
@Component
class H2MigrationCompatibility implements FlywayConfigurationCustomizer {

  private static final Pattern PARTIAL_INDEX =
      Pattern.compile("(CREATE\s+UNIQUE\s+INDEX[^;]*?)\s+WHERE[^;]*;", Pattern.CASE_INSENSITIVE);

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
        return new StringReader(PARTIAL_INDEX.matcher(sql).replaceAll("$1;"));
      } catch (IOException exception) {
        throw new UncheckedIOException(exception);
      }
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

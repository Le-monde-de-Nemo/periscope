/** Adapted from https://github.com/Minecrell/TerminalConsoleAppender/ */
package fr.eirb.lemondedenemo.periscope.utils;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.jetbrains.annotations.Nullable;
import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

@Plugin(
    name = TerminalConsoleAppender.PLUGIN_NAME,
    category = Core.CATEGORY_NAME,
    elementType = Appender.ELEMENT_TYPE,
    printObject = true)
public class TerminalConsoleAppender extends AbstractAppender {

  public static final String PLUGIN_NAME = "TerminalConsole";

  static final String PROPERTY_PREFIX = "terminal";

  public static final String JLINE_OVERRIDE_PROPERTY = PROPERTY_PREFIX + ".jline";

  public static final String ANSI_OVERRIDE_PROPERTY = PROPERTY_PREFIX + ".ansi";

  private static final @Nullable Boolean ANSI_OVERRIDE = getOptionalAnsiOverrideProperty();

  private static final PrintStream stdout = System.out;

  private static boolean initialized;
  private static @Nullable Terminal terminal;
  private static @Nullable LineReader reader;

  protected TerminalConsoleAppender(
      String name,
      Filter filter,
      Layout<? extends Serializable> layout,
      boolean ignoreExceptions,
      Property[] properties) {
    super(name, filter, layout, ignoreExceptions, properties);
    if (!initialized) initializeTerminal();
  }

  public static synchronized @Nullable Terminal getTerminal() {
    return terminal;
  }

  public static synchronized @Nullable LineReader getLineReader() {
    return reader;
  }

  public static synchronized void setReader(@Nullable LineReader newReader) {
    if (newReader != null && newReader.getTerminal() != terminal) {
      throw new IllegalArgumentException(
          "Reader was not created with TerminalConsoleAppender.getTerminal()");
    }

    reader = newReader;
  }

  public static boolean isAnsiSupported() {
    if (!initialized) initializeTerminal();
    return ANSI_OVERRIDE != null ? ANSI_OVERRIDE : terminal != null;
  }

  private static synchronized void initializeTerminal() {
    if (!initialized) {
      initialized = true;

      try {
        terminal = TerminalBuilder.builder().system(true).build();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private static @Nullable Boolean getOptionalAnsiOverrideProperty() {
    String value =
        PropertiesUtil.getProperties()
            .getStringProperty(TerminalConsoleAppender.ANSI_OVERRIDE_PROPERTY);
    if (value == null) {
      return null;
    }

    if (value.equalsIgnoreCase("true")) {
      return Boolean.TRUE;
    } else if (value.equalsIgnoreCase("false")) {
      return Boolean.FALSE;
    } else {
      LOGGER.warn(
          "Invalid value for boolean input property '{}': {}",
          TerminalConsoleAppender.ANSI_OVERRIDE_PROPERTY,
          value);
      return null;
    }
  }

  private static synchronized void print(String text) {
    if (terminal != null) {
      if (reader != null) {
        // Draw the prompt line again if a reader is available
        reader.printAbove(text);
      } else {
        terminal.writer().print(text);
        terminal.writer().flush();
      }
    } else {
      stdout.print(text);
    }
  }

  public static synchronized void close() throws IOException {
    if (initialized) {
      initialized = false;
      reader = null;
      if (terminal != null) {
        try {
          terminal.close();
        } finally {
          terminal = null;
        }
      }
    }
  }

  @PluginBuilderFactory
  public static <B extends Builder<B>> B newBuilder() {
    return new Builder<B>().asBuilder();
  }

  @Override
  public void append(LogEvent event) {
    print(getLayout().toSerializable(event).toString());
  }

  public static class Builder<B extends Builder<B>> extends AbstractAppender.Builder<B>
      implements org.apache.logging.log4j.core.util.Builder<TerminalConsoleAppender> {

    @Override
    public TerminalConsoleAppender build() {
      return new TerminalConsoleAppender(
          getName(), getFilter(), getOrCreateLayout(), isIgnoreExceptions(), getPropertyArray());
    }
  }
}

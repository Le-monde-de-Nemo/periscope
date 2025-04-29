/** Excerpt from https://github.com/Minecrell/TerminalConsoleAppender/ */
package fr.eirb.lemondedenemo.periscope.utils;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.lookup.AbstractLookup;
import org.apache.logging.log4j.core.lookup.StrLookup;
import org.jetbrains.annotations.Nullable;

@Plugin(name = "tca", category = StrLookup.CATEGORY)
public final class TCALookup extends AbstractLookup {

  /** Lookup key that returns if ANSI colors are unsupported/disabled. */
  public static final String KEY_DISABLE_ANSI = "disableAnsi";

  @Override
  @Nullable
  public String lookup(LogEvent event, String key) {
    if (KEY_DISABLE_ANSI.equals(key)) {
      return String.valueOf(!TerminalConsoleAppender.isAnsiSupported());
    }
    return null;
  }
}

package fr.eirb.lemondedenemo.periscope.commands;

import fr.eirb.lemondedenemo.periscope.api.commands.manager.CommandManager;
import fr.eirb.lemondedenemo.periscope.api.commands.manager.CommandManager.Command;
import fr.eirb.lemondedenemo.periscope.api.commands.manager.CommandResult;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import org.apache.logging.log4j.Logger;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public final class REPL extends Thread {

  private static final String INPUT_REPL = "\33[31m> \033[0m";
  private static final String OUTPUT_REPL = "\33[31m< \033[0m";
  private final Logger logger;
  private final CommandManager commands;
  private final InputStream in;
  private final OutputStream out;
  private LineReader reader;
  private Terminal terminal;

  public REPL(Logger logger, CommandManager commands, InputStream in, OutputStream out) {
    this.logger = logger;
    this.commands = commands;
    this.in = in;
    this.out = out;
    this.setDaemon(true);
    this.setPriority(Thread.MAX_PRIORITY);
    this.setName("REPL");
  }

  private void exitREPL() {
    // Exit
    try {
      CommandResult result =
          this.commands.execute(Command.EXIT, Command.EXIT.getPattern().matcher("bye")).get();
      out.write((OUTPUT_REPL + result.getMessage() + "\n").getBytes(StandardCharsets.UTF_8));
    } catch (Exception e) {
      logger.error(e);
    }
  }

  private void setupREPL() throws IOException {
    this.terminal = TerminalBuilder.builder().system(false).streams(this.in, this.out).build();
    this.reader = LineReaderBuilder.builder().terminal(terminal).build();
  }

  private void readREPL() throws ExecutionException, InterruptedException {
    String line;
    while ((line = reader.readLine(INPUT_REPL)) != null) {
      if (line.isEmpty()) continue;

      logger.trace("Console: {}", line);
      Matcher matcher = null;
      for (Command command : Command.values()) {
        matcher = command.getPattern().matcher(line);
        if (!matcher.find()) continue;

        CommandResult result = this.commands.execute(command, matcher).get();
        terminal.writer().println(OUTPUT_REPL + result.getMessage());
        break;
      }

      assert matcher != null;
      matcher.reset();
      if (!matcher.find()) terminal.writer().println(OUTPUT_REPL + "NOK: unknown command");
    }
  }

  @Override
  public void run() {
    try {

      setupREPL();

      readREPL();

      exitREPL();

    } catch (EndOfFileException e) {
      exitREPL();
    } catch (Exception e) {
      logger.error(e);
    }
  }
}

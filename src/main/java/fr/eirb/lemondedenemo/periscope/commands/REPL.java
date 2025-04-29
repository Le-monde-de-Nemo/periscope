package fr.eirb.lemondedenemo.periscope.commands;

import fr.eirb.lemondedenemo.periscope.api.commands.manager.CommandManager;
import fr.eirb.lemondedenemo.periscope.api.commands.manager.CommandManager.Command;
import fr.eirb.lemondedenemo.periscope.api.commands.manager.CommandResult;
import fr.eirb.lemondedenemo.periscope.utils.TerminalConsoleAppender;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;

public final class REPL extends Thread {

  private static final String INPUT_REPL = "\33[31m> \033[0m";
  private static final String OUTPUT_REPL = "\33[31m< \033[0m";
  private final Logger logger;
  private final CommandManager commands;
  private final OutputStream out;
  private LineReader reader;
  private Terminal terminal;

  public REPL(Logger logger, CommandManager commands, OutputStream out) {
    this.logger = logger;
    this.commands = commands;
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
      this.out.write((OUTPUT_REPL + result.getMessage() + "\n").getBytes(StandardCharsets.UTF_8));
      this.out.flush();
      TerminalConsoleAppender.setReader(null);
    } catch (Exception e) {
      logger.error(e);
    }
  }

  private void setupREPL() {
    this.terminal = TerminalConsoleAppender.getTerminal();
    this.reader =
        LineReaderBuilder.builder().terminal(this.terminal).history(new DefaultHistory()).build();
    TerminalConsoleAppender.setReader(this.reader);
  }

  private void readREPL() throws ExecutionException, InterruptedException {
    String line;
    while ((line = reader.readLine(INPUT_REPL)) != null) {
      if (line.isEmpty()) continue;

      logger.trace("Console: {}", line);
      Matcher matcher = null;
      for (Command command : Command.values()) {
        matcher = command.getPattern().matcher(line);
        if (matcher.find()) {
          CommandResult result = this.commands.execute(command, matcher).get();
          terminal.writer().println(OUTPUT_REPL + result.getMessage());
          break;
        }
      }

      assert matcher != null;
      matcher.reset();
      if (!matcher.find()) terminal.writer().println(OUTPUT_REPL + "NOK: unknown command");
    }
  }

  @Override
  public void run() {
    boolean continueRunning = true;

    setupREPL();

    while (continueRunning) {
      try {
        readREPL();
      } catch (UserInterruptException ignored) {
        this.logger.log(Level.DEBUG, "REPL interrupted");
      } catch (ExecutionException | InterruptedException e) {
        continueRunning = false;
        this.logger.log(Level.ERROR, "REPL execution error", e);
      } catch (EndOfFileException e) {
        continueRunning = false;
        this.logger.log(Level.DEBUG, "REPL end of file", e);
      }
    }

    exitREPL();
  }
}

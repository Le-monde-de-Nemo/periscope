package fr.eirb.lemondedenemo.periscope.commands;

import fr.eirb.lemondedenemo.periscope.api.commands.manager.CommandManager;
import fr.eirb.lemondedenemo.periscope.api.commands.manager.CommandManager.Command;
import fr.eirb.lemondedenemo.periscope.api.commands.manager.CommandResult;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import jline.console.ConsoleReader;
import org.apache.logging.log4j.Logger;

public final class REPL extends Thread {

  private final Logger logger;
  private final CommandManager commands;
  private final InputStream in;
  private final OutputStream out;

  public REPL(Logger logger, CommandManager commands, InputStream in, OutputStream out) {
    this.logger = logger;
    this.commands = commands;
    this.in = in;
    this.out = out;
    this.setDaemon(true);
    this.setPriority(Thread.MAX_PRIORITY);
    this.setName("REPL");
  }

  @Override
  public void run() {
    try {
      ConsoleReader reader = new ConsoleReader(this.in, this.out);
      String line;
      loop:
      while (true) {
        line = reader.readLine("\33[31m> \33[0m");
        if (line == null) {
          line = "stop";
        }
        if (line.isEmpty()) continue;

        logger.info("Console: {}", line);
        Matcher matcher = null;
        for (Command command : Command.values()) {
          matcher = command.getPattern().matcher(line);
          if (!matcher.find()) continue;

          CommandResult result = this.commands.execute(command, matcher).get();
          if (result.isSuccess()) {
            this.logger.info(result.getMessage());
            out.write((result.getMessage() + "\n").getBytes(StandardCharsets.UTF_8));
            break loop;
          } else {
            out.write((result.getMessage() + "\n").getBytes(StandardCharsets.UTF_8));
            this.logger.warn(result.getMessage());
          }
          break;
        }

        assert matcher != null;
        if (!matcher.find())
          out.write("NOK : command inconnue.\n".getBytes(StandardCharsets.UTF_8));
      }
    } catch (Exception e) {
      logger.error(e);
    }
  }
}

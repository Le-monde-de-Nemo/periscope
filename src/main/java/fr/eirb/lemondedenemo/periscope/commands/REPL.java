package fr.eirb.lemondedenemo.periscope.commands;

import fr.eirb.lemondedenemo.periscope.api.commands.manager.CommandManager;
import fr.eirb.lemondedenemo.periscope.api.commands.manager.CommandResult;
import jline.console.ConsoleReader;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public final class REPL extends Thread {

  private final Logger logger;
  private final CommandManager commands;

  public REPL(Logger logger, CommandManager commands) {
    this.logger = logger;
    this.commands = commands;
    this.setDaemon(true);
    this.setPriority(Thread.MAX_PRIORITY);
    this.setName("REPL");
  }

  public void run() {
    try {
        ConsoleReader reader = new ConsoleReader("Client rézo", System.in, System.out, null);
        String line;
        loop:
        while (true) {
          line = reader.readLine("\33[31m> \33[0m");
          if (line == null)
            line = "stop";
          if (line.isEmpty())
            continue;

          logger.info("Console: " + line);
          String[] components = line.split(" ");
          switch (components[0].toLowerCase(Locale.ROOT)) {
            case "quit", "bye", "stop" -> {
              try {
                CommandResult result = this.commands.execute(CommandManager.Command.EXIT, List.of()).get();
                if (result.isSuccess()) {
                  this.logger.info(result.getMessage());
                  break loop;
                } else {
                  this.logger.warn(result.getMessage());
                }
              } catch (ExecutionException e) {
                throw new RuntimeException(e);
              }
            }
            case "delfish" -> {
              try {
                if (components.length < 2) {
                  this.logger.warn("delFish nécessite un argument");
                  continue;
                }
                CommandResult result = this.commands.execute(CommandManager.Command.DELETE_FISH, List.of(components[1])).get();
                if (result.isSuccess()) {
                  this.logger.info(result.getMessage());
                } else {
                  this.logger.warn(result.getMessage());
                }
              } catch (ExecutionException e) {
                throw new RuntimeException(e);
              }
            }
            case "startfish" -> {
              try {
                if (components.length < 2) {
                  this.logger.warn("startFish nécessite un argument");
                  continue;
                }
                CommandResult result = this.commands.execute(CommandManager.Command.START_FISH, List.of(components[1])).get();
                if (result.isSuccess()) {
                  this.logger.info(result.getMessage());
                } else {
                  this.logger.warn(result.getMessage());
                }
              } catch (ExecutionException e) {
                throw new RuntimeException(e);
              }
            }
            default -> logger.warn("NOK : command inconnue.");
          }
        }
      } catch (IOException | InterruptedException exception) {
        exception.printStackTrace();
      }
  }

}

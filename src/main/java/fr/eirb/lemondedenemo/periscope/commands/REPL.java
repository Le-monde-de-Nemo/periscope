package fr.eirb.lemondedenemo.periscope.commands;

import fr.eirb.lemondedenemo.periscope.api.commands.manager.CommandManager;
import fr.eirb.lemondedenemo.periscope.api.commands.manager.CommandResult;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
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

        logger.info("Console: " + line);
        String[] components = line.split(" ");
        List<String> arguments = List.of(Arrays.copyOfRange(components, 1, components.length));
        switch (components[0].toLowerCase(Locale.ROOT)) {
          case "quit", "bye", "stop" -> {
            try {
              CommandResult result =
                  this.commands.execute(CommandManager.Command.EXIT, arguments).get();
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
          case "addfish" -> {
            try {
              if (components.length < 3) {
                this.logger.warn("addFish nécessite deux arguments");
                continue;
              }
              CommandResult result =
                  this.commands.execute(CommandManager.Command.ADD_FISH, arguments).get();
              if (result.isSuccess()) {
                this.logger.info(result.getMessage());
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
              CommandResult result =
                  this.commands.execute(CommandManager.Command.DELETE_FISH, arguments).get();
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
              CommandResult result =
                  this.commands.execute(CommandManager.Command.START_FISH, arguments).get();
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
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}

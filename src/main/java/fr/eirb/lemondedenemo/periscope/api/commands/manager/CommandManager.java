package fr.eirb.lemondedenemo.periscope.api.commands.manager;

import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface CommandManager {

  CompletableFuture<CommandResult> execute(Command command, Matcher matcher);

  enum Command {
    STATUS("status"),
    ADD_FISH(
        "addFish (?<name>[a-zA-Z0-9]+) at (?<fishX>\\d+)x(?<fishY>\\d+), ?(?<fishLength>\\d+)x(?<fishHeight>\\d+), (?<method>[a-zA-Z]+)"),
    DELETE_FISH("delFish (?<name>[a-zA-Z0-9]+)"),
    START_FISH("startFish (?<name>[a-zA-Z0-9]+)"),
    EXIT("(log out)|(bye)|(quit)|(stop)");

    private final Pattern pattern;

    Command(String pattern) {
      this.pattern = Pattern.compile(pattern);
    }

    @Override
    public String toString() {
      return this.pattern.toString();
    }

    public Pattern getPattern() {
      return pattern;
    }
  }
}

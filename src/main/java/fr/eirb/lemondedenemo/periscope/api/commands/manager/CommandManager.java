package fr.eirb.lemondedenemo.periscope.api.commands.manager;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CommandManager {

  CompletableFuture<CommandResult> execute(Command command, List<String> arguments);

  enum Command {
    STATUS("status"),
    ADD_FISH("addFish"),
    DELETE_FISH("delFish"),
    START_FISH("startFish");

    private final String name;

    Command(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return this.name;
    }
  }
}

package fr.eirb.lemondedenemo.periscope.commands;

import fr.eirb.lemondedenemo.periscope.api.commands.manager.CommandManager;
import fr.eirb.lemondedenemo.periscope.api.commands.manager.CommandResult;
import fr.eirb.lemondedenemo.periscope.api.events.CommandResultReceiveEvent;
import fr.eirb.lemondedenemo.periscope.api.events.SendQuitEvent;
import fr.eirb.lemondedenemo.periscope.api.events.manager.EventHandler;
import fr.eirb.lemondedenemo.periscope.api.events.manager.EventManager;
import fr.eirb.lemondedenemo.periscope.api.events.manager.Listener;
import fr.eirb.lemondedenemo.periscope.api.network.Connection;
import fr.eirb.lemondedenemo.periscope.api.network.packets.*;
import fr.eirb.lemondedenemo.periscope.utils.Pair;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FishCommandManager implements CommandManager {

  private final EventManager eventManager;
  private final Connection connection;
  private final Deque<Pair<Command, CompletableFuture<CommandResult>>> futuresResult;

  public FishCommandManager(EventManager eventManager, Connection connection) {
    this.eventManager = eventManager;
    this.connection = connection;
    this.futuresResult = new LinkedList<>();
    this.eventManager.addListener(new ResultInterpretor());
  }

  @Override
  public CompletableFuture<CommandResult> execute(Command command, List<String> arguments) {
    CompletableFuture<CommandResult> future = new CompletableFuture<>();
    if (command == Command.STATUS) {
      return future;
    }
    if (command == Command.EXIT) {
      SendQuitEvent event = new SendQuitEvent();
      this.eventManager.fireEvent(event);
      if (event.isCancelled()) future.complete(new FishCommandResult(false, "Send quit cancelled"));
      else future.complete(new FishCommandResult(true, "Send quit accepted"));
      this.connection.send(new DisconnectingPacket());
      return future;
    }
    if (arguments.isEmpty()) {
      future.complete(new FishCommandResult(false, "Pas assez d'arguments"));
      return future;
    }
    Packet packet =
        switch (command) {
          case EXIT, STATUS -> null;
          case ADD_FISH ->
              arguments.size() < 2
                  ? null
                  : new AddFishPacket(arguments.getFirst(), arguments.get(1));
          case DELETE_FISH -> new DeleteFishPacket(arguments.getFirst());
          case START_FISH -> new StartFishPacket(arguments.getFirst());
          default -> throw new IllegalStateException("Unexpected value: " + command);
        };
    if (packet == null) {
      future.complete(new FishCommandResult(false, "Pas assez d'arguments"));
      return future;
    }
    this.futuresResult.addLast(new Pair<>(command, future));
    this.connection.send(packet);
    return future;
  }

  public class ResultInterpretor implements Listener {

    @EventHandler
    public void onEvent(CommandResultReceiveEvent event) {
      Pair<Command, CompletableFuture<CommandResult>> pair =
          FishCommandManager.this.futuresResult.pop();
      String failureMessage =
          switch (pair.first()) {
            case STATUS, EXIT -> "This should not be printed!";
            case ADD_FISH -> "modèle de mobilité non supporté";
            case DELETE_FISH, START_FISH -> "Poisson inexistant";
          };
      pair.second()
          .complete(
              new FishCommandResult(
                  event.success(), (event.success() ? "OK : " : "NOK : ") + failureMessage));
    }
  }
}

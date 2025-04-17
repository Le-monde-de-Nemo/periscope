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
import fr.eirb.lemondedenemo.periscope.api.utils.Fish;
import fr.eirb.lemondedenemo.periscope.utils.Coords;
import fr.eirb.lemondedenemo.periscope.utils.Pair;
import fr.eirb.lemondedenemo.periscope.utils.RealFish;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;

public class FishCommandManager implements CommandManager {
  private final EventManager eventManager;
  private final Connection connection;
  private final Deque<Pair<Command, CompletableFuture<CommandResult>>> futuresResult;

  public FishCommandManager(EventManager eventManager, Connection connection) {
    this.eventManager = eventManager;
    this.connection = connection;
    this.futuresResult = new LinkedList<>();
    this.eventManager.addListener(new ResultInterpreter());
  }

  @Override
  public CompletableFuture<CommandResult> execute(Command command, Matcher matcher) {
    CompletableFuture<CommandResult> future = new CompletableFuture<>();
    matcher.reset();
    if (!matcher.find()) {
      future.complete(new FishCommandResult(false, "Illegal arguments"));
      return future;
    }
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

    Packet packet =
        switch (command) {
          case DELETE_FISH -> {
            String name = matcher.group("name");
            yield new DeleteFishPacket(name);
          }
          case START_FISH -> {
            String name = matcher.group("name");
            yield new StartFishPacket(name);
          }
          case ADD_FISH -> {
            String name = matcher.group("name");
            Coords location =
                new Coords(
                    Integer.parseInt(matcher.group("fishX")),
                    Integer.parseInt(matcher.group("fishY")));
            Fish fish =
                new RealFish(
                    name,
                    Integer.parseInt(matcher.group("fishLength")),
                    Integer.parseInt(matcher.group("fishHeight")),
                    name);
            String method = matcher.group("method");

            yield new AddFishPacket(name, fish, location, method);
          }
          default -> throw new IllegalStateException("Unexpected value: " + command);
        };
    this.futuresResult.addLast(new Pair<>(command, future));
    this.connection.send(packet);
    return future;
  }

  public class ResultInterpreter implements Listener {

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
                  event.success(), (event.success() ? "OK : " : "NOK : " + failureMessage)));
    }
  }
}

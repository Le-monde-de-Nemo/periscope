package fr.eirb.lemondedenemo.periscope.commands;

import fr.eirb.lemondedenemo.periscope.api.commands.manager.CommandResult;

public record FishCommandResult(boolean success, String message) implements CommandResult {

  @Override
  public boolean isSuccess() {
    return this.success;
  }

  @Override
  public String getMessage() {
    return this.message;
  }
}

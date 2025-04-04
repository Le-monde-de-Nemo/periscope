package fr.eirb.lemondedenemo.periscope.api.events;

import fr.eirb.lemondedenemo.periscope.api.events.manager.Event;
import java.util.List;

// TODO : Object has to be changed
public record FishesReceivedEvent(List<Object> fishes) implements Event {}

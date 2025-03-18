package fr.eirb.lemondedenemo.periscope.api.events;

import fr.eirb.lemondedenemo.periscope.api.events.manager.Event;

public record PongReceiveEvent(int id) implements Event {}

package fr.eirb.lemondedenemo.periscope.api.events;

import fr.eirb.lemondedenemo.periscope.api.events.manager.Event;
import java.util.Optional;

public record HandShakeReceiveEvent(Optional<String> id) implements Event {}

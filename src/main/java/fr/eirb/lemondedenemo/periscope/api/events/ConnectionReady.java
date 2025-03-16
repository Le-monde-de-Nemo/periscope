package fr.eirb.lemondedenemo.periscope.api.events;

import fr.eirb.lemondedenemo.periscope.api.events.manager.Event;
import java.net.SocketAddress;

public record ConnectionReady(SocketAddress address) implements Event {}

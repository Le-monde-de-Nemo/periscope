package fr.eirb.lemondedenemo.periscope.events;

import fr.eirb.lemondedenemo.periscope.api.events.HandShakeReceiveEvent;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EventManagerTest {

  @Test
  public void testAddListener() {
    FishEventManager manager = new FishEventManager(null);
    HandShakeListener handshake = new HandShakeListener();
    manager.addListener(handshake);
    manager.fireEvent(new HandShakeReceiveEvent(Optional.of("N1")));
    Assertions.assertEquals(1, handshake.called);
  }

  @Test
  public void testRemoveListener() {
    FishEventManager manager = new FishEventManager(null);
    HandShakeListener handshake = new HandShakeListener();
    manager.addListener(handshake);
    manager.removeListener(handshake);
    manager.fireEvent(new HandShakeReceiveEvent(Optional.of("N1")));
    Assertions.assertEquals(0, handshake.called);
  }
}

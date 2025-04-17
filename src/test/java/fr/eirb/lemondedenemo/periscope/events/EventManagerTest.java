package fr.eirb.lemondedenemo.periscope.events;

import fr.eirb.lemondedenemo.periscope.api.events.HandShakeReceiveEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EventManagerTest {

  @Test
  void testAddListener() {
    FishEventManager manager = new FishEventManager(null);
    HandShakeListener handshake = new HandShakeListener();
    manager.addListener(handshake);
    manager.fireEvent(new HandShakeReceiveEvent(true, "N1", 0, 500, 500, 500));
    Assertions.assertEquals(1, handshake.called);
  }

  @Test
  void testRemoveListener() {
    FishEventManager manager = new FishEventManager(null);
    HandShakeListener handshake = new HandShakeListener();
    manager.addListener(handshake);
    manager.removeListener(handshake);
    manager.fireEvent(new HandShakeReceiveEvent(true, "N1", 0, 500, 500, 500));
    Assertions.assertEquals(0, handshake.called);
  }
}

package fr.eirb.lemondedenemo.periscope.api.display;

import fr.eirb.lemondedenemo.periscope.api.utils.Fish;
import fr.eirb.lemondedenemo.periscope.utils.Coords;

public interface TankDisplay {

  /** Start the display */
  void start(double width, double height);

  /** Get tank width * */
  double getWidth();

  /** Get tank height * */
  double getHeight();

  /** Stop the display */
  void stop();

  /**
   * Add a fish in the tank
   *
   * @param id fish id
   * @param fish fish representation
   * @param coords where to place the fish in the tank
   */
  void addFish(String id, Fish fish, Coords coords);

  /**
   * Remove a fish from the tank
   *
   * @param id fish id
   */
  void removeFish(String id);

  /**
   * Move a fish in the tank
   *
   * @param id fish id
   * @param coords where the fish goes
   * @param duration duration of the transition
   */
  void moveFish(String id, Coords coords, double duration);
}

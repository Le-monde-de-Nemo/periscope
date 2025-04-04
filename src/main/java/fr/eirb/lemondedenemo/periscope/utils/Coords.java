package fr.eirb.lemondedenemo.periscope.utils;

public record Coords(int x, int y) {

  public Coords(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public Coords add(Coords other) {
    return new Coords(x + other.x, y + other.y);
  }

  public Coords sub(Coords other) {
    return new Coords(x - other.x, y - other.y);
  }

  public Coords mul(int factor) {
    return new Coords(x * factor, y * factor);
  }

  public Coords div(int factor) {
    return new Coords(x / factor, y / factor);
  }

  public int distance(Coords other) {
    return Math.abs(x - other.x) + Math.abs(y - other.y);
  }
}

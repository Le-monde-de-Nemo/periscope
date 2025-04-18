package fr.eirb.lemondedenemo.periscope.api.network;

import java.util.regex.Pattern;

public interface NetworkParser {
  String FISH_PATTERN =
      "\\[(?<id>\\w+) at (?<destX>-?\\d+)x(?<destY>-?\\d+),(?<fishLength>\\d+)x(?<fishHeight>\\d+),(?<duration>\\d+)\\]";

  void parse(String message);

  enum Packets {
    GREETING(
        "(?<fail>no greeting)|(?<success>greeting (?<id>[a-zA-Z0-9]+) (?<vueX>\\d+)x(?<vueY>\\d+)\\+(?<vueWidth>\\d+)\\+(?<vueHeight>\\d+))"),
    PONG("pong (?<id>\\d+)"),
    COMMAND_RESULT("(?<fail>NOK)|(?<success>OK)"),
    GET_FISHES(String.format("list (?<fishes>(?<fish>%s ?)+)", FISH_PATTERN)),
    EXIT("bye");

    private final Pattern pattern;

    Packets(String pattern) {
      this.pattern = Pattern.compile(pattern);
    }

    public Pattern getPattern() {
      return pattern;
    }
  }
}

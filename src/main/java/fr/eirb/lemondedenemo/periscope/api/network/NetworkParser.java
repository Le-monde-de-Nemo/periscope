package fr.eirb.lemondedenemo.periscope.api.network;

import java.util.regex.Pattern;

public interface NetworkParser {
  void parse(String message);

  enum Packets {
    GREETING(
        "(?<fail>no greeting)|(?<success>greeting (?<id>[a-zA-Z0-9]+) (?<vueX>\\d+)x(?<vueY>\\d+)\\+(?<vueWidth>\\d+)\\+(?<vueHeight>\\d+))"),
    PONG("pong (?<id>\\d+)"),
    COMMAND_RESULT("(?<fail>NOK)|(?<success>OK)"),
    GET_FISHES("list (?<fishes>(?<fish>\\[\\w+ at \\d+x\\d+,\\d+x\\d+,\\d+\\] ?)+)"),
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

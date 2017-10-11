package me.piruin.spinney.sample;

import java.util.Arrays;
import java.util.List;

class Data {

  static final List<String> department =
    Arrays.asList("NSTDA", "NECTEC", "BIOTEC", "MTEC", "NANOTEC");

  static final List<DatabaseItem> country = Arrays.asList(
    new DatabaseItem(1, "THAILAND"),
    new DatabaseItem(2, "JAPAN"),
    new DatabaseItem(3, "SOUTH KOREA"),
    new DatabaseItem(4, "VIETNAM")
  );

  static final List<DatabaseItem> regions = Arrays.asList(
    new DatabaseItem(1, "Center", 1),
    new DatabaseItem(2, "North", 1),
    new DatabaseItem(3, "East-North", 1),
    new DatabaseItem(4, "East", 1),
    new DatabaseItem(5, "West", 1),
    new DatabaseItem(6, "South", 1),
    new DatabaseItem(7, "Hokkaido", 2),
    new DatabaseItem(8, "Kyushu", 2),
    new DatabaseItem(8, "Central", 2)
  );

  static final List<DatabaseItem> cities = Arrays.asList(
    new DatabaseItem(1, "BANGKOK", 1),
    new DatabaseItem(2, "CHONBURI", 1),
    new DatabaseItem(3, "CHIANG MAI", 1),
    new DatabaseItem(4, "TOKYO", 2),
    new DatabaseItem(5, "HOKKAIDO", 2),
    new DatabaseItem(6, "SEOUL", 3),
    new DatabaseItem(7, "HOJIMIN", 4)
  );

  static final List<DatabaseItem> districts = Arrays.asList(
    new DatabaseItem(1, "Bang Khen", 1),
    new DatabaseItem(2, "Chatuchak", 1),
    new DatabaseItem(3, "Don Mueang", 1),
    new DatabaseItem(4, "Pattaya ", 2),
    new DatabaseItem(5, "Doi Tao ", 3)
  );

}

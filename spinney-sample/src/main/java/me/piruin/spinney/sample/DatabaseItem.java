package me.piruin.spinney.sample;

public class DatabaseItem {

  private final int id;
  private final String name;
  private int parentId;

  public DatabaseItem(int id, String name) {
    this.id = id;
    this.name = name;
  }

  public DatabaseItem(int id, String name, int parentId) {
    this(id, name);
    this.parentId = parentId;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public int getParentId() {
    return parentId;
  }

  @Override public String toString() {
    return "DatabaseItem{" +
      "id=" + id +
      ", name='" + name + '\'' +
      '}';
  }
}

package me.piruin.spinney.sample;

class DatabaseItem {

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

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    DatabaseItem that = (DatabaseItem) o;

    if (id != that.id) return false;
    if (parentId != that.parentId) return false;
    return name.equals(that.name);
  }

  @Override public int hashCode() {
    int result = id;
    result = 31 * result + name.hashCode();
    result = 31 * result + parentId;
    return result;
  }
}

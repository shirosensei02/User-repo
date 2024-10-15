package cs204.project.Controller;

public class Player {
  private Long id;
  private int rank;

  // Constructor
  public Player(Long id, int rank) {
    this.id = id;
    this.rank = rank;
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public int getRank() {
    return rank;
  }

  public void setRank(int rank) {
    this.rank = rank;
  }
}

package extra;

import java.util.Random;

public class Guesser {
  private int goal;
  private int guesses;
  private String id;

  public Guesser(String id) {
    this.id = id;
    Random random = new Random();
    this.goal = random.nextInt(100) + 1;
    this.guesses = 0;
  }

  public void incrementGuesses() {
    this.guesses++;
  }

  public int getGuesses() {
    return guesses;
  }

  public String getId() {
    return id;
  }

  public int guess(int guess) {
    this.guesses++;
    return Integer.compare(guess, this.goal);
  }
}

package f2.spw;

public interface GameReporter {

	int getHP();
	long getScore();
	long getTime();
    double getDifficulty();
    boolean getGameoverStatus();
    boolean getPauseStatus();
    SpaceShip getPosition();

}

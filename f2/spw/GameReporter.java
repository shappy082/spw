package f2.spw;

public interface GameReporter {

	int getHP();
	int getNumNuclear();
	long getScore();
	long getTime();
    double getDifficulty();
    boolean getGameoverStatus();
    boolean getPauseStatus();

}

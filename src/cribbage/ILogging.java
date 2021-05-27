package cribbage;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

public interface ILogging {
	public void loadProperties();

	public void logSeed();

	public void logPlayer(int nPlayers);

	public void logDeal(int player, Hand hand);

	public void logDiscard(int player, Hand hand);

	public void logStarter(Card card);

	public void logPlay(int player, int playValue, Card card);

	public void logScore(int playerNumber, int totScore, int score, String type, Hand hand);

	public void logShow(int playerNumber, Card starterCard, Hand hand);

}

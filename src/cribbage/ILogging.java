package cribbage;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

public interface ILogging {
	/**
	 * Load properties of the game. Used for loading game seed and player type.
	 */
	public void loadProperties();

	/** Log this game's seed
	 * 
	 */
	public void logSeed();

	/**
	 * Log the type of player
	 * @param nPlayers
	 */
	public void logPlayer(int nPlayers);

	/**
	 * Log a player's hand after the cards are dealed out.
	 * @param player
	 * @param hand
	 */
	public void logDeal(int player, Hand hand);

	/**
	 * Log a player's discarded cards.
	 * @param player
	 * @param hand
	 */
	public void logDiscard(int player, Hand hand);

	/**
	 * Log what card is used as the starter.
	 * @param card
	 */
	public void logStarter(Card card);

	/**
	 * Log a move played by a player, with current play's value after player played that move.
	 * @param player
	 * @param playValue
	 * @param card
	 */
	public void logPlay(int player, int playValue, Card card);

	/**
	 * Log a player's score with score type and included player's hand if possible.
	 * @param playerNumber
	 * @param totalScore
	 * @param score
	 * @param type
	 * @param hand
	 */
	public void logScore(int playerNumber, int totalScore, int score, String type, Hand hand);

	/**
	 * Log a player's hand in The Show (with the starter card).
	 * @param playerNumber
	 * @param starterCard
	 * @param hand
	 */
	public void logShow(int playerNumber, Card starterCard, Hand hand);

}

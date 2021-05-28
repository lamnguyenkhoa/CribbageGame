// W06 Team 02 [THU 03:15 PM]

package cribbage;

import java.io.IOException;

import ch.aplu.jcardgame.Hand;

public interface IScoring {
	/**
	 * Load information such as valid combinations or different scoring rule
	 * @param filename
	 * @throws IOException
	 */
	public void loadScoreSetting(String filename) throws IOException;
	
	/**
	 * Calculate score for the starter card.
	 * @param hand
	 */
	public void ScoringStarter(Hand hand);

	/**
	 * Calculate score when a player say "go".
	 * @param player
	 */
	public void ScoringGo(int player);

	/**
	 * Calculate score in The Play when a recent card are played. Scoring
	 * must involved that new card.
	 * @param hand
	 * @param player
	 */
	public void ScoringPlay(Hand hand, int player);

	/**
	 * Calculate score in The Show, using player's hand and the starter card.
	 * @param starter
	 * @param hand
	 * @param player
	 */
	public void ScoringShow(Hand starter, Hand hand, int player);
}

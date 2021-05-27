package cribbage;

import ch.aplu.jcardgame.Hand;

public interface IScoring {
	public void ScoringStarter(Hand hand);

	public void ScoringGo(int player);

	public void ScoringPlay(Hand hand, int player);

	public void ScoringShow(Hand starter, Hand hand, int player);
}

// W06 Team 02 [THU 03:15 PM]

package cribbage;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

public class RandomPlayer extends IPlayer {

	@Override
	public Card discard() {
		return Cribbage.randomCard(hand);
	}

	@Override
	Card selectToLay(Hand inPlayHand) {
		return hand.isEmpty() ? null : Cribbage.randomCard(hand);
	}

}

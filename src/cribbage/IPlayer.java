// W06 Team 02 [THU 03:15 PM]

package cribbage;

import ch.aplu.jcardgame.*;

import java.util.ArrayList;

public abstract class IPlayer {
	int id;
	Deck deck; // Need this since can't get from hand to deck
	Hand hand;

	void setId(int id) {
		this.id = id;
	}

	void startSegment(Deck deck, Hand hand) {
		this.deck = deck;
		this.hand = hand;
	}

	abstract Card discard();

	boolean emptyHand() {
		return hand.isEmpty();
	}

	abstract Card selectToLay(Hand inPlayHand);

	Card lay(int limit, Hand inHandPlay) {
		// System.out.println("lay(" + limit + ")");
		// First, we take out unlayable cards and set them aside
		Hand unlayable = new Hand(deck);
		for (Card c : ((ArrayList<Card>) hand.getCardList().clone())) // Modify list, so need to iterate over clone
			if (Cribbage.cardValue(c) > limit) {
				c.removeFromHand(true);
				// System.out.println("hand = " + hand.toString());
				unlayable.insert(c, false);
			}
		// hand.draw(); Cribbage.delay(1000);
		// Now we choose a card
		Card s = selectToLay(inHandPlay);
		// Then we re-insert those unlayable cards back to our hand
		hand.insert(unlayable, true);
		return s;
	}

}

package cribbage;

import java.util.stream.Collectors;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Deck;
import ch.aplu.jcardgame.Hand;

public class CanonicalName{

	public static String canonical(Suit s) {
		return s.toString().substring(0, 1);
	}

	public static String canonical(Rank r) {
		switch (r) {
		case ACE:
		case KING:
		case QUEEN:
		case JACK:
		case TEN:
			return r.toString().substring(0, 1);
		default:
			return String.valueOf(r.value);
		}
	}

	public static String canonical(Card c) {
		return canonical((Rank) c.getRank()) + canonical((Suit) c.getSuit());
	}

	public static String canonical(Hand h) {
		Deck defaultDeck = Cribbage.cribbage.getDeck();
		Hand tmpHand = new Hand(defaultDeck); // Clone to sort without changing the original hand
		for (Card C : h.getCardList())
			tmpHand.insert(C.getSuit(), C.getRank(), false);
		tmpHand.sort(Hand.SortType.POINTPRIORITY, false);
		return "[" + tmpHand.getCardList().stream().map(c -> canonical(c)).collect(Collectors.joining(",")) + "]";
	}

}

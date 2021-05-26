package cribbage;

import java.util.stream.Stream;

import ch.aplu.jcardgame.Deck;
import cribbage.Cribbage.Rank;
import cribbage.Cribbage.Suit;

public class MyCardValues implements Deck.CardValues { // Need to generate a unique value for every card
	public int[] values(Enum suit) {  // Returns the value for each card in the suit
		return Stream.of(Rank.values()).mapToInt(r -> (((Rank) r).order-1)*(Suit.values().length)+suit.ordinal()).toArray();
	}
}

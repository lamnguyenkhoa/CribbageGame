package cribbage;

import ch.aplu.jcardgame.*;

import ch.aplu.jgamegrid.*;
import cribbage.Cribbage.MyCardValues;
import cribbage.Cribbage.Rank;
import cribbage.Cribbage.Suit;

import java.awt.Color;
import java.awt.Font;
import java.awt.print.Printable;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.spi.LocaleNameProvider;
import java.util.stream.Collectors;
import java.util.stream.Stream;



//singleton facade class for scoring//
public class ScoreSystem {

	public static final int DEALER = 1;
	public static final int STARTER_IS_JACK_SCORE = 2;
	protected static int GO_SCORE = 1;
	protected static int FIFTEEN = 15;
	protected static int FIFTEEN_SCORE = 2;
	protected static int THIRTYONE = 31;
	protected static int THIRTYONE_SCORE = 2;
	protected static int PAIR2_SCORE = 2;
	protected static int PAIR3_SCORE = 6;
	protected static int PAIR4_SCORE = 12;
	protected static int RUN3_SCORE = 3;
	protected static int RUN4_SCORE = 4;
	protected static int RUN5_SCORE = 5;
	protected static int RUN6_SCORE = 6;
	protected static int RUN7_SCORE = 7;

	private static ScoreSystem singleInstance = null;
	
	private ScoreSystem() {
	}
	
	public static void loadProperties(Properties properties) {
		
	}
	
	public static ScoreSystem getInstance() {
		if (singleInstance == null) {
			singleInstance = new ScoreSystem();
		}
		return singleInstance;
	}	

	
	public void ScoringStarter(Hand hand) {
		if(hand.getFirst().getRank() == Rank.JACK) {
			//starter card is a jack, dealer gets 2 point//
			Cribbage.addScore(DEALER, STARTER_IS_JACK_SCORE);
			
			//LOGGING//
		}
	}
	
	public void ScoringGo(int player) {
		Cribbage.addScore(player, GO_SCORE);
		// LOGGING
	}
	
	public void ScoringPlay(Hand hand, int player) {
		CheckTotalsCardValue(hand, player);
		CheckPairTriQuadInPlay(hand, player);
		CheckRunsInPlay(hand, player);
	}
	
	public void ScoringShow(Hand starter, Hand hand, int player) {
		Card tmpCard = starter.getFirst();
		starter.remove(tmpCard, false);
		hand.insert(tmpCard, false);
		CheckRunsInShow(hand, player);
		CheckPairTriQuadInShow(hand, player);
		CheckFifteen(hand, player, new ArrayList<Card>());
		hand.remove(tmpCard, false);
		starter.insert(tmpCard, false);
	}
	
	/**
	 * Check if this hand's total face value is equal 15 or 31. Used in The Play.
	 */
	public void CheckTotalsCardValue(Hand hand, int player) {
		int totalFaceValue = 0;
		for (Card c: hand.getCardList()) totalFaceValue += Cribbage.cardValue(c);
		if(totalFaceValue == FIFTEEN){
			Cribbage.addScore(player, FIFTEEN_SCORE);
			//LOGGING//
		}
		if(totalFaceValue == THIRTYONE){
			Cribbage.addScore(player, THIRTYONE_SCORE);
			//LOGGING//
		}
	}
		
	public void CheckPairTriQuadInPlay(Hand hand, int player) {
		int n = hand.getNumberOfCards();
		Enum recentRank = hand.get(n-1).getRank();
		// Check for pair 2
		if (n>=2 && hand.get(n-2).getRank() == recentRank) {
			// Check for pair 3
			if (n>=3 && hand.get(n-3).getRank() == recentRank) {
				// Check for pair 4
				if (n>=4 && hand.get(n-4).getRank() == recentRank) {
					Cribbage.addScore(player, PAIR4_SCORE);
					return;
				}
				Cribbage.addScore(player, PAIR3_SCORE);
				return;
			}
			Cribbage.addScore(player, PAIR2_SCORE);
			return;
		}
	}
	
	public void CheckRunsInPlay(Hand hand, int player) {
		int n = hand.getNumberOfCards();
		int lastNumber = 99999; 
		ArrayList<Integer> cardSequence = new ArrayList<Integer>();
		// Read hand card into an array until meet pair
		for (int i=n-1; i>-1; i--) {
			int cardValue = Cribbage.cardValue(hand.get(i));
			if (cardValue == lastNumber) {
				break;
			}
			cardSequence.add(cardValue);
			lastNumber = cardValue;
		}
		// Check if sequence
		int n2 = cardSequence.size();
		if (n2 <= 2) {
			return;
		}
		int maxValue = Collections.max(cardSequence);
		int minValue = Collections.min(cardSequence);
		if (maxValue - minValue + 1 == n2) {
			// Yes it's a run
			switch (n2) {
			case 3:
				Cribbage.addScore(player, RUN3_SCORE);
				break;
			case 4:
				Cribbage.addScore(player, RUN4_SCORE);
				break;
			case 5:
				Cribbage.addScore(player, RUN5_SCORE);
				break;
			case 6:
				Cribbage.addScore(player, RUN6_SCORE);
				break;
			case 7:
				Cribbage.addScore(player, RUN7_SCORE);
				break;
			}
		}
		
	}
	
	/**
	 * Check if this hand has runs. This method only used in The Show, 
	 * so max length is 5 (if no rules are changed).
	 * @param hand
	 * @param player
	 */
	public void CheckRunsInShow(Hand hand, int player) {
		for (int runLength=5; runLength>2; runLength--) {
			ArrayList<Card[]> runCards = hand.getSequences(runLength);
			if (runCards.size() != 0) {
				switch(runLength) {
					case 3:
						Cribbage.addScore(player, RUN3_SCORE);
						System.out.println("player" + player + "score run3 in show");
						break;
					case 4:
						Cribbage.addScore(player, RUN4_SCORE);
						System.out.println("player" + player + "score run4 in show");
						break;
					case 5:
						Cribbage.addScore(player, RUN5_SCORE);
						System.out.println("player" + player + "score run5 in show");
						break;
				}
				for (Card[] cards : runCards) {
					for (Card card : cards) {
						System.out.println(card.toString());
					}
					// LOGGING //
				}
			}
		}
		
	}
	
	public void CheckPairTriQuadInShow(Hand hand, int player) {
		ArrayList<Card[]> pairCardList = hand.getPairs();
		for (Card[] cards : pairCardList) {
			Cribbage.addScore(player, PAIR2_SCORE);
			System.out.println("player" + player + "score pair2 in show");
			// LOGGING
		}
		ArrayList<Card[]> triCardList = hand.getTrips();
		for (Card[] cards : triCardList) {
			Cribbage.addScore(player, PAIR3_SCORE);
			System.out.println("player" + player + "score pair3 in show");
			// LOGGING
		}
		ArrayList<Card[]> quadCardList = hand.getQuads();
		for (Card[] cards : quadCardList) {
			Cribbage.addScore(player, PAIR4_SCORE);
			System.out.println("player" + player + "score pair4 in show");
			// LOGGING
		}
	}

	/**
	 * This is a subset problem. Use recursive.
	 * Check for all combination of cards that have sum equal to 15.
	 * @param hand
	 * @param player
	 * @param chosenCards
	 */
	public void CheckFifteen(Hand hand, int player, ArrayList<Card> chosenCards) {
		// check sum of chosen cards group
		int cardSum = 0;
		for (Card card : chosenCards) {
			cardSum += Cribbage.cardValue(card);
		}
		if (cardSum == FIFTEEN) {
			Cribbage.addScore(player, FIFTEEN_SCORE);
			System.out.println("player" + player + "score fifteen in show");
			// LOGGING
		}
		if (cardSum > FIFTEEN) return;
		for (int i=0; i<hand.getNumberOfCards(); i++) {
			Card card = hand.get(i);
			hand.remove(card, false);
			chosenCards.add(card);
			CheckFifteen(hand, player, chosenCards);
			// revert
			chosenCards.remove(card);
			hand.insert(card, false);
		}
	}
	
	public void jackSameUnit() {
		
	}
}
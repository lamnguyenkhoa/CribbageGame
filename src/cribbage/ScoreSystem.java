// W06 Team 02 [THU 03:15 PM]

package cribbage;

import ch.aplu.jcardgame.*;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

//singleton facade class for scoring//
public class ScoreSystem implements IScoring {

	private int STARTERISJACK_SCORE = 2;
	private int GO_SCORE = 1;
	private final int FIFTEEN = 15;
	private int FIFTEEN_SCORE = 2;
	private final int THIRTYONE = 31;
	private int THIRTYONE_SCORE = 2;
	private int PAIR2_SCORE = 2;
	private int PAIR3_SCORE = 6;
	private int PAIR4_SCORE = 12;
	private int RUN3_SCORE = 3;
	private int RUN4_SCORE = 4;
	private int RUN5_SCORE = 5;
	private int RUN6_SCORE = 6;
	private int RUN7_SCORE = 7;
	private int FLUSH4_SCORE = 4;
	private int FLUSH5_SCORE = 5;
	private int JACKSAMESUIT_SCORE = 1;

	private static ScoreSystem singleInstance = null;
	
	private ScoreSystem() {
	}
	
	@Override
	public void loadScoreSetting(String filename) throws IOException {
		Properties properties = new Properties();
		properties.setProperty("starter", "2");
		properties.setProperty("go", "1");
		properties.setProperty("fifteen", "2");
		properties.setProperty("thirtyone", "2");
		properties.setProperty("pair2", "2");
		properties.setProperty("pair3", "6");
		properties.setProperty("pair4", "12");
		properties.setProperty("run3", "3");
		properties.setProperty("run4", "4");
		properties.setProperty("run5", "5");
		properties.setProperty("run6", "6");
		properties.setProperty("run7", "7");
		properties.setProperty("flush4", "4");
		properties.setProperty("flush5", "5");
		properties.setProperty("jack", "1");
		// Read from file
		try (FileReader inStream = new FileReader(filename)) {
			properties.load(inStream);
		}
		STARTERISJACK_SCORE = Integer.parseInt(properties.getProperty("starter"));
		GO_SCORE = Integer.parseInt(properties.getProperty("go"));
		FIFTEEN_SCORE = Integer.parseInt(properties.getProperty("fifteen"));
		THIRTYONE_SCORE = Integer.parseInt(properties.getProperty("thirtyone"));
		PAIR2_SCORE = Integer.parseInt(properties.getProperty("pair2"));
		PAIR3_SCORE = Integer.parseInt(properties.getProperty("pair3"));
		PAIR4_SCORE = Integer.parseInt(properties.getProperty("pair4"));
		RUN3_SCORE = Integer.parseInt(properties.getProperty("run3"));
		RUN4_SCORE = Integer.parseInt(properties.getProperty("run4"));
		RUN5_SCORE = Integer.parseInt(properties.getProperty("run5"));
		RUN6_SCORE = Integer.parseInt(properties.getProperty("run6"));
		RUN7_SCORE = Integer.parseInt(properties.getProperty("run7"));
		FLUSH4_SCORE = Integer.parseInt(properties.getProperty("flush4"));
		FLUSH5_SCORE = Integer.parseInt(properties.getProperty("flush5"));
		JACKSAMESUIT_SCORE = Integer.parseInt(properties.getProperty("jack"));
	}
	
	public static ScoreSystem getInstance() {
		if (singleInstance == null) {
			singleInstance = new ScoreSystem();
		}
		return singleInstance;
	}	

	@Override
	public void ScoringStarter(Hand hand) {
		if(hand.getFirst().getRank() == Rank.JACK) {
			Cribbage.cribbage.addScore(Cribbage.DEALER, STARTERISJACK_SCORE, "starter", hand);
		}
	}
	
	@Override
	public void ScoringGo(int player) {
		Cribbage.cribbage.addScore(player, GO_SCORE, "go", null);
	}
	
	@Override
	public void ScoringPlay(Hand hand, int player) {
		CheckTotalsCardValue(hand, player);
		CheckRunsInPlay(hand, player);
		CheckPairTriQuadInPlay(hand, player);
	}
	
	@Override
	public void ScoringShow(Hand starter, Hand hand, int player) {
		Card tmpCard = starter.getFirst();
		starter.remove(tmpCard, false);
		CheckJackSameSuit(hand, tmpCard, player);
		CheckFlush(hand, tmpCard, player);
		hand.insert(tmpCard, false);
		// CheckFifteen use recursive se we need another data type for easier implementation
		ArrayList<Card> chosenCard = new ArrayList<Card>();
		ArrayList<Card> cardList = hand.getCardList();
		CheckFifteen(0, chosenCard, cardList, player);
		CheckPairTriQuadInShow(hand, player);
		CheckRunsInShow(hand, player);
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
			Cribbage.cribbage.addScore(player, FIFTEEN_SCORE, "fifteen", null);
			//LOGGING//
		}
		if(totalFaceValue == THIRTYONE){
			Cribbage.cribbage.addScore(player, THIRTYONE_SCORE, "thirtyone", null);
			//LOGGING//
		}
	}
	
	/**
	 * Check if the last placed card formed a pair2, pair3 or pair4.
	 * @param hand
	 * @param player
	 */
	public void CheckPairTriQuadInPlay(Hand hand, int player) {
		int n = hand.getNumberOfCards();
		Enum recentRank = hand.get(n-1).getRank();
		// Check for pair 2
		if (n>=2 && hand.get(n-2).getRank() == recentRank) {
			// Check for pair 3
			if (n>=3 && hand.get(n-3).getRank() == recentRank) {
				// Check for pair 4
				if (n>=4 && hand.get(n-4).getRank() == recentRank) {
					Cribbage.cribbage.addScore(player, PAIR4_SCORE, "pair4", null);
					return;
				}
				Cribbage.cribbage.addScore(player, PAIR3_SCORE, "pair3", null);
				return;
			}
			Cribbage.cribbage.addScore(player, PAIR2_SCORE, "pair2", null);
			return;
		}
	}
	
	/**
	 * Check if the last placed card form a of 3,4,5,6,7 in play. 
	 * Runs cannot span pairs.
	 * @param hand
	 * @param player
	 */
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
				Cribbage.cribbage.addScore(player, RUN3_SCORE, "run3", null);
				break;
			case 4:
				Cribbage.cribbage.addScore(player, RUN4_SCORE, "run4", null);
				break;
			case 5:
				Cribbage.cribbage.addScore(player, RUN5_SCORE, "run5", null);
				break;
			case 6:
				Cribbage.cribbage.addScore(player, RUN6_SCORE, "run6", null);
				break;
			case 7:
				Cribbage.cribbage.addScore(player, RUN7_SCORE, "run7", null);
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
			Hand[] runHands = hand.extractSequences(runLength);
			for (Hand runHand : runHands) {
				switch(runLength) {
				case 3:
					Cribbage.cribbage.addScore(player, RUN3_SCORE, "run3", runHand);
					break;
				case 4:
					Cribbage.cribbage.addScore(player, RUN4_SCORE, "run4", runHand);
					break;
				case 5:
					Cribbage.cribbage.addScore(player, RUN5_SCORE, "run5", runHand);
					break;
				}
			}
		}
		
	}
	
	/**
	 * Check for all combinations of pairs in The Show.
	 * @param hand
	 * @param player
	 */
	public void CheckPairTriQuadInShow(Hand hand, int player) {
		Hand[] pairHands = hand.extractPairs();
		for (Hand pairHand : pairHands) {
			Cribbage.cribbage.addScore(player, PAIR2_SCORE, "pair2", pairHand);
		}
		Hand[] triHands = hand.extractTrips();
		for (Hand triHand : triHands) {
			Cribbage.cribbage.addScore(player, PAIR3_SCORE, "pair3", triHand);
		}
		Hand[] quadHands = hand.extractQuads();
		for (Hand quadHand : quadHands) {
			Cribbage.cribbage.addScore(player, PAIR4_SCORE, "pair4", quadHand);
		}
	}

	/**
	 * This is a subset sum problem. Use recursive.
	 * Check for all combination of cards that have sum equal to 15.
	 * @param l
	 * @param chosenCards
	 * @param cardList
	 * @param player
	 */
	public void CheckFifteen(int l, ArrayList<Card> chosenCards, ArrayList<Card> cardList, int player) {
		int cardSum = 0;
		for (Card card : chosenCards) {
			cardSum += Cribbage.cardValue(card);
		}
		if (cardSum == FIFTEEN) {
			// convert ArrayList to Hand format for logging
			Hand choseHand = new Hand(Cribbage.cribbage.getDeck());
			for (Card card : chosenCards) {
				choseHand.insert(card.getSuit(), card.getRank(), false);
			}
			Cribbage.cribbage.addScore(player, FIFTEEN_SCORE, "fifteen", choseHand);
		}
		if (cardSum > FIFTEEN) return;
		
		for (int i=l; i< cardList.size(); i++) {
			// Check if it is repeated
			if (i>l && cardList.get(i) == cardList.get(i-1)) {
				System.out.println("TRUE");
				continue;
			}
			chosenCards.add(cardList.get(i));
			CheckFifteen(i+1, chosenCards, cardList, player);
			chosenCards.remove(cardList.get(i));
		}
	}
	
	/**
	 * Check if 4 card in hand is same suit. If yes, then check for a flush5.
	 * @param hand
	 * @param starterCard
	 * @param player
	 */
	public void CheckFlush(Hand hand, Card starterCard, int player) {
		Enum compareSuit = hand.getFirst().getSuit();
		int n = hand.getNumberOfCards();
		for (int i=0; i<n; i++) {
			if (hand.get(i).getSuit() != compareSuit) {
				return;
			}
		}
		if (starterCard.getSuit() == compareSuit) {
			hand.insert(starterCard, false);
			Cribbage.cribbage.addScore(player, FLUSH5_SCORE, "flush5", hand);
			hand.remove(starterCard, false);
		} else {
			Cribbage.cribbage.addScore(player, FLUSH4_SCORE, "flush4", hand);
		}
	}
	
	/**
	 * Check if there are Jack cards in hand that have same suit with starter card.
	 * 
	 * @param hand
	 * @param starterCard
	 * @param player
	 */
	public void CheckJackSameSuit(Hand hand, Card starterCard, int player) {
		Enum starterSuit = starterCard.getSuit();
		ArrayList<Card> cardList = hand.getCardList();
		for (Card card : cardList) {
			if (card.getRank() == Rank.JACK && card.getSuit() == starterSuit) {
				Hand tmpHand = new Hand(Cribbage.cribbage.getDeck());
				tmpHand.insert(starterSuit, Rank.JACK, false);
				Cribbage.cribbage.addScore(player, JACKSAMESUIT_SCORE, "jack", tmpHand);
			}
		}
	}
}
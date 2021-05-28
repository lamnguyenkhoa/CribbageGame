// W06 Team 02 [THU 03:15 PM]

package cribbage;
// Cribbage.java
import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;

import java.awt.Color;
import java.awt.Font;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Cribbage extends CardGame {
	private static final long serialVersionUID = -2600764570207053492L;
	static Cribbage cribbage; // Provide access to singleton
	private final String version = "0.1";
	static public final int nPlayers = 2;
	static public final int DEALER = 1;
	public final int nStartCards = 6;
	public final int nDiscards = 2;
	private final int handWidth = 400;
	private final int cribWidth = 150;
	private final int segmentWidth = 180;
	private final Deck deck = new Deck(Suit.values(), Rank.values(), "cover", new MyCardValues());
	private final Location[] handLocations = { new Location(360, 75), new Location(360, 625) };
	private final Location[] scoreLocations = { new Location(570, 25), new Location(570, 675) };
	private final Location[] segmentLocations = { // need at most three as 3x31=93 > 2x4x10=80
			new Location(150, 350), new Location(400, 350), new Location(650, 350) };
	private final Location starterLocation = new Location(50, 625);
	private final Location cribLocation = new Location(700, 625);
	private final Location seedLocation = new Location(5, 25);
	// private final TargetArea cribTarget = new TargetArea(cribLocation,
	// CardOrientation.NORTH, 1, true);
	private final Actor[] scoreActors = { null, null }; // , null, null };
	private final Location textLocation = new Location(350, 450);
	private final Hand[] hands = new Hand[nPlayers];
	private final Hand[] copiedHands = new Hand[nPlayers];
	private Hand starter;
	private Hand crib;
	static private final IPlayer[] players = new IPlayer[nPlayers];
	static private int[] scores = new int[nPlayers];
	final Font normalFont = new Font("Serif", Font.BOLD, 24);
	final Font bigFont = new Font("Serif", Font.BOLD, 36);
	static Random random;
	static boolean ANIMATE;
	static int SEED;

	public enum Suit {
		CLUBS, DIAMONDS, HEARTS, SPADES
	}

	public enum Rank {
		// Order of cards is tied to card images
		ACE(1, 1), KING(13, 10), QUEEN(12, 10), JACK(11, 10), TEN(10, 10), NINE(9, 9), EIGHT(8, 8), SEVEN(7, 7),
		SIX(6, 6), FIVE(5, 5), FOUR(4, 4), THREE(3, 3), TWO(2, 2);

		public final int order;
		public final int value;

		Rank(int order, int value) {
			this.order = order;
			this.value = value;
		}
	}

	class MyCardValues implements Deck.CardValues { // Need to generate a unique value for every card
		public int[] values(Enum suit) { // Returns the value for each card in the suit
			return Stream.of(Rank.values())
					.mapToInt(r -> (((Rank) r).order - 1) * (Suit.values().length) + suit.ordinal()).toArray();
		}
	}

	class Segment {
		Hand segment;
		boolean go;
		int lastPlayer;
		boolean newSegment;

		void reset(final List<Hand> segments) {
			segment = new Hand(deck);
			segment.setView(Cribbage.this, new RowLayout(segmentLocations[segments.size()], segmentWidth));
			segment.draw();
			go = false; // No-one has said "go" yet
			lastPlayer = -1; // No-one has played a card yet in this segment
			newSegment = false; // Not ready for new segment yet
		}
	}

	static int cardValue(Card c) {
		return ((Cribbage.Rank) c.getRank()).value;
	}

	/*
	 * Canonical String representations of Suit, Rank, Card, and Hand
	 */

	public static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
		int x = random.nextInt(clazz.getEnumConstants().length);
		return clazz.getEnumConstants()[x];
	}

	void transfer(Card c, Hand h) {
		if (ANIMATE) {
			c.transfer(h, true);
		} else {
			c.removeFromHand(true);
			h.insert(c, true);
		}
	}

	private void dealingOut(Hand pack, Hand[] hands) {
		for (int i = 0; i < nStartCards; i++) {
			for (int j = 0; j < nPlayers; j++) {
				Card dealt = randomCard(pack);
				dealt.setVerso(false); // Show the face
				transfer(dealt, hands[j]);
			}
		}
	}

	public static Card randomCard(Hand hand) {
		int x = random.nextInt(hand.getNumberOfCards());
		return hand.get(x);
	}

	public static void setStatus(String string) {
		cribbage.setStatusText(string);
	}

	private void initScore() {
		for (int i = 0; i < nPlayers; i++) {
			scores[i] = 0;
			scoreActors[i] = new TextActor("0", Color.WHITE, bgColor, bigFont);
			addActor(scoreActors[i], scoreLocations[i]);
		}
	}

	public static void addScore(int player, int amount, String type, Hand hand) throws ArrayIndexOutOfBoundsException {
		scores[player] += amount;
		LogSystem.getInstance().logScore(player, scores[player], amount, type, null);
		LogSystem.getInstance().logScore(player, scores[player], amount, type, hand);
	}

	public void updateScore(int player) {
		removeActor(scoreActors[player]);
		scoreActors[player] = new TextActor(String.valueOf(scores[player]), Color.WHITE, bgColor, bigFont);
		addActor(scoreActors[player], scoreLocations[player]);
	}

	private void deal(Hand pack, Hand[] hands) {
		for (int i = 0; i < nPlayers; i++) {
			hands[i] = new Hand(deck);
			// players[i] = (1 == i ? new HumanPlayer() : new RandomPlayer());
			players[i].setId(i);
			players[i].startSegment(deck, hands[i]);
		}
		RowLayout[] layouts = new RowLayout[nPlayers];
		for (int i = 0; i < nPlayers; i++) {
			layouts[i] = new RowLayout(handLocations[i], handWidth);
			layouts[i].setRotationAngle(0);
			// layouts[i].setStepDelay(10);
			hands[i].setView(this, layouts[i]);
			hands[i].draw();
		}
		layouts[0].setStepDelay(0);

		dealingOut(pack, hands);
		for (int i = 0; i < nPlayers; i++) {
			hands[i].sort(Hand.SortType.POINTPRIORITY, true);
			LogSystem.getInstance().logDeal(i, hands[i]);
		}
		layouts[0].setStepDelay(0);
	}

	private void discardToCrib() {
		crib = new Hand(deck);
		RowLayout layout = new RowLayout(cribLocation, cribWidth);
		layout.setRotationAngle(0);
		crib.setView(this, layout);
		// crib.setTargetArea(cribTarget);
		crib.draw();
		for (IPlayer player : players) {
			Hand discardHand = new Hand(deck);
			for (int i = 0; i < nDiscards; i++) {
				Card c = player.discard();
				transfer(c, crib);
				// make a copy version for logging
				discardHand.insert(c.getSuit(), c.getRank(), false);
			}
			crib.sort(Hand.SortType.POINTPRIORITY, true);
			discardHand.sort(Hand.SortType.POINTPRIORITY, true);
			LogSystem.getInstance().logDiscard(player.id, discardHand);
		}
	}

	private void starter(Hand pack) {
		starter = new Hand(deck); // if starter is a Jack, the dealer gets 2 points
		RowLayout layout = new RowLayout(starterLocation, 0);
		layout.setRotationAngle(0);
		starter.setView(this, layout);
		starter.draw();
		Card dealt = randomCard(pack);
		dealt.setVerso(false);
		transfer(dealt, starter);
		ScoreSystem.getInstance().ScoringStarter(starter);
		LogSystem.getInstance().logStarter(starter.getFirst());

	}

	int total(Hand hand) {
		int total = 0;
		for (Card c : hand.getCardList())
			total += cardValue(c);
		return total;
	}

	private void play() {
		ScoreSystem scoreSystem = ScoreSystem.getInstance();
		final int thirtyone = 31;
		List<Hand> segments = new ArrayList<>();
		int currentPlayer = 0; // Player 1 is dealer
		Segment s = new Segment();
		s.reset(segments);
		while (!(players[0].emptyHand() && players[1].emptyHand())) {
			// System.out.println("segments.size() = " + segments.size());
			Card nextCard = players[currentPlayer].lay(thirtyone - total(s.segment), s.segment);
			if (nextCard == null) {
				if (s.go) {
					// Another "go" after previous one with no intervening cards
					// lastPlayer gets 1 point for a "go"
					s.newSegment = true;
					System.out.println("GO!");
				} else {
					// currentPlayer says "go"
					s.go = true;
				}
				currentPlayer = (currentPlayer + 1) % nPlayers;
			} else {
				s.lastPlayer = currentPlayer; // last Player to play a card in this segment
				transfer(nextCard, s.segment);
				LogSystem.getInstance().logPlay(currentPlayer, total(s.segment), nextCard);
				// add play scoring//
				scoreSystem.ScoringPlay(s.segment, currentPlayer);
				updateScore(currentPlayer);
				//

				if (total(s.segment) == thirtyone) {
					// lastPlayer gets 2 points for a 31
					s.newSegment = true;
					currentPlayer = (currentPlayer + 1) % nPlayers;
				} else {
					if (!s.go) { // if the other not already "go" (s.go is False) then switch to him
						currentPlayer = (currentPlayer + 1) % nPlayers;
					}
				}
			}
			if (s.newSegment) {
				segments.add(s.segment);
				s.reset(segments);
			}
		}
		// score to last player if it not 31
		if (s.lastPlayer != -1) {
			scoreSystem.ScoringGo(s.lastPlayer);
			updateScore(s.lastPlayer);
		}
	}

	void showHandsCrib() {
		ScoreSystem scoreSystem = ScoreSystem.getInstance();
		LogSystem logger = LogSystem.getInstance();
		// Scoring for player
		for (int i = 0; i < nPlayers; i++) {
			logger.logShow(i, starter.getFirst(), copiedHands[i]);
			scoreSystem.ScoringShow(starter, copiedHands[i], i);
			updateScore(i);
		}
		// score crib (for dealer)
		logger.logShow(DEALER, starter.getFirst(), crib);
		scoreSystem.ScoringShow(starter, crib, DEALER);
		updateScore(DEALER);
	}

	void backupCards() {
		for (int i = 0; i < nPlayers; i++) {
			copiedHands[i] = new Hand(deck);
			for (Card c : hands[i].getCardList()) {
				Enum cardSuit = c.getSuit();
				Enum cardRank = c.getRank();
				copiedHands[i].insert(cardSuit, cardRank, false);
			}
		}
	}

	public Deck getDeck() {
		return this.deck;
	}

	public Cribbage() {
		super(850, 700, 30);
		// game speed, higher = slower
		setSimulationPeriod(1);
		cribbage = this;
		setTitle("Cribbage (V" + version + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
		setStatusText("Initializing...");
		initScore();

		LogSystem logger = LogSystem.getInstance();
		logger.logSeed();
		logger.logPlayer(nPlayers);

		Hand pack = deck.toHand(false);
		RowLayout layout = new RowLayout(starterLocation, 0);
		layout.setRotationAngle(0);
		pack.setView(this, layout);
		pack.setVerso(true);
		pack.draw();
		addActor(new TextActor("Seed: " + SEED, Color.BLACK, bgColor, normalFont), seedLocation);

		/* Play the round */
		deal(pack, hands);
		discardToCrib();
		starter(pack);
		backupCards();
		play();
		showHandsCrib();

		addActor(new Actor("sprites/gameover.gif"), textLocation);
		setStatusText("Game over.");
		refresh();
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException,
			InvocationTargetException, InstantiationException, IllegalAccessException {
		/* Handle Properties */
		// System.out.println("Working Directory = " + System.getProperty("user.dir"));
		Properties cribbageProperties = new Properties();
		// Default properties
		cribbageProperties.setProperty("Animate", "true");
		cribbageProperties.setProperty("Player0", "cribbage.RandomPlayer");
		cribbageProperties.setProperty("Player1", "cribbage.HumanPlayer");

		// Read properties
		try (FileReader inStream = new FileReader("cribbage.properties")) {
			cribbageProperties.load(inStream);
		}

		// Load score setting
		ScoreSystem.getInstance().loadScoreSetting("cribbage.properties");

		// Control Graphics
		ANIMATE = Boolean.parseBoolean(cribbageProperties.getProperty("Animate"));

		// Control Randomisation
		/* Read the first argument and save it as a seed if it exists */
		if (args.length > 0) { // Use arg seed - overrides property
			SEED = Integer.parseInt(args[0]);
		} else { // No arg
			String seedProp = cribbageProperties.getProperty("Seed"); // Seed property
			if (seedProp != null) { // Use property seed
				SEED = Integer.parseInt(seedProp);
			} else { // and no property
				SEED = new Random().nextInt(); // so randomise
			}
		}
		random = new Random(SEED);

		// Control Player Types
		Class<?> clazz;
		clazz = Class.forName(cribbageProperties.getProperty("Player0"));
		players[0] = (IPlayer) clazz.getConstructor().newInstance();
		clazz = Class.forName(cribbageProperties.getProperty("Player1"));
		players[1] = (IPlayer) clazz.getConstructor().newInstance();
		// End properties

		new Cribbage();
	}

}

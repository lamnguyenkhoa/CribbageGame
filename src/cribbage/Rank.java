package cribbage;

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

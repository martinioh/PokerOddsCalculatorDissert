package com.example.pokeroddscalculator;

public class Card {
    private int rank, suit;

    private static String[] suits = { "Spades", "Hearts", "Clubs", "Diamonds" };
    private static String[] ranks  = { "Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King" };

    public static String rankAsString( int __rank ) {
        return ranks[__rank];
    }

    Card(byte suit, byte rank)
    {
        this.rank=rank;
        this.suit=suit;
    }

    public @Override String toString()
    {
        return ranks[rank] + " of " + suits[suit];
    }

    public int getRank() {
        return rank;
    }

    public int getSuit() {
        return suit;
    }
}



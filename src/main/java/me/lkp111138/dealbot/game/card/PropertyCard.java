package me.lkp111138.dealbot.game.card;

public interface PropertyCard extends Card {
    int[] propertySetCounts = {2, 3, 3, 3, 3, 3, 3, 2, 4, 2};
    int[][] propertyRents = {
            {1, 2},
            {1, 2, 3},
            {1, 2, 4},
            {1, 3, 5},
            {2, 3, 6},
            {2, 4, 6},
            {2, 4, 7},
            {3, 8},
            {1, 2, 3, 4},
            {1, 2}
    };
}

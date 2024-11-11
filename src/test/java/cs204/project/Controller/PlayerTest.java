package cs204.project.Controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    private Player player;
    private static final Long TEST_ID = 1L;
    private static final int TEST_RANK = 100;

    @BeforeEach
    void setUp() {
        // Create a new Player instance before each test
        player = new Player(TEST_ID, TEST_RANK);
    }

    @Test
    void testConstructor() {
        // Test that the constructor properly sets values
        assertEquals(TEST_ID, player.getId());
        assertEquals(TEST_RANK, player.getRank());
    }

    @Test
    void testGetId() {
        // Test getId method
        assertEquals(TEST_ID, player.getId());
    }

    @Test
    void testGetRank() {
        // Test getRank method
        assertEquals(TEST_RANK, player.getRank());
    }

    @Test
    void testSetRank() {
        // Test setRank method
        int newRank = 200;
        player.setRank(newRank);
        assertEquals(newRank, player.getRank());
    }

    @Test
    void testSetRankWithNegativeValue() {
        // Test setting a negative rank
        int negativeRank = -10;
        player.setRank(negativeRank);
        assertEquals(negativeRank, player.getRank());
    }

    @Test
    void testSetRankWithZero() {
        // Test setting rank to zero
        player.setRank(0);
        assertEquals(0, player.getRank());
    }
}
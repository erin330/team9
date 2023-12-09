package engine;

import engine.Score;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ScoreTest {
    private Score score1;
    private Score score2;

    @BeforeEach
    public void setUp() {
        // 设置测试所需的初始数据
        score1 = new Score("ABC", 100);
        score2 = new Score("DEF", 200);
    }

    @Test
    public void testGetName() {
        // 测试 getName() 方法
        assertEquals("ABC", score1.getName());
        assertEquals("DEF", score2.getName());
    }

    @Test
    public void testGetScore() {
        // 测试 getScore() 方法
        assertEquals(100, score1.getScore());
        assertEquals(200, score2.getScore());
    }

    @Test
    public void testCompareTo() {
        // 测试 compareTo() 方法
        assertEquals(1, score1.compareTo(score2)); // score1的分数小于score2，应返回正数
        assertEquals(-1, score2.compareTo(score1)); // score2的分数大于score1，应返回负数
        assertEquals(0, score1.compareTo(score1)); // 相同的分数应返回0
    }
}

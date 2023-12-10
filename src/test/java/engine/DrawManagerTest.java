package engine;

import org.junit.jupiter.api.DisplayName;
import org.mockito.ArgumentCaptor;
import screen.Screen;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DrawManagerTest {
    private static Screen mockScreen;
    private static Frame mockFrame;
    private static BufferedImage mockBackBuffer;
    private static Graphics mockBackBufferGraphics;
    private static DrawManager testDrawManager;

    @org.junit.jupiter.api.BeforeAll
    static void setuoTest(){
        mockScreen = mock(Screen.class);
        mockFrame = mock(Frame.class);
        mockBackBuffer = mock(BufferedImage.class);
        mockBackBufferGraphics = mock(Graphics.class);

        when(mockScreen.getWidth()).thenReturn(100);
        when(mockScreen.getHeight()).thenReturn(100);

        when(mockFrame.getBottomHudHeight()).thenReturn(10);

        when(mockBackBuffer.getGraphics()).thenReturn(mockBackBufferGraphics);

        when(mockBackBufferGraphics.getFontMetrics()).thenReturn(mock(FontMetrics.class));

        testDrawManager = DrawManager.getInstance();
        testDrawManager.setFrame(mockFrame);
        testDrawManager.initDrawing(mockScreen, mockBackBuffer);

    }

    @org.junit.jupiter.api.Test
    @DisplayName("Example test for drawHighScore")
    void drawHighScore() {
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);

        testDrawManager.drawHighScore(mockScreen, 1);
        verify(mockBackBufferGraphics, atLeastOnce()).drawString(argument.capture(), anyInt(), anyInt());
        assertEquals("0001", argument.getValue());
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Example test for drawLives")
    void drawAmmo() {
        ArgumentCaptor<String> drawString = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> horizontal = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> vertical = ArgumentCaptor.forClass(Integer.class);

        testDrawManager.drawAmmo(mockScreen, 5, 0);
        verify(mockBackBufferGraphics, atLeastOnce()).drawString(drawString.capture(), horizontal.capture(), vertical.capture());
        assertEquals("BUL: 10/5", drawString.getValue());
        assertEquals(10, horizontal.getValue());
        assertEquals(125, vertical.getValue());
    }
}
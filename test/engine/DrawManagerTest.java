package engine;

import org.junit.jupiter.api.DisplayName;
import org.mockito.ArgumentCaptor;
import screen.Screen;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DrawManagerTest {

    @org.junit.jupiter.api.Test
    @DisplayName("Mock test for drawHighScore")
    void drawHighScore() {
        Screen mockScreen = mock(Screen.class);
        Frame mockFrame = mock(Frame.class);
        BufferedImage mockBackBuffer = mock(BufferedImage.class);
        Graphics mockBackBufferGraphics = mock(Graphics.class);

        when(mockScreen.getWidth()).thenReturn(100);
        when(mockScreen.getHeight()).thenReturn(100);

        when(mockFrame.getBottomHudHeight()).thenReturn(10);

        when(mockBackBuffer.getGraphics()).thenReturn(mockBackBufferGraphics);

        when(mockBackBufferGraphics.getFontMetrics()).thenReturn(mock(FontMetrics.class));

        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);

        DrawManager testDrawManager = DrawManager.getInstance();
        testDrawManager.setFrame(mockFrame);
        testDrawManager.initDrawing(mockScreen, mockBackBuffer);

        verify(mockScreen, times(1)).getWidth();
        verify(mockScreen, times(1)).getWidth();


        testDrawManager.drawHighScore(mockScreen, 1);

        verify(mockScreen, times(2)).getWidth();
        verify(mockBackBufferGraphics, times(1)).drawString(anyString(), anyInt(), anyInt());
        verify(mockBackBufferGraphics).drawString(argument.capture(), anyInt(), anyInt());
        assertEquals("0001", argument.getValue());


    }
}
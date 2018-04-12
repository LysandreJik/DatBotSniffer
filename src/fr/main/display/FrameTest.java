package fr.main.display;

import org.junit.Test;

public class FrameTest {

    Frame frame;

    @Test
    public void testDisplay() throws InterruptedException {
        frame = new Frame();
        frame.display();
        Thread.sleep(200);
        frame.dispose();
    }

    @Test
    public void testAddPacket() throws InterruptedException {
        frame = new Frame();
        frame.display();
        Thread.sleep(200);
        frame.addPacket(50, "NAME", "tooltip", "VALUE");
        Thread.sleep(200);
        frame.dispose();
    }

    @Test(expected = NullPointerException.class)
    public void testNonInitAddPacket() {
        frame = new Frame();
        frame.addPacket(50, "", "tooltip", "");
    }

    @Test(expected = NullPointerException.class)
    public void testNonInitDispose() {
        frame = new Frame();
        frame.dispose();
    }
}
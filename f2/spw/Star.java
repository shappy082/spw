package f2.spw;

import java.awt.Color;
import java.awt.Graphics2D;

public class Star extends Sprite {

    private boolean alive = true;

    public Star(int x, int y) {
        super(x, y, 1, 1);
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(new Color((int)(Math.random() * 0x1000000)));
        g.fillOval(x, y, width, height);
    }

    public boolean isAlive() {
        return alive;
    }
}
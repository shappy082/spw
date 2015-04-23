package f2.spw;

import java.awt.Color;
import java.awt.Graphics2D;

public class Nuclear extends Sprite {

    private int step = 5;
    private boolean alive = true;

    public Nuclear(int x, int y) {
        super(x, y, 9, 9);
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.YELLOW);
        g.fillOval(x, y, width, height);
    }

    public void proceed() {
        y += step;
    }

    public boolean isAlive() {
        return alive;
    }
}
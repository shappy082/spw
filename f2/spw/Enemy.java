package f2.spw;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;

public class Enemy extends Sprite{
	public static final int Y_TO_FADE = 400;
	public static final int Y_TO_DIE = 600;
	
	private int step = 10;
	private double direction;
	private boolean alive = true;
	
	public Enemy(int x, int y) {
		super(x, y, 5, 15);
	}

	@Override
	public void draw(Graphics2D g) {
		if(y < Y_TO_FADE)
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		else{
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 
					(float)(Y_TO_DIE - y)/(Y_TO_DIE - Y_TO_FADE)));
		}
		g.setColor(Color.RED);
		g.fillOval(x, y, width, height);
		//g.fillRect(x, y, width, height);
	}

	public void proceed(){
		direction = Math.random();
		if (direction < 0.25 && x > 0) {
			x+=step/2;
		}
		else if (direction < 0.50 && x < 400) {
			x-=step/2;
		}
		else x+=0;
		
		y += step;
		if(y > Y_TO_DIE){
			alive = false;
		}
	}
	
	public boolean isAlive(){
		return alive;
	}
}

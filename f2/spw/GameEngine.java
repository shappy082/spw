package f2.spw;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.Timer;


public class GameEngine implements KeyListener, GameReporter{
	GamePanel gp;
		
	private ArrayList<Enemy> enemies = new ArrayList<Enemy>();	
	private SpaceShip v;	
	private Timer timer;

	private int gameSpeed = 50;
	private long score = 0;
	private double difficulty = 0.1;

	private boolean gameoverStatus = false;
	
	public GameEngine(GamePanel gp, SpaceShip v) {
		this.gp = gp;
		this.v = v;	
		
		gp.sprites.add(v);
		
		timer = new Timer(gameSpeed, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				process();
			}
		});
		timer.setRepeats(true);
	}
	
	public void start(){
		timer.start();
	}
	
	private void generateEnemy(){
		Enemy e = new Enemy((int)(Math.random()*390), 30);
		gp.sprites.add(e);
		enemies.add(e);
	}
	
	private void process(){
		if(Math.random() < difficulty){
			generateEnemy();
		}
		
		Iterator<Enemy> e_iter = enemies.iterator();
		while(e_iter.hasNext()){
			Enemy e = e_iter.next();
			e.proceed();
			
			if(!e.isAlive()){
				e_iter.remove();
				gp.sprites.remove(e);
				score += 100;
				if (score % 2000 == 0) {
                    if (difficulty >= 1.0) {
                        difficulty = 1.0;
                    }
                    else difficulty += 0.1;
                }
			}
		}
		
		gp.updateGameUI(this);
		
		Rectangle2D.Double vr = v.getRectangle();
		Rectangle2D.Double er;
		for(Enemy e : enemies){
			er = e.getRectangle();
			if(er.intersects(vr)){
				die();
				return;
			}
		}
	}
	
	public void die(){
		gameoverStatus = true;
		timer.stop();
		gp.updateGameUI(this);
	}
	
	void controlVehicle(KeyEvent e) {
		switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                v.moveLR(-1);
                break;
            case KeyEvent.VK_RIGHT:
                v.moveLR(1);
                break;
            case KeyEvent.VK_UP:
                v.moveUD(-1);
                break;
            case KeyEvent.VK_DOWN:
                v.moveUD(1);
                break;
            case KeyEvent.VK_H: //Harder
                difficulty += 0.10;
                break;
            case KeyEvent.VK_E: //Easier
                difficulty -= 0.10;
                break;
            case KeyEvent.VK_ENTER: //Restart Game
            	if (gameoverStatus == true) {
            		restart();
            	}
            	else break;
		}
	}

	public boolean getGameoverStatus(){
        return gameoverStatus;
    }

	public double getDifficulty(){
		return difficulty;
	}

	public long getScore(){
		return score;
	}
	
	private void restart(){
		gameoverStatus = false;
		clear();
		start();
		difficulty = 0.1;
		score = 0;
		v.x = 180;
		v.y = 550;
	}

	public void clear() {
        gp.sprites.removeAll(enemies);
        enemies.clear();
    }

	@Override
	public void keyPressed(KeyEvent e) {
		controlVehicle(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		//do nothing
	}

	@Override
	public void keyTyped(KeyEvent e) {
		//do nothing		
	}
}

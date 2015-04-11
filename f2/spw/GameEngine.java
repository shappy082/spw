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
	private ArrayList<Medic> medics = new ArrayList<Medic>();
	private SpaceShip v;

	private Timer timer;
	private Timer gameTime;

	private int gameSpeed = 50;
	private int hp = 2000;

	private long timeCount = 0;
	private long time = 0;
	private long score = 0;
	private double difficulty = 0.1;
	private double medicChange = 0.01;

	private boolean gameoverStatus = false;
	private boolean pauseStatus = false;

	private Sound bgm = new Sound("space.wav");
	private Sound gameoverSound = new Sound("gameOver.wav");
	private Sound healthSound = new Sound("health.wav");
	private Sound hitSound = new Sound("hit.wav");
	
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
		bgm.loop();
		bgm.play();
	}
	
	private void generateEnemy(){
		Enemy e = new Enemy((int)(Math.random()*390), 30);
		gp.sprites.add(e);
		enemies.add(e);
	}

	private void generateMedic(){
		Medic m = new Medic((int)(Math.random()*390), 30);
		gp.sprites.add(m);
		medics.add(m);
	}
	
	private void process(){
		timeCount++;
		if (timeCount%20 == 0) {
			time++;
		}

		if (Math.random() < difficulty){
			generateEnemy();
		}
		if (Math.random() < medicChange){
			generateMedic();
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
                    else difficulty += 0.05;
                }
			}
		}

		Iterator<Medic> m_iter = medics.iterator();
        while (m_iter.hasNext()) {
            Medic m = m_iter.next();
            m.proceed();

            if (!m.isAlive()) {
                m_iter.remove();
                gp.sprites.remove(m);
                score += 100;
            }
        }
		
		gp.updateGameUI(this);
		
//HIT by Enemy
		Rectangle2D.Double vr = v.getRectangle();
		Rectangle2D.Double er;
		for(Enemy e : enemies){
			er = e.getRectangle();
			if(er.intersects(vr)){
				gp.sprites.remove(e);
				hitSound.play();
				hp -= 100;
				if (hp <= 0) {
					hp = 0;
					die();
				}
			return;
			}
		}

//GET Medic
		Rectangle2D.Double mr = v.getRectangle();
    	Rectangle2D.Double rm;
    	for (Medic m : medics) {
            rm = m.getRectangle();
            if (rm.intersects(mr)) {
                gp.sprites.remove(m);
                healthSound.play();
                hp += 100;
                if (hp >= 4000) {
                	hp = 4000;
                }
                return;
            }
        }
	}

	public void die(){
		gameoverSound.play();
		gameoverStatus = true;
		timer.stop();
		bgm.stop();
		gp.updateGameUI(this);
	}

	public void pause(){
		if (pauseStatus == false && gameoverStatus == false) {
			pauseStatus = true;
			gp.updateGameUI(this);
			timer.stop();
		}
		else if (pauseStatus == true && gameoverStatus == false) {
			pauseStatus = false;
			timer.start();
		}
	}

	private void restart(){ 
		if (gameoverStatus == true) {
			difficulty = 0.1;
			score = 0;
			hp = 2000;
			v.x = 180;
			v.y = 550;
			time = 0;

			clear();
			start();
			gameoverStatus = false;
		}
	}

	public void clear() {
        gp.sprites.removeAll(enemies);
        enemies.clear();
        gp.sprites.removeAll(medics);
        medics.clear();
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
		}
	}

	void controlGame(KeyEvent e) {
		switch (e.getKeyCode()) {
            case KeyEvent.VK_H: //Harder
                difficulty += 0.10;
                break;
            case KeyEvent.VK_E: //Easier
                difficulty -= 0.10;
                break;
            case KeyEvent.VK_ENTER: //Restart Game
            	restart();
            	break;
            case KeyEvent.VK_P: //Pause Game
            	pause();
            	break;
            case KeyEvent.VK_M: //Release Medic **FOR DEBUG**
            	generateMedic();
            	break;
		}
	}

//GET game value
	public boolean getPauseStatus(){
		return pauseStatus;
	}

	public boolean getGameoverStatus(){
        return gameoverStatus;
    }

	public double getDifficulty(){
		return difficulty;
	}

	public int getHP(){
		return hp;
	}

	public long getTime(){
		return time;
	}

	public long getScore(){
		return score;
	}

	public SpaceShip getPosition(){
		return v;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (pauseStatus == false) {
			controlVehicle(e);
		}
		controlGame(e);
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

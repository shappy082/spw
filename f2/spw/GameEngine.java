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
	private ArrayList<Nuclear> nuclears = new ArrayList<>();
	private ArrayList<Star> stars = new ArrayList<Star>();
	private SpaceShip v;

	private Timer timer;
	private Timer gameTime;

	private int gameSpeed = 50;
	private int hp = 2000;

	private int numNuclear = 0;
	private long timeCount = 0;
	private long time = 0;
	private long score = 0;
	private double difficulty = 0.1;
	private double medicChange = 0.005;
	private double starChange = 0.5;
	private double nuclearChange = 0.0001;

	private boolean gameoverStatus = false;
	private boolean pauseStatus = false;

	private Sound bgm = new Sound("space.wav");
	private Sound gameoverSound = new Sound("gameOver.wav");
	private Sound itemSound = new Sound("health.wav");
	private Sound hitSound = new Sound("hit.wav");
	private Sound bombSound = new Sound("bomb.wav");
	
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

	private void generateNuclear() {
        Nuclear n = new Nuclear((int) (Math.random()*390), 30);
        gp.sprites.add(n);
        nuclears.add(n);
    }

    private void generateStar(){
		Star s = new Star((int)(Math.random()*390), (int)(Math.random()*640));
		gp.sprites.add(s);
		stars.add(s);
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
		if (Math.random() < starChange && stars.size() < 100){
			generateStar();
		}
		if ((float)Math.random() < nuclearChange) {
            generateNuclear();
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

        Iterator<Nuclear> n_iter = nuclears.iterator();
        while (n_iter.hasNext()) {
            Nuclear n = n_iter.next();
            n.proceed();

            if (!n.isAlive()) {
                n_iter.remove();
                gp.sprites.remove(n);
                score += 1000;
            }
        }

        Iterator<Star> s_iter = stars.iterator();
        while (s_iter.hasNext()) {
            Star s = s_iter.next();

            if (!s.isAlive()) {
                s_iter.remove();
                gp.sprites.remove(s);
            }
        }
		
		gp.updateGameUI(this);
		
//HIT by Enemy
		Rectangle2D.Double vr = v.getRectangle();
		Rectangle2D.Double er;
		for(Enemy e : enemies){
			er = e.getRectangle();
			if(er.intersects(vr)){
				enemies.remove(e);
				gp.sprites.remove(e);
				hitSound.play();
				hp -= 200;
				if (hp <= 0) {
					hp = 0;
					die();
				}
			return;
			}
		}
//Get Nuclear
		Rectangle2D.Double nr = v.getRectangle();
        Rectangle2D.Double rn;
        for (Nuclear n : nuclears) {
            rn = n.getRectangle();
            if (rn.intersects(nr)) {
                itemSound.play();
                nuclears.remove(n);
                gp.sprites.remove(n);
                numNuclear++;
                if (numNuclear > 3) {
                	numNuclear = 3;
                	bombSound.play();
                	gp.sprites.removeAll(medics);
                	clear();
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
            	medics.remove(m);
                gp.sprites.remove(m);
                itemSound.play();
                hp += 500;
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
			numNuclear = 0;
			hp = 2000;
			v.x = 180;
			v.y = 550;
			time = 0;
			gp.sprites.removeAll(stars);
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
        gp.sprites.removeAll(nuclears);
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
            case KeyEvent.VK_SPACE:
            	if (numNuclear != 0 && gameoverStatus == false) {
            		bombSound.play();
            		clear();
            		numNuclear--;
            	}
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
            case KeyEvent.VK_N: //Release Nuclear **FOR DEBUG**
            	generateNuclear();
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

	public int getNumNuclear(){
		return numNuclear;
	}

//CONTROL
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

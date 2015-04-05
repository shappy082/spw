package f2.spw;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

public class GamePanel extends JPanel {
	
	private BufferedImage bi;	
	Graphics2D big;
	ArrayList<Sprite> sprites = new ArrayList<Sprite>();

	public GamePanel() {
		bi = new BufferedImage(400, 600, BufferedImage.TYPE_INT_ARGB);
		big = (Graphics2D) bi.getGraphics();
		big.setBackground(Color.BLACK);
	}

	public void updateGameUI(GameReporter reporter){
		big.clearRect(0, 0, 400, 600);
		big.setColor(Color.WHITE);
		big.setFont(new Font ("Helvetica", Font.PLAIN, 12));

		//HP Display
		big.drawString(String.format("HP: %04d", reporter.getHP()), 20, 20);
		
		//Time Display
		long time = reporter.getTime();
		big.drawString(String.format("Time: %02d.%02d", time/60, time%60), 20, 40);

		//Score Display
        big.drawString(String.format("%08d", reporter.getScore()), 165, 20);

        //Difficult Displays
        if (reporter.getDifficulty() > 0.7) {
            big.drawString("Difficult: HARD", 280, 20);
        }
        else if (reporter.getDifficulty() > 0.4) {
            big.drawString("Difficult: NORMAL", 280, 20);
        }
        else if (reporter.getDifficulty() > 0.0) {
            big.drawString("Difficult: EASY", 280, 20);
        }

		//DRAW Object
        for (Sprite s : sprites) {
            s.draw(big);
        }

        //GAME OVER Display
        big.setColor(Color.WHITE);
        if (reporter.getGameoverStatus() == true) {
        	big.drawString("Press ENTER to play again", 122, 320);
        	big.setFont(new Font ("TimesRoman", Font.BOLD, 20));
            big.drawString("GAME OVER", 135, 300);
        }

		repaint();
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(bi, null, 0, 0);
	}
}

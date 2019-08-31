package scenes.visual;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.util.ArrayList;

import adt.Visual;
import adt.VisualElement;
import audio.SFX;
import elem.Player;
import handlers.GameHandler;
import scenes.Race;

//Vis også tid osv more information

public class WinVisual extends Visual {
	private Player player;
	private boolean everyoneDone;
	private boolean over = false;
	private String[] strings;
	private String theWinnerIntro;
	private int stage;
	private float theWinnerIntroAlpha;
	private float[] alphas;
	private int alphaIncIndex;
	private Color tc;
	private int x;
	private int y;

	public WinVisual() {
		visualElements = new ArrayList<VisualElement>();
		theWinnerIntro = "And the winner is:";
	}

	@Override
	public void tick(double tickFactor) {
		for (int i = 0; i < visualElements.size(); i++) {
			visualElements.get(i).tick(tickFactor);
		}

		if (everyoneDone) {
			if (stage == 0) {
				theWinnerIntroAlpha += 0.01f * tickFactor;
				if (theWinnerIntroAlpha >= 1) {
					theWinnerIntroAlpha = 1;
					stage++;
				}
			} else if (stage == 1) {
				theWinnerIntroAlpha -= 0.02f * tickFactor;
				if (theWinnerIntroAlpha <= 0) {
					theWinnerIntroAlpha = 0;
					stage++;
				}
			} else if (stage == 2 && alphaIncIndex < alphas.length) {

				if (alphas[alphaIncIndex] < 1f) {
					alphas[alphaIncIndex] += 0.05f * tickFactor;
					if (alphas[alphaIncIndex] > 1)
						alphas[alphaIncIndex] = 1;
				} else {
					alphaIncIndex++;
				}
			} else {
				over = true;
			}
		}
	}

	@Override
	public void render(Graphics g) {

		g.setColor(Color.BLACK);
		g.fillRect(0, 0, Race.WIDTH, Race.HEIGHT);
		Graphics2D g2d = (Graphics2D) g;

		if (everyoneDone) {
			g2d.setColor(tc);
			g2d.setFont(font);
			AlphaComposite ac = null;

			if (stage > 1) {
				for (int i = 0; i < strings.length; i++) {
					ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphas[i]);
					g2d.setComposite(ac);
					g2d.drawString(strings[i], x + font.getSize(), y + (i + 1) * (font.getSize()));
				}
			} else {
				ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, theWinnerIntroAlpha);
				g2d.setComposite(ac);
				g2d.drawString(theWinnerIntro, font.getSize(), (Race.HEIGHT / 2) - (font.getSize() / 2));
			}

			g2d.setComposite(ac.derive(1f));
		}
		for (int i = 0; i < visualElements.size(); i++) {
			visualElements.get(i).render(g);
		}

	}

	@Override
	public void setRace(Race race) {

	}

	@Override
	public void setPlayer(Player player) {
		this.player = player;
	}

	@Override
	public boolean hasAnimationsRunning() {
		return false;
	}

	public void setEveryoneDone(boolean everyoneDone) {
		this.everyoneDone = everyoneDone;
		if (everyoneDone == true) {
			claimWinner();
			player.stopAllClientHandlerOperations();
			player.endClientHandler();
		}
	}

	private void claimWinner() {
		tc = Color.white;
		GameHandler.music.stop();
		SFX.playMP3Sound("winsound");
		strings = player.getWinner().split("#");
		alphas = new float[strings.length];
		font = new Font("Calibri", 0, Race.WIDTH / 25);
	}

	public boolean isOver() {
		return over;
	}

}

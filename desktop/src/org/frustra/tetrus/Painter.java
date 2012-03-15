package org.frustra.tetrus;

public class Painter extends Thread {
	{ this.setDaemon(true); }
	
	private TetrUs app;
	public static int fps = 0;
	
	public Painter(TetrUs app) {
		this.app = app;
		this.start();
	}
	
	public void run() {
		int fps = 0;
		long timer = System.currentTimeMillis();
		long time;
		while (true) {
			if (app.drawTrigger) {
				app.repaint();
				fps++;
				time = System.currentTimeMillis();
				if (time - timer > 1000) {
					Painter.fps = fps;
					fps = 0;
					timer = time;
				}
			}
			try {
				Thread.sleep(app.logicSpeed);
			} catch (InterruptedException e) {
				return;
			}
		}
	}
}

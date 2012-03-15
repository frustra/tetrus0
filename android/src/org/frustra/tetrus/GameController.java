package org.frustra.tetrus;


public class GameController extends Thread {
	{ this.setDaemon(true); }
	
	TetrUsGame v;
	
	int fallSpeed = 750;
	
	public GameController(TetrUsGame v) {
		this.v = v;
		this.start();
	}
	
	public void run() {
		long timer = System.currentTimeMillis();
		boolean placeOnNext = false;
		while (true) {
			if (v.isStarted()) {
				int tmpFallSpeed = v.fastFall ? fallSpeed / 10 : fallSpeed;
				if (System.currentTimeMillis() - timer > tmpFallSpeed) {
					if (v.collideBlock(v.currPiece, v.currX, v.currY + 1)) {
						if (placeOnNext) {
							v.placeBlock(v.currPiece, v.currBlock, v.currX, v.currY);
							v.getNewBlock();
							placeOnNext = false;
						} else placeOnNext = true;
					} else {
						placeOnNext = false;
						v.drawTrigger = true;
						v.currY++;
					}
					timer = System.currentTimeMillis();
				}
				if (System.currentTimeMillis() - v.keyTimer > 75 && (v.leftMoveCount > TetrUsGame.SLIDE_TOLERANCE || v.rightMoveCount > TetrUsGame.SLIDE_TOLERANCE)) {
					if (v.leftMoveCount > TetrUsGame.SLIDE_TOLERANCE) {
						if (!v.collideBlock(v.currPiece, v.currX - 1, v.currY)) {
							//drawTrigger = true;
							v.currX--;
							v.leftMoveCount = 0;
						}
					} else if (v.rightMoveCount > TetrUsGame.SLIDE_TOLERANCE) {
						if (!v.collideBlock(v.currPiece, v.currX + 1, v.currY)) {
							//drawTrigger = true;
							v.currX++;
							v.rightMoveCount = 0;
						}
					}
					v.keyTimer = System.currentTimeMillis();
				}
				
			}
			
			if (v.drawTrigger) v.getPaintView().forceRedraw();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				return;
			}
		}
	}
}

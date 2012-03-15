package org.frustra.tetrus;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.MotionEvent;

public class TetrUsGame {
    private PaintView view;
    private static final float TOUCH_TOLERANCE = 6;
    protected static final int SLIDE_TOLERANCE = 1;

	protected long keyTimer = 0;
	protected int nextBlock = -1;
	protected int currBlock = -1;
	protected int currX = -1;
	protected int currY = -1;
	protected int lastRow;
	protected boolean[][] currPiece = new boolean[0][0];
	protected int[][] blocks = new int[10][18];
	protected boolean drawTrigger = false;
	protected int leftMoveCount, rightMoveCount;
	private boolean started = false;
	protected boolean fastFall;
    
	@SuppressWarnings("unchecked")
	ArrayList<Integer>[] rows = new ArrayList[256];
	
    int[] colors = new int[] {
		Color.rgb(0, 255, 0),
		Color.rgb(102, 204, 255),
		Color.rgb(255, 0, 0),
		Color.rgb(0, 0, 255),
		Color.rgb(204, 0, 255),
		Color.rgb(255, 102, 0),
		Color.rgb(255, 255, 0),
	};
    
	boolean[][][] pieces = new boolean[][][] {
		{
			{true, true, false},
			{false, true, true}
		},
		{
			{false, true, true},
			{true, true, false}
		},
		{
			{true, true},
			{true, true}
		},
		{
			{false, false, true},
			{true, true, true}
		},
		{
			{true, false, false},
			{true, true, true}
		},
		{
			{true, true, true, true}
		},
		{
			{false, true, false},
			{true, true, true}
		}
	};
    
	public TetrUsGame(Context c) {
		nextBlock = -1;
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 18; y++) {
				blocks[x][y] = -1;
			}
		}
		getNewBlock();
		
		final Bitmap b = BitmapFactory.decodeResource(c.getResources(), R.drawable.banner);
		
        view = new PaintView(c) {
			public void onViewDraw(Canvas canvas) {
		        mPaint.setColor(Color.RED);

		        view.mPaint.setColor(Color.BLACK);
				canvas.drawRect(15, 15, 265, 515, view.mPaint);
				view.mPaint.setColor(Color.RED);
				canvas.drawRect(15, 63, 265, 67, view.mPaint);
				
				canvas.drawBitmap(b, null, new Rect(15, 15, 265, 73), view.mPaint);
				
				view.mPaint.setColor(Color.BLACK);
				canvas.drawRect(280, 15, 410, 95, view.mPaint);
		        try {
					synchronized (pieces[nextBlock]) {
						for (int y = 0; y < pieces[nextBlock].length; y++) {
							for (int x = 0; x < pieces[nextBlock][y].length; x++) {
								if (pieces[nextBlock][y][x]) {
									drawBlockABS(canvas, colors[nextBlock], 345 + (int) ((x - (pieces[nextBlock][y].length / 2.0)) * 25.0), 55 + (int) ((y - (pieces[nextBlock].length / 2.0)) * 25.0), 180);
								}
							}
						}
					}
					synchronized (currPiece) {
						for (int x = 0; x < currPiece.length; x++) {
							for (int y = 0; y < currPiece[x].length; y++) {
								if (currPiece[x][y]) {
									drawBlock(canvas, colors[currBlock], currX + x, currY + y, 255);
								}
							}
						}
					}
					synchronized (blocks) {
						for (int x = 0; x < 10; x++) {
							for (int y = 0; y < 18; y++) {
								if (blocks[x][y] > -1 && blocks[x][y] < colors.length) {
									drawBlock(canvas, colors[blocks[x][y]], x, y, 180);
								}
							}
						}
					}
				} catch (Exception e) { }
		        drawTrigger = false;
			}

			public boolean onDown(MotionEvent e) { return true; }

			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				//Log.v("TETRUS", "Flick velocity x: " + velocityX + " y: " + velocityY);
				if (velocityY > 1800) {
	    			while (true) {
	    				if (collideBlock(currPiece, currX, currY + 1)) {
	    					break;
	    				} else currY++;
	    			}
	    			return true;
				}
				return false;
			}

			public void onLongPress(MotionEvent e) { }

			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		        if (Math.abs(distanceX) >= TOUCH_TOLERANCE) {
		        	if (distanceX > 0) {
		        		leftMoveCount++;
						if (rightMoveCount > 0) rightMoveCount--;
		        	} else {
		        		rightMoveCount++;
						if (leftMoveCount > 0) leftMoveCount--;
		        	}
		        } else if (Math.abs(distanceY) >= TOUCH_TOLERANCE) {
		        	if (distanceY < -4) {
		        		fastFall = true;
		        	} else {
		        		fastFall = false;
		        	}
		        }
				return true;
			}

			public void onShowPress(MotionEvent e) { }

			public boolean onSingleTapUp(MotionEvent e) {
				if (started) {
					boolean[][] tmpPiece = new boolean[currPiece[0].length][currPiece.length];
					for (int x2 = 0; x2 < currPiece.length; x2++) {
						for (int y2 = 0; y2 < currPiece[x2].length; y2++) {
							if (currPiece[x2][y2]) tmpPiece[currPiece[x2].length - y2 - 1][x2] = true;
						}
					}
					int newX = (int) ((currPiece.length / 2.0) - (tmpPiece.length / 2.0));
					int newY = (int) ((tmpPiece.length / 2.0) - (currPiece.length / 2.0));
					if (!collideBlock(tmpPiece, currX + newX, currY + newY)) {
						currPiece = tmpPiece;
						currX += newX;
						currY += newY;
					} else {
						int radius = 2;
						for (int yOff = 0; yOff >= -radius; yOff--) {
							for (int xOff = 1; xOff <= radius; xOff++) {
								if (!collideBlock(tmpPiece, currX + newX + xOff, currY + newY - yOff)) {
									currPiece = tmpPiece;
									currX += newX + xOff;
									currY += newY - yOff;
									break;
								}
								if (!collideBlock(tmpPiece, currX + newX - xOff, currY + newY - yOff)) {
									currPiece = tmpPiece;
									currX += newX - xOff;
									currY += newY - yOff;
									break;
								}
								if (yOff != 0) {
									if (!collideBlock(tmpPiece, currX + newX + xOff, currY + newY + yOff)) {
										currPiece = tmpPiece;
										currX += newX + xOff;
										currY += newY + yOff;
										break;
									}
									if (!collideBlock(tmpPiece, currX + newX - xOff, currY + newY + yOff)) {
										currPiece = tmpPiece;
										currX += newX - xOff;
										currY += newY + yOff;
										break;
									}
								}
							}
						}
					}
				}
				return true;
			}

			public boolean onUp(MotionEvent event) {
				leftMoveCount = 0;
				rightMoveCount = 0;
				fastFall = false;
				return false;
			}
        };
        new GameController(this);
	}
	
	public void checkRows() {
		synchronized (blocks) {
			lastRow++;
			rows[lastRow & 0x00FF] = new ArrayList<Integer>();
			int row = 0;
			while (row >= 0) {
				row = -1;
				row: for (int y = 17; y >= 0; y--) {
					for (int x = 0; x < 10; x++) {
						if (blocks[x][y] < 0 && row < 0) {
							continue row;
						} else if (row >= 0) {
							blocks[x][y + 1] = blocks[x][y];
							blocks[x][y] = -1;
						}
					}
					row = y;
				}
				if (row >= 0) rows[lastRow & 0x00FF].add(row);
			}
		}
	}
	
	public void placeBlock(boolean[][] piece, int type, int x, int y) {
		synchronized (blocks) {
			for (int x2 = 0; x2 < piece.length; x2++) {
				for (int y2 = 0; y2 < piece[x2].length; y2++) {
					if (piece[x2][y2]) {
						if ((x + x2) >= 0 && (x + x2) < 10 && (y + y2) < 18 && (y + y2) >= 0) {
							blocks[x + x2][y + y2] = type;
						} else {
							// Lose game
							nextBlock = -1;
							getNewBlock();
							for (int x3 = 0; x3 < 10; x3++) {
								for (int y3 = 0; y3 < 18; y3++) {
									blocks[x3][y3] = -1;
								}
							}
							return;
						}
					}
				}
			}
		}
		checkRows();
		drawTrigger = true;
	}
	
	public boolean collideBlock(boolean[][] piece, int x, int y) {
		for (int x2 = 0; x2 < piece.length; x2++) {
			for (int y2 = 0; y2 < piece[x2].length; y2++) {
				if (piece[x2][y2] && ((x + x2) < 0 || (x + x2) >= 10 || (y + y2) >= 18 || ((y + y2) >= 0 && blocks[x + x2][y + y2] >= 0))) {
					return true;
				}
			}
		}
		return false;
	}
	
	public Path getPolygonPath(int[] x, int[] y, int numPoints) {
		Path p = new Path();
		p.moveTo(x[0], y[0]);
		for (int i = 1; i < numPoints; i++) {
			p.lineTo(x[i], y[i]);
		}
		return p;
	}
	
	public void drawBlock(Canvas g, int c, int x, int y, int alpha) {
		if (x >= 0 && y >= -2 && x < 10 && y < 18) {
			drawBlockABS(g, c, 15 + (25 * x), 65 + (25 * y), alpha);
		}
	}
	
	public void drawBlockABS(Canvas canvas, int c, int x, int y, int alpha) {
		/*if (alpha > 100) {
			view.mPaint.setColor(Color.BLACK);
			canvas.drawRect(x, y, 25, 25, view.mPaint);
		}*/
		view.mPaint.setColor(Color.argb(alpha, Math.min(255, Color.red(c) + 100), Math.min(255, Color.green(c) + 100), Math.min(255, Color.blue(c) + 100)));
		canvas.drawPath(getPolygonPath(new int[] {x, x + 25, x}, new int[] {y, y, y + 25}, 3), view.mPaint);
		view.mPaint.setColor(Color.argb(alpha, Math.max(0, Color.red(c) - 100), Math.max(0, Color.green(c) - 100), Math.max(0, Color.blue(c) - 100)));
		canvas.drawPath(getPolygonPath(new int[] {x + 25, x + 25, x}, new int[] {y, y + 25, y + 25}, 3), view.mPaint);
		view.mPaint.setColor(Color.argb(alpha, Color.red(c), Color.green(c), Color.blue(c)));
		canvas.drawRect(x + 3, y + 3, x + 21, y + 21, view.mPaint);
	}
	
	public PaintView getPaintView() {
		return view;
	}
	
	public void getNewBlock() {
		if (nextBlock < 0) nextBlock = (int) (Math.random() * 7);
		synchronized (currPiece) {
			synchronized (pieces[nextBlock]) {
				currBlock = nextBlock;
				nextBlock = (int) (Math.random() * 7);
				currX = 5 - (pieces[currBlock][0].length / 2);
				currY = -pieces[currBlock].length;
				currPiece = new boolean[pieces[currBlock][0].length][pieces[currBlock].length];
				for (int x = 0; x < currPiece.length; x++) {
					for (int y = 0; y < currPiece[x].length; y++) {
						if (pieces[currBlock][y][x]) currPiece[x][y] = true;
					}
				}
			}
		}
		drawTrigger = true;
	}
	
	public void resetGame() {
		nextBlock = -1;
		getNewBlock();
		for (int x3 = 0; x3 < 10; x3++) {
			for (int y3 = 0; y3 < 18; y3++) {
				blocks[x3][y3] = -1;
			}
		}
	}
	
	public boolean isStarted() {
		return started;
	}
	
	public void setStarted(boolean started) {
		this.started = started;
	}
}

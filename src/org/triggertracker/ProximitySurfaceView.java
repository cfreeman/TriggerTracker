package org.triggertracker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class ProximitySurfaceView extends SurfaceView implements
		SurfaceHolder.Callback {

	private ProximityThread thread;
	private Context ctx;

	// foreground image used for proximity view
	private Bitmap foregroundImage;

	private SurfaceHolder sh;
	private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

	public ProximitySurfaceView(Context context) {
		super(context);
		sh = getHolder();
		sh.addCallback(this);

		paint.setColor(Color.BLUE);
		paint.setStyle(Style.FILL);

		ctx = context;
		setFocusable(true);

		foregroundImage = 
				BitmapFactory.decodeResource(this.getResources(),R.drawable.beeper);
	}

	public ProximityThread getThread() {
		return thread;
	}

	public void surfaceCreated(SurfaceHolder holder) {
		thread = new ProximityThread(sh, ctx);

		thread.setRunning(true);
		thread.start();
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		thread.setSurfaceSize(width, height);
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		thread.setRunning(false);
		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}
	}

	public void setProximityValue(int proximity) {
		this.getThread().proximity = proximity;
	}

	class ProximityThread extends Thread {

		private int canvasWidth = 200;
		private int canvasHeight = 400;
		
		Bitmap scaledForegroundImage;

		private boolean run = false;

		static final int MAX_BACKGROUND_COLOUR_INTENSITY = 255;
		private int backgroundColourIntensity = MAX_BACKGROUND_COLOUR_INTENSITY;
		public int backgroundColourReductionValue = 5;

		// max distance between any 2 players
		static final int MAX_PLAYER_PROXIMITY = 250;
		
		//number used to scale proximity value to adjust flash interval
		static final int PROXIMITY_SCALE = 8;

		final static int MAX_FLASH_VALUE = 35;
		final static int MIN_FLASH_VALUE = 5;

		public int proximity = MAX_PLAYER_PROXIMITY;

		public ProximityThread(SurfaceHolder surfaceHolder, Context context) {
			sh = surfaceHolder;
			ctx = context;
			
			setSurfaceSize(getWidth(), getHeight());
		}

		public void doStart() {
			synchronized (sh) {
			}
		}

		public void run() {
			while (run) {
				Canvas c = null;
				try {
					c = sh.lockCanvas(null);
					synchronized (sh) {
						doDraw(c);
					}
				} finally {
					if (c != null) {
						sh.unlockCanvasAndPost(c);
					}
				}
			}
		}

		public void setRunning(boolean b) {
			run = b;
		}

		public void setSurfaceSize(int width, int height) {
			synchronized (sh) {
				canvasWidth = width;
				canvasHeight = height;
				
				//find scale values for foreground image
				float scaleWidth = ((float) canvasWidth) / foregroundImage.getWidth();
				float scaleHeight = ((float) canvasHeight) / foregroundImage.getHeight();

				//create scaling matrix
				Matrix matrix = new Matrix();
				matrix.postScale(scaleWidth, scaleHeight);

				//scale foreground image to fit the canvas
				scaledForegroundImage = Bitmap.createBitmap(
						foregroundImage, 0, 0, 
						foregroundImage.getWidth(),
						foregroundImage.getHeight(), 
						matrix, false);
				
				doStart();
			}
		}

		private void doDraw(Canvas canvas) {
			canvas.restore();

			backgroundColourReductionValue = MAX_FLASH_VALUE - (proximity / PROXIMITY_SCALE);

			if (backgroundColourReductionValue < MIN_FLASH_VALUE) {
				backgroundColourReductionValue = MIN_FLASH_VALUE;
			}

			if (backgroundColourIntensity > 0 + backgroundColourReductionValue) {
				backgroundColourIntensity -= backgroundColourReductionValue;
			} else {
				backgroundColourIntensity = MAX_BACKGROUND_COLOUR_INTENSITY;
			}

			canvas.drawColor(Color.rgb(backgroundColourIntensity, 0, 0));

			//draw foreground image to screen
			canvas.drawBitmap(scaledForegroundImage, 0, 0, paint);
		}
	}

}

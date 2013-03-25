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

public class ProximitySurfaceView extends SurfaceView  implements SurfaceHolder.Callback {

	ProximityThread thread;
	Context ctx;
	
	Bitmap mask;
	
	private SurfaceHolder sh;
	private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	public ProximitySurfaceView(Context context) {
	    super(context);
	    sh = getHolder();
	    sh.addCallback(this);
	    paint.setColor(Color.BLUE);
	    paint.setStyle(Style.FILL);
	        
	    ctx = context;
	    setFocusable(true); // make sure we get key events
	    
	    mask = BitmapFactory.decodeResource(this.getResources(), R.drawable.beeper);
	    
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
	 
	 public void setProximityValue(int proximity){
		 this.getThread().proximity = proximity;
	 }
	  
	 class ProximityThread extends Thread {
		  private int canvasWidth = 200;
		  private int canvasHeight = 400;
		  
		  private boolean run = false;
		  
		  private int redValue = 255;
		  public int flashValue = 5;
		  
		  final int MAX_FLASH_VALUE = 35;
		  final int MIN_FLASH_VALUE = 5;
		  
		  public int proximity = 255;
		 
		  public ProximityThread(SurfaceHolder surfaceHolder, Context context) {
		    sh = surfaceHolder;
		    ctx = context;
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
		      doStart();
		    }
		  }
		  private void doDraw(Canvas canvas) {
		    canvas.restore();
		    
		    flashValue = MAX_FLASH_VALUE - (proximity / 8);
		    
		    if(flashValue < MIN_FLASH_VALUE){
		    	flashValue = MIN_FLASH_VALUE;
		    }
		    
		    if(redValue > 0 + flashValue){
		    	redValue -= flashValue;
		    	System.err.println("HB FlashValue: " + flashValue);
		    }else{
		    	redValue = 255;
		    }
		    
		    canvas.drawColor(Color.rgb(redValue, 0, 0));
		    
		    float scaleWidth = ((float) getWidth()) / mask.getWidth();
		    float scaleHeight = ((float) getHeight()) / mask.getHeight();

		    Matrix matrix = new Matrix();
		    matrix.postScale(scaleWidth, scaleHeight);

		    Bitmap resized = Bitmap.createBitmap(mask, 0, 0, mask.getWidth(), mask.getHeight(), matrix, false);
		    
		    canvas.drawBitmap(resized, 0, 0, paint);
		  }
		}
	
}

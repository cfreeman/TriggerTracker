package org.triggertracker;

import java.text.DecimalFormat;
import java.util.List;

import android.R.string;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;

public class PlayerRadarView extends android.view.View {
	
	private static final int PLAYER_ICON_SIZE = 50;

	private Bitmap playerIcon;
	
	private float startAngle = 0;
	private float minimumScreenSize = 100;
	private float midpointX = 100;
	private float midpointY = 100;
	private float screenWidth = 100;
	private float screenHeight = 100;
	
	private float currentLatitude = -26.689806f;
	private float currentLongitude = 153.1358447f;
	
	private List<Player> playerLocations;
	
	protected final Paint paintRadarGrid = new Paint();
    protected final Paint paintRadar = new Paint();
    protected final Paint paintScreenText = new Paint();
    
    protected final int STROKEWIDTH = 2;
	
	private DecimalFormat df = new DecimalFormat("####.####");

	public PlayerRadarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.paintRadarGrid.setARGB(150, 0, 255, 0);
        this.paintRadarGrid.setAntiAlias(true);
        this.paintRadarGrid.setStyle(Style.STROKE);
        this.paintRadarGrid.setStrokeWidth(STROKEWIDTH);
        
        this.paintRadar.setARGB(150, 0, 255, 0);
        this.paintRadar.setAntiAlias(true);
        this.paintRadar.setStyle(Style.STROKE);
        this.paintRadar.setStrokeWidth(STROKEWIDTH);
        
        this.paintScreenText.setARGB(255, 0, 255, 0);
        this.paintScreenText.setTextSize(12);
        this.paintScreenText.setFakeBoldText(true);
        	
	}

	@Override
	protected void onDraw(Canvas canvas) {
		/*if (!hasInitialized) {
			initializeConstants();
		}*/
		
		minimumScreenSize = getHeight();
		
		drawRadarGrid(canvas);
		drawRadar(canvas);
		drawPlayers(canvas, playerLocations);
		String currentLocation = "( " + df.format(currentLongitude) + ", "
				+ df.format(currentLatitude) + " )";
		canvas.drawText(currentLocation, 25, 25,
				paintScreenText);
		
		canvas.drawText(Integer.toString(getWidth()), 25, 50,
				paintScreenText);

	
		canvas.drawText(Integer.toString(getHeight()), 25, 100,
				paintScreenText);}

	private void drawRadarGrid(Canvas canvas) {
		canvas.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2, paintRadarGrid);
		canvas.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight(), paintRadarGrid);
	}

	private void drawRadar(Canvas canvas) {
		if (startAngle > 360) {
			startAngle = 0;
		}
		float x = (float) (minimumScreenSize / 2 * Math.cos(startAngle
				* (Math.PI / 180)));
		float y = (float) (minimumScreenSize / 2 * Math.sin(startAngle
				* (Math.PI / 180)));
		canvas.drawLine(getWidth() / 2, getHeight() / 2, x + getWidth() / 2, y
				+ getHeight() / 2, paintRadar);
		startAngle += 3;
	}

	public void setPlayerLocations(List<Player> playerLocations) {
		this.playerLocations = playerLocations;
	}

	private void drawPlayers(Canvas canvas, List<Player> playerLocations) {
		if(playerLocations != null){
			if (playerIcon == null) {
				/*playerIcon = BitmapFactory.decodeResource(this.getResources(),
						R.drawable.player);*/
			}
			for (Player playerLoc : playerLocations) {
				float x = (float) ((currentLongitude * 400 - playerLoc.getLon() * 400) + (getWidth() / 2));
				float y = (float) ((currentLatitude * 400 - playerLoc.getLat() * 400) + (getHeight() / 2));
				System.err.println("Draw Player " + playerLoc.getId() + ": "+ x + "," + y + " : " + (currentLongitude * 100 - playerLoc.getLon() * 100) + "," + (currentLatitude * 100 - playerLoc.getLat() * 100));
				//canvas.drawBitmap(playerIcon, x, y, paintScreenText);
				int playerID = playerLoc.getId();
				canvas.drawText(Integer.toString(playerID), x, y, paintScreenText);
			}
		}
	}

}

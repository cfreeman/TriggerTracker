package org.triggertracker;

import java.util.List;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.location.Location;

public class PlayerRadarView extends android.view.View {
	
	//scaling value used on location data for drawing locations
	static final int RADAR_LOCATION_SCALE = 400;
	
	private float radarBeamStartAngle = 0;
	
	private float screenWidth = 100;
	private float screenHeight = 100;
	private float minimumScreenSize = 100;
	private float midpointX = 100;
	private float midpointY = 100;
	
	private Location currentLocation;
	private List<Player> playerLocations;
	
	protected final Paint paintRadarGrid = new Paint();
    protected final Paint paintRadar = new Paint();
    protected final Paint paintScreenText = new Paint();
    
    protected final int STROKEWIDTH = 2;

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
		
		screenWidth = getWidth();
		screenHeight = getHeight();
		
		minimumScreenSize = screenHeight;
		
		midpointX = screenWidth / 2;
		midpointY = screenHeight / 2;
		
		drawRadarGrid(canvas);
		drawRadar(canvas);
		drawPlayers(canvas, playerLocations);
	}

	private void drawRadarGrid(Canvas canvas) {
		canvas.drawLine(0, midpointY, screenWidth, midpointY, paintRadarGrid);
		canvas.drawLine(midpointX, 0, midpointX, screenHeight, paintRadarGrid);
	}

	//draw radar beam
	private void drawRadar(Canvas canvas) {
		if (radarBeamStartAngle > 360) {
			radarBeamStartAngle = 0;
		}
		float x = (float) (minimumScreenSize / 2 * Math.cos(radarBeamStartAngle
				* (Math.PI / 180)));
		float y = (float) (minimumScreenSize / 2 * Math.sin(radarBeamStartAngle
				* (Math.PI / 180)));
		
		canvas.drawLine(midpointX, midpointY, x + midpointX, y
				+ midpointY, paintRadar);
		
		radarBeamStartAngle += 3;
	}

	public void setPlayerLocations(Location currentLocation, List<Player> playerLocations) {
		this.currentLocation = currentLocation;
		this.playerLocations = playerLocations;
	}

	private void drawPlayers(Canvas canvas, List<Player> playerLocations) {
		if(playerLocations != null){
			for (Player playerLoc : playerLocations) {
				
				int playerID = playerLoc.getId();
				float y = (float) ((currentLocation.getLatitude() * RADAR_LOCATION_SCALE 
						- playerLoc.getLat() * RADAR_LOCATION_SCALE) + (midpointY));
				
				float x = (float) ((currentLocation.getLongitude() * RADAR_LOCATION_SCALE 
						- playerLoc.getLon() * RADAR_LOCATION_SCALE) + (midpointX));
				
				canvas.drawText(Integer.toString(playerID), x, y, paintScreenText);
				
			}
		}
	}

}

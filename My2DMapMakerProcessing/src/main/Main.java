package main;

import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;

import processing.core.PApplet;
import processing.core.PImage;

public class Main extends PApplet {

	Tiles t;
	PImage[] displayTileButtonImages;
	Integer[][] previousMapTileNum;
	boolean stateSaved = false;
	
	int xOffset;
	int yOffset;	
	int scale;
	
	int activeTile;
	int previousActiveTile;
		
	int frameCount;
	char lastKeyPressed;
		
	int gameState = 0;
	boolean newMap = false;
	boolean loadMap = false;
	
	String newFileName = "";	
	String loadFileName = "";
	
	public void settings() {
		size(1000, 1000);
		fullScreen();
	}
	
	public void setup() {		
		frameRate(600);		
		frameCount = 0;
	}
	
	public void setupMap(String fileName) {
		t = new Tiles(this, fileName);
		previousMapTileNum = copyMapTileNum(t.mapTileNum);
		stateSaved = false;
		
		setDefaultOffsets();
		
		activeTile = -1;
		previousActiveTile = -1;
		scale = 1;
		
		displayTileButtonImages = new PImage[t.tile.length];
		for (int i = 0; i < t.tile.length; i++) {
			displayTileButtonImages[i] = t.tile[i].copy();
			displayTileButtonImages[i].resize(t.tileSize * 2 * scale, t.tileSize * 2 * scale);
		}
		drawGrid();
		drawUI();
	}
	
	private void setDefaultOffsets() {
		xOffset = width / 2 - (t.cols * t.tileSize / 2);
		yOffset = height / 2 - (t.rows * t.tileSize / 2);
	}
	
	public void draw() {
		
		if (gameState == 0) {								
			
			noStroke();
			textAlign(CENTER);
			background(0xff9b816c);
			
			fill(0xFF84261f);
			rect(width / 2 - 100, height / 2 - 80, 200, 60, 10);			
			rect(width / 2 - 100, height / 2, 200, 60, 10);
			
			if (!newMap && !loadMap) {
				textSize(40);			
				fill(0);
				text("new map", width / 2, height / 2 - 40);
				text("load map", width / 2, height / 2 + 40);				
			}						

			if (newMap) {
				background(0xff9b816c);				
				rect(width / 2 - 100, height / 2 - 80, 200, 60, 10);
				textSize(40);
				text(newFileName, width / 2, height / 3);
				
				fill(0);
				text("enter filename space width space height", width / 2, height / 4);
				text("start", width / 2, height / 2 - 40);
			}
			else if (loadMap) {
				background(0xff9b816c);				
				rect(width / 2 - 100, height / 2 - 80, 200, 60, 10);
				textSize(40);
				text(loadFileName, width / 2, height / 3);
				
				fill(0);
				text("enter filename", width / 2, height / 4);
				text("start", width / 2, height / 2 - 40);
			}
			
			return;
		}

		else if (mousePressed) { // remember that mousepressed method causes methods below to wait while running
			int newActiveTile = -1; // and it clears the screen with each drawGrid();
			
			if (mouseX <= t.tileSize * 2 * 16 && mouseY <= t.tileSize * 2 * 2) {
				newActiveTile = changeActiveTile();
				if (activeTile != newActiveTile) {
					previousActiveTile = activeTile;
					activeTile = newActiveTile;
				}
			}

			else if (mouseX > xOffset && mouseY > yOffset && mouseX < xOffset + t.cols * 16 * scale && mouseY < yOffset +  t.rows * 16 * scale) {
				if (mouseButton == LEFT) {
					setTile(activeTile);
				}
				else {
					setTile(previousActiveTile);
				}
				drawGrid();
			}
		}
		stroke(5);
		noFill();
		
		// things to do with lower poll rate
		if (frameCount == 25) {
			textAlign(LEFT);
//			System.out.println(frameRate);
			
			//coords display
			fill(0xFF64666f);
			rect(width - 130, 0, 60, 20);
			
			fill(0);
			int tilesMouseX = mouseX - xOffset;
			int tilesMouseY = mouseY - yOffset;

			int[] tilesMouseIndex = {tilesMouseY / t.tileSize, tilesMouseX / t.tileSize};
			
			textSize(20);
			text(tilesMouseIndex[1] + ", " + tilesMouseIndex[0], width - 123, 16);	
			frameCount = 0;
			
			// cursor? id have to draw to a seperate layer on top ,would be useful for all UI 
			
			//rect(tilesMouseX + xOffset, tilesMouseY + yOffset, t.tileSize, t.tileSize);
		}
		
		frameCount++;
//		noLoop();				
	}
	
	public void drawGrid() { 
		
		drawTiles();
		
		stroke(0x31000000);
		strokeWeight(1);
		
		int tileAreaLeft = xOffset;
		int tileAreaRight = xOffset + t.cols * t.tileSize;
		int tileAreaTop = yOffset;
		int tileAreaBottom = yOffset + t.rows * t.tileSize;
		
		// Vertical grid lines
		for (int i = 0; i < t.cols; i++) {
		    int x = i * t.tileSize + xOffset;
		    line(x, tileAreaTop, x, tileAreaBottom);
		}

		// Horizontal grid lines
		for (int i = 0; i < t.rows; i++) {
		    int y = i * t.tileSize + yOffset;
		    line(tileAreaLeft, y, tileAreaRight, y);
		}
			
		noStroke();
		
	}
	
	public void drawTiles() {
		for (int i = 0; i < t.rows; i++) {
			for (int j = 0; j < t.cols; j++) {
				image(t.tileImages[i][j], j * t.tileSize + xOffset, i * t.tileSize + yOffset);
			}
		}
		// main tiles border
		noFill();
		stroke(0xffbdac97);
		strokeWeight(5);
		rect(xOffset - 3, yOffset - 3, t.tileSize * t.cols + 5, t.tileSize * t.rows + 5, 5);
	
		noStroke();
	}
	
	public void drawUI() {
		
		fill(20, 20, 20, 200); // instruction display
		rect(width - 130, 20, 130, height / 2 - 180);
		textSize(15);
		textAlign(LEFT);
		fill(255, 255, 255, 220);
		text(	"press ctrl then z \nto undo\n"
				+ "\npress enter then \na number\nto fill the map\n"
				+ "\npress - or = \nto add columns\n"
				+ "\npress _ or + \nto add rows\n"
				+ "\npress [ or ] \nto remove columns\n"
				+ "\npress { or } \nto remove rows", width - 125, 40);
		
		// tile buttons
		for (int i = 0, j = 0; i < t.tile.length; i++, j++) {			
			if (i * t.tileSize * 2 <= 480) {
				image(displayTileButtonImages[i], i * t.tileSize * 2, 0);
				j--;
			}
			else {
				image(displayTileButtonImages[i], j * t.tileSize * 2, 32);
			}			
		}
		
		// save button
		fill(255);		
		rect(width - 70, 0, 70, 20);
		
		fill(0);
		
		textAlign(CENTER);
		textSize(20);
		text("Save", width - 40, 15);
		
	}
	private void addRow(String side) {
		Integer[][] newMapTileNum = new Integer[t.rows + 1][t.cols];
		PImage[][] newTileImages = new PImage[t.rows + 1][t.cols];
		if (side == "top") {
			for (int row = 1; row <= t.rows; row++) {
				for (int col = 0; col < t.cols; col++) {
					newMapTileNum[row][col] = t.mapTileNum[row - 1][col];
					newTileImages[row][col] = t.tileImages[row - 1][col];
				}
			}
			for (int col = 0; col < newMapTileNum[0].length; col++) {
				newMapTileNum[0][col] = 0;
				newTileImages[0][col] = t.tile[0];
			}			
		}
		
		else if (side == "bottom") {
			for (int row = 0; row < t.rows; row++) {
				for (int col = 0; col < t.cols; col++) {
					newMapTileNum[row][col] = t.mapTileNum[row][col];
					newTileImages[row][col] = t.tileImages[row][col];
				}
			}
			for (int col = 0; col < newMapTileNum[0].length; col++) {
				newMapTileNum[t.rows][col] = 0;
				newTileImages[t.rows][col] = t.tile[0];
			}			
		}
		t.rows++;		
		t.mapTileNum = newMapTileNum;
		t.tileImages = newTileImages;
	}
	private void addCol(String side) {
		Integer[][] newMapTileNum = new Integer[t.rows][t.cols + 1];
		PImage[][] newTileImages = new PImage[t.rows][t.cols + 1];	
		if (side == "left") {
			for (int row = 0; row < t.rows; row++) {
				for (int col = 1; col <= t.cols; col++) {
					newMapTileNum[row][col] = t.mapTileNum[row][col - 1];
					newTileImages[row][col] = t.tileImages[row][col - 1];
				}
			}
			for (int row = 0; row < newMapTileNum.length; row++) {
				newMapTileNum[row][0] = 0;
				newTileImages[row][0] = t.tile[0];
			}
		}
		else if (side == "right") {
			for (int row = 0; row < t.rows; row++) {
				for (int col = 0; col < t.cols; col++) {
					newMapTileNum[row][col] = t.mapTileNum[row][col];
					newTileImages[row][col] = t.tileImages[row][col];
				}
			}
			for (int row = 0; row < newMapTileNum.length; row++) {
				newMapTileNum[row][t.cols] = 0;
				newTileImages[row][t.cols] = t.tile[0];
			}
		}
		t.cols++;		
		t.mapTileNum = newMapTileNum;
		t.tileImages = newTileImages;
	}
	
	private void removeRow(String side) {
		Integer[][] newMapTileNum = new Integer[t.rows - 1][t.cols];
		PImage[][] newTileImages = new PImage[t.rows - 1][t.cols];
		if (side == "top") {
			for (int row = 0; row < t.rows - 1; row++) {
				for (int col = 0; col < t.cols; col++) {
					newMapTileNum[row][col] = t.mapTileNum[row + 1][col];
					newTileImages[row][col] = t.tileImages[row + 1][col];
				}
			}		
		}
		
		else if (side == "bottom") {
			for (int row = 0; row < t.rows - 1; row++) {
				for (int col = 0; col < t.cols; col++) {
					newMapTileNum[row][col] = t.mapTileNum[row][col];
					newTileImages[row][col] = t.tileImages[row][col];
				}
			}			
		}
		t.rows--;		
		t.mapTileNum = newMapTileNum;
		t.tileImages = newTileImages;
	}
	private void removeCol(String side) {
		Integer[][] newMapTileNum = new Integer[t.rows][t.cols - 1];
		PImage[][] newTileImages = new PImage[t.rows][t.cols - 1];
		if (side == "left") {
			for (int row = 0; row < t.rows; row++) {
				for (int col = 0; col < t.cols - 1; col++) {
					newMapTileNum[row][col] = t.mapTileNum[row][col + 1];
					newTileImages[row][col] = t.tileImages[row][col + 1];
				}
			}		
		}
		
		else if (side == "right") {
			for (int row = 0; row < t.rows; row++) {
				for (int col = 0; col < t.cols - 1; col++) {
					newMapTileNum[row][col] = t.mapTileNum[row][col];
					newTileImages[row][col] = t.tileImages[row][col];
				}
			}			
		}
		t.cols--;		
		t.mapTileNum = newMapTileNum;
		t.tileImages = newTileImages;
	}
	
	public void mousePressed() {		
		if (gameState == 0) {
			int x = width / 2 - 100;
			int y = height / 2 - 80;			
			int rwidth = 200;
			int rheight = 60;	
			
			if (mouseX >= x && mouseX <= x + rwidth) {
				if (!newMap && !loadMap && mouseY >= y && mouseY <= y + rheight) { // new map
					newMap = true;
					return;
				}
				else if (newMap && mouseY >= y && mouseY <= y + rheight) { // start button
					loadCreatedMap();
				}
				if (!loadMap && mouseY >= height / 2 && mouseY <= height / 2 + rheight) { // new map
					loadMap = true;
					return;
				}
				else if (loadMap && mouseY >= y && mouseY <= y + rheight) { // start button					
					loadExistingMap();
				}
			}			
		}
		else {
			if (mouseX > xOffset && mouseY > yOffset && mouseX < width - xOffset && mouseY < height - yOffset) { // undo state save
				if (mouseButton == LEFT) {
					if (!stateSaved) {
						previousMapTileNum = copyMapTileNum(t.mapTileNum);
				        stateSaved = true;
					}
				}
			}
			if (mouseX >= width - 70 && mouseY <= 20) {				
				saveMap();
			}			
		}		
	}
	
	public void mouseReleased() {
		if (stateSaved) {
			stateSaved = false;
		}
	}

	public void keyPressed() {		
		if (gameState == 0) {
			if (newMap) {
				if (keyCode == BACKSPACE && newFileName.length() > 0) {
					newFileName = newFileName.substring(0, newFileName.length() - 1);
				}
				else if (keyCode >= 48 && keyCode <= 57 || keyCode >= 65 && keyCode <= 90 || keyCode == 32 || key == '_'){
					newFileName += key;
				}				
				if (keyCode == ENTER) {
					loadCreatedMap();
				}
			}
			else if (loadMap) {
				if (keyCode == BACKSPACE && loadFileName.length() > 0) {
					loadFileName = loadFileName.substring(0, loadFileName.length() - 1);
				}
				else if (keyCode >= 48 && keyCode <= 57 || keyCode >= 65 && keyCode <= 90 || keyCode == 32 || key == '_'){
					loadFileName += key;
				}
				if (keyCode == ENTER) {
					loadExistingMap();
				}
			}
			return;
		}
		switch (keyCode) {
        case UP:
        	yOffset -= 5;
        	break;
        case DOWN:
        	yOffset += 5;
        	break;
        case LEFT:
        	xOffset -= 5;
        	break;
        case RIGHT:
        	xOffset += 5;
        	break;
        case CONTROL:
        	lastKeyPressed = (char) keyCode;
        	break;
        case ENTER:
        	lastKeyPressed = (char) keyCode;
        	break;
        }
        
        switch (key) {
        case '-':
        	addCol("left");
        	t.loadImagesArray();
        	setDefaultOffsets();
        	System.out.println("cols: " + t.cols);
        	break;
        case '_':
        	addRow("top");
        	t.loadImagesArray();
        	setDefaultOffsets();
        	System.out.println("rows: "+ t.rows);
        	break;
        case '=':
        	addCol("right");
        	t.loadImagesArray();
    		setDefaultOffsets();
    		System.out.println("cols: " + t.cols);
        	break;
        case '+':
        	addRow("bottom");
        	t.loadImagesArray();
    		setDefaultOffsets();
    		System.out.println("rows: "+ t.rows);
        	break;
        case '[':
        	removeCol("left");
        	t.loadImagesArray();
        	setDefaultOffsets();
        	System.out.println("cols: " + t.cols);
        	break;
        case '{':
        	removeRow("top");
        	t.loadImagesArray();
        	setDefaultOffsets();
        	System.out.println("rows: " + t.rows);
        	break;
        case ']':
        	removeCol("right");
        	t.loadImagesArray();
        	setDefaultOffsets();
        	System.out.println("cols: " + t.cols);
        	break;
        case '}':
        	removeRow("bottom");
        	t.loadImagesArray();
        	setDefaultOffsets();
        	System.out.println("rows: " + t.rows);
        	break;
        case 'z':
        	if (lastKeyPressed == CONTROL) { // ctrl z!
            	t.setMapTileNum(previousMapTileNum);        	
            	lastKeyPressed = ' ';
            	System.out.println("undone.");
            }
        	break;
        }

		if (lastKeyPressed == ENTER && key != ENTER) {
			if (key >= '0' && key <= '9') {
				Integer[][] newMapTileNum = new Integer[t.rows][t.cols];
				
				for (int i = 0; i < t.rows; i++) {
					for (int j = 0; j < t.cols; j++) {
						newMapTileNum[i][j] = key - '0';
					}
				}
				t.setMapTileNum(newMapTileNum);
			}
			
			lastKeyPressed = ' ';
		}
		background(0xff9b816c);		
		drawGrid();
        drawUI();
	}
	
	public void keyReleased() {
    
	}
	
	public int changeActiveTile() {
		
		if (mouseY > t.tileSize * 2) {
			for (int i = 1; i <= t.tile.length; i++) {
				if (mouseX < t.tileSize * 2 * i) {					
					return i + 15;
				}
			}
			
		}
		else if (mouseY > 0){
			for (int i = 1; i <= t.tile.length; i++) {
				if (mouseX < t.tileSize * 2 * i) {
					return i - 1;
				}
			}
		}
		
		
		print("active tile is -1");
		return -1;
	}
	
	public void setTile(int tileNum) {
		
		Integer[][] newMapTileNum = t.mapTileNum;
		
		int tilesMouseY = mouseY - yOffset;
		int tilesMouseX = mouseX - xOffset;		
		
		if (tilesMouseX < 0 || tilesMouseY < 0 || tileNum < 0) {
			return;
		}
		
		int[] tilesMouseIndex = {Math.clamp(tilesMouseX / t.tileSize, 0, t.cols - 1), Math.clamp(tilesMouseY / t.tileSize, 0, t.rows - 1)};
		
		newMapTileNum[tilesMouseIndex[1]][tilesMouseIndex[0]] = tileNum;
		
		t.setMapTileNum(newMapTileNum);
		//image(t.tile[tileNum], tilesMouseIndex[0] * t.tileSize + xOffset, tilesMouseIndex[1] * t.tileSize + yOffset);
	}
	
	private void loadCreatedMap() {
		String[] newFileInfo = newFileName.split(" ");
		
		File file = new File("res/maps/" + newFileInfo[0] + ".txt");					
		if (file.exists() || newFileName == "") {
			System.out.println("invalid filename: " + newFileName);
			return;
		}					
		
		PrintWriter output = createWriter(file);
		
		for (int i = 0; i < Integer.parseInt(newFileInfo[2]); i++) {
			for (int j = 0; j < Integer.parseInt(newFileInfo[1]); j++) {							
				output.print("0 ");
			}
			output.println();
		}
		
		output.flush();
		output.close();

		background(0xff9b816c);
		setupMap(newFileInfo[0]);
		gameState = 1;
	}
	private void loadExistingMap() {
		File file = new File("res/maps/" + loadFileName + ".txt");					
		if (!file.exists() || loadFileName == "") {
			System.out.println("invalid filename: " + loadFileName);
			return;
		}

		background(0xff9b816c);
		setupMap(loadFileName);					
		gameState = 1;
	}
	
	public void saveMap() {
		
		StringBuilder sb = new StringBuilder();
	    for (Integer[] row : t.mapTileNum) {
	        for (int col : row) {
	            sb.append(col).append(" ");
	        }
	        sb.append("\n");
	    }
    
//	    for (int col = t.mapTileNum[0].length - 1; col >= 0; col--) { //                    rotates map anticlockwise!
//	        for (int row = 0; row < t.mapTileNum.length; row++) {
//	            sb.append(t.mapTileNum[row][col]).append(" ");
//	        }
//	        sb.append("\n");
//	    }
	    
	    String outputString = sb.toString();
	    
	    String fileName;
	    File file = null;		    

	    if (!newFileName.equals("")) {
	    	fileName = newFileName.split(" ")[0];
	    	file = new File("res/maps/" + fileName + ".txt");
	    }
	    else {
	    	fileName = loadFileName;
	    	file = new File("res/maps/" + fileName + ".txt");
	    	
//		    int count = 0; 										// to avoid overwriting when saving loaded map!
//		    fileName += count;
//		    
//		    while (file.exists()) {				    				    	
//		    	fileName = fileName.substring(0, fileName.length()) + count;
//		    	count++;
//		    	file = new File("res/maps/" + fileName + ".txt");				    	
//		    } 
	    }
	    
	    
	    PrintWriter writer;
		try {
			writer = new PrintWriter(file);
			writer.print(outputString);
	        writer.flush();
	        writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        
        println("File saved: " + fileName);
	}
	
	private Integer[][] copyMapTileNum(Integer[][] original) {
	    Integer[][] copy = new Integer[original.length][];
	    for (int i = 0; i < original.length; i++) {
	        copy[i] = original[i].clone(); // Deep copy each row
	    }
	    return copy;
	}
	
	public static void main(String[] args) {
		PApplet.main("main.Main");
	}

}

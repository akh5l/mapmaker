package main;

import processing.core.PApplet;
import processing.core.PImage;

public class Tiles {
	PApplet pApplet;
	PImage[] tile;
	Integer[][] maxMapTileNum;
	Integer[][] mapTileNum;
	PImage[][] tileImages;
	int cols;
	int rows;
	int tileSize;
	String filename;
	
	public Tiles(PApplet pApplet, String filename) {
		this.pApplet = pApplet;
		this.filename = filename;
		
		tileSize = 16;
		tile = new PImage[32];
		maxMapTileNum = new Integer[100][100];
		
				
		tile[0] = pApplet.loadImage("res/tiles/grass00.png");
		tile[1] = pApplet.loadImage("res/tiles/grass01.png");
		tile[2] = pApplet.loadImage("res/tiles/water00.png");
		tile[3] = pApplet.loadImage("res/tiles/water01.png");
		tile[4] = pApplet.loadImage("res/tiles/water02.png");
		tile[5] = pApplet.loadImage("res/tiles/water03.png");
		tile[6] = pApplet.loadImage("res/tiles/water04.png");
		tile[7] = pApplet.loadImage("res/tiles/water05.png");
		tile[8] = pApplet.loadImage("res/tiles/water06.png");
		tile[9] = pApplet.loadImage("res/tiles/water07.png");
		tile[10] = pApplet.loadImage("res/tiles/water08.png");
		tile[11] = pApplet.loadImage("res/tiles/water09.png");
		tile[12] = pApplet.loadImage("res/tiles/water10.png");
		tile[13] = pApplet.loadImage("res/tiles/water11.png");
		tile[14] = pApplet.loadImage("res/tiles/water12.png");
		tile[15] = pApplet.loadImage("res/tiles/water13.png");
		tile[16] = pApplet.loadImage("res/tiles/road00.png");
		tile[17] = pApplet.loadImage("res/tiles/road01.png");
		tile[18] = pApplet.loadImage("res/tiles/road02.png");
		tile[19] = pApplet.loadImage("res/tiles/road03.png");
		tile[20] = pApplet.loadImage("res/tiles/road04.png");
		tile[21] = pApplet.loadImage("res/tiles/road05.png");
		tile[22] = pApplet.loadImage("res/tiles/road06.png");
		tile[23] = pApplet.loadImage("res/tiles/road07.png");
		tile[24] = pApplet.loadImage("res/tiles/road08.png");
		tile[25] = pApplet.loadImage("res/tiles/road09.png");
		tile[26] = pApplet.loadImage("res/tiles/road10.png");
		tile[27] = pApplet.loadImage("res/tiles/road11.png");
		tile[28] = pApplet.loadImage("res/tiles/road12.png");
		tile[29] = pApplet.loadImage("res/tiles/earth.png");
		tile[30] = pApplet.loadImage("res/tiles/wall.png");
		tile[31] = pApplet.loadImage("res/tiles/tree.png");
		
		loadMap();
		
		tileImages = new PImage[rows][cols];
		loadImagesArray();
		
	}
	
	public void loadMap() {
		String[] map = pApplet.loadStrings("res/maps/" + filename + ".txt");
		
		for (int i = 0; i < map.length; i++) {
			String[] numbers = map[i].split(" ");
			
			for (int j = 0; j < numbers.length; j++) {
				
				int num = Integer.parseInt(numbers[j]);
				maxMapTileNum[i][j] = num;
			}
		}
		
		rows = 0;
		cols = 0;
		
		String[] numbers = map[0].split(" ");
		
		for (int i = 0; i < numbers.length; i++) {
			if (numbers[i] != null) {
				cols++;
				
			}
		}		
		
		for (int i = 0; i < map.length; i++) {
			if (map[i] != null) {
				rows++;
			}
		}
		
		mapTileNum = new Integer[rows][cols];
		
		for (int i = 0; i < rows; i++) {						
			for (int j = 0; j < cols; j++) {
				mapTileNum[i][j] = maxMapTileNum[i][j];
			}
		}
		
		System.out.printf("cols: %d\nrows: %d", cols, rows);
		System.out.println();
	}
	
	public void loadImagesArray() {
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {				
				tileImages[i][j] = tile[mapTileNum[i][j]];
			}
		}
	}
	
	public void setMapTileNum(Integer[][] newMapTileNum) {
		mapTileNum = newMapTileNum;
		loadImagesArray();
	}

}

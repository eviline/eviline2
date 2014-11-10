package org.eviline.core;

public enum KickTable {
	NO_KICK(new int[][]{{0,0}}),

	UP_RIGHT(new int[][]{  {0,0}, {-1, 0}, {-1, 1}, { 0, 2}, {-1, 2}}),
	RIGHT_DOWN(new int[][]{{0,0}, { 1, 0}, { 1, 1}, { 0,-2}, { 1,-2}}),
	DOWN_LEFT(new int[][]{ {0,0}, { 1, 0}, { 1,-1}, { 0, 2}, { 1, 2}}),
	LEFT_UP(new int[][]{   {0,0}, {-1, 0}, {-1, 1}, { 0,-2}, {-1,-2}}),

	UP_LEFT(new int[][]{   {0,0}, { 1, 0}, { 1,-1}, { 0, 2}, { 1, 2}}),
	RIGHT_UP(new int[][]{  {0,0}, { 1, 0}, { 1, 1}, { 0,-2}, { 1,-2}}),
	DOWN_RIGHT(new int[][]{{0,0}, {-1, 0}, {-1,-1}, { 0, 2}, {-1, 2}}),
	LEFT_DOWN(new int[][]{ {0,0}, {-1, 0}, {-1, 1}, { 0,-2}, {-1,-2}}),

	IUP_RIGHT(new int[][]{{0,0}, {-2,0}, {1,0}, {-2,1}, {1,-2}}),
	IRIGHT_DOWN(new int[][]{{0,0}, {-1,0}, {2,0}, {-1,-2}, {2,1}}),
	IDOWN_LEFT(new int[][]{{0,0}, {2,0}, {-1,0}, {2,-1}, {-1,2}}),
	ILEFT_UP(new int[][]{{0,0}, {1,0}, {-2,0}, {1,2}, {-2,-1}}),

	IUP_LEFT(new int[][]{{0,0}, {-1,0}, {2,0}, {-1,-2}, {2,1}}),
	IRIGHT_UP(new int[][]{{0,0}, {2,0}, {-1,0}, {2,-1}, {-1,2}}),
	IDOWN_RIGHT(new int[][]{{0,0}, {1,0}, {-2,0}, {1,2}, {-2,-1}}),
	ILEFT_DOWN(new int[][]{{0,0}, {-2,0}, {1,0}, {-2,1}, {1,-2}}),
	;

	private int[][] table;

	private KickTable(int[][] table) {
		this.table = table;
		for(int[] test : table) {
			test[1] = test[1];
		}
	}
	
	public int[][] table() { return table; }
	
}

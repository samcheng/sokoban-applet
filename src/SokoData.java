import java.io.*;

public class SokoData {
    private int height, width;
    private int[][] grid;
    private String name;
    private int playerX, playerY;
    private int emptyHoles;
    private STile[][] listeners;
    private SokoEndListener endListener;
		private SokoDataListener dataListener;
    private int[] moves;
    private boolean[] pushes;
		private int numMoves, backs;
		private int numPushes;
		private Sokoban world;

		private int length, shortestLength;
		private boolean[][] beenThere;
		private int[] path, shortestPath;

    public SokoData(BufferedReader in, Sokoban world) {
	String line = null;
	char s;
	
	this.world = world;

	moves = new int[10000];
	pushes = new boolean[10000];
	backs = 0;
	numMoves = 0;
	emptyHoles = 0;
	
	grid = new int[Sokoban.MAX_WIDTH][Sokoban.MAX_HEIGHT];
	listeners = new STile[Sokoban.MAX_WIDTH][Sokoban.MAX_HEIGHT];
	
	for(int i = 0; i < Sokoban.MAX_HEIGHT; i++) {
	    try{
		line = in.readLine();
	    } catch (IOException e) {line = null;}
	    for(int j = 0; j < Sokoban.MAX_WIDTH; j++) {
		if(line == null || j >= line.length())
		    s = ' ';
		else
		    s = line.charAt(j);
		switch(s) {
		case ' ':
		    grid[j][i] = Sokoban.SPACE;
		    break;
		case '#':
		    grid[j][i] = Sokoban.WALL;
		    break;
		case '.':
		    grid[j][i] = Sokoban.HOLE;
		    emptyHoles++;
		    break;
		case '$':
		    grid[j][i] = Sokoban.BOX;
		    break;
		case '*':
		    grid[j][i] = Sokoban.HOLE_FILLED;
		    break;
		case '@':
		    grid[j][i] = Sokoban.PLAYER;
		    playerX = j;
		    playerY = i;
		    break;
		case '+':
		    grid[j][i] = Sokoban.HOLE_PLAYER;
		    playerX = j;
		    playerY = i;
		    break;
		default:
		}
		
		
	    }
	}
    }
    
    public int level() {
				return world.getLevel();
		}

		public void move(int x, int y) {
				path = new int[100];
				shortestPath = new int[100];
				beenThere = new boolean[Sokoban.MAX_WIDTH][Sokoban.MAX_HEIGHT];

				int len = FindPath(playerX, playerY, 99, x, y);
				for(int i = 0; i < len; i++) {
						move(shortestPath[i]);
						System.out.println("move " + shortestPath[i]);
						world.sleep(50);
				}
	 
		}

		private int FindPath(int workingX, int workingY, 
												 int pathSize, int destX, int destY) {
				length = 0;
				shortestLength = 99;
				WorkPath(workingX, workingY, pathSize, destX, destY);
				if(shortestLength < 99)
						return shortestLength;
				else
						return 0;
		}
		
		private void WorkPath(int workingX, int workingY, 
														 int pathSize, int destX, int destY) {
				int dir, newX = 0, newY = 0;
				if(workingX == destX && workingY == destY) {
						if (length < shortestLength) {
								shortestLength = length;
								for(int i = 0; i < length; i++)
										shortestPath[i] = path[i];
						}
						return;
				}
				if(length > shortestLength)
						return;

				if(beenThere[workingX][workingY] || length == pathSize)
						return;
				for(dir = 0; dir < 4; dir++) {
						switch(dir) {
						case Sokoban.NORTH: 
								newX = workingX;
								newY = workingY - 1;
								break;
						case Sokoban.SOUTH:
								newX = workingX;
								newY = workingY + 1;
								break;
						case Sokoban.EAST:
								newY = workingY;
								newX = workingX + 1;
								break;
						case Sokoban.WEST:
								newY = workingY;
								newX = workingX - 1;
								break;
						default:
						}
						if(!beenThere[newX][newY] && 
							 (grid[newX][newY] == Sokoban.EMPTY || 
							 grid[newX][newY] == Sokoban.HOLE)) {
								path[length++] = dir;
								beenThere[workingX][workingY] = true;
								WorkPath(newX, newY, pathSize, destX, destY);
								length--;
								beenThere[workingX][workingY] = false;
						}
				}
		}
								

		public void move(int d) {
	int newX, newY, twoX, twoY;
	boolean move;
	boolean push = false;


	switch(d) {
	case Sokoban.NORTH: 
	    twoX = newX = playerX;
	    newY = playerY - 1;
	    twoY = playerY - 2;
	    break;
	case Sokoban.SOUTH:
	    twoX = newX = playerX;
	    newY = playerY + 1;
	    twoY = playerY + 2;
	    break;
	case Sokoban.EAST:
	    twoY = newY = playerY;
	    newX = playerX + 1;
	    twoX = playerX + 2;
	    break;
	case Sokoban.WEST:
	    twoY = newY = playerY;
	    newX = playerX - 1;
	    twoX = playerX - 2;
	    break;
	default:
	    newX = twoX = playerX;
	    newY = twoY = playerY;
	}
	switch(grid[newX][newY]) {
	case Sokoban.EMPTY:
	    grid[newX][newY] = Sokoban.PLAYER;
	    move = true;
	    break;
	case Sokoban.WALL:
	    move = false;
	    break;
	case Sokoban.HOLE:
	    grid[newX][newY] = Sokoban.HOLE_PLAYER;
	    move = true;
	    break;
	case Sokoban.BOX:
			push = true;
	    switch(grid[twoX][twoY]) {
	    case Sokoban.EMPTY:
					grid[twoX][twoY] = Sokoban.BOX;
					grid[newX][newY] = Sokoban.PLAYER;
					move = true;
					break;
	    case Sokoban.HOLE:
					grid[twoX][twoY] = Sokoban.HOLE_FILLED;
					grid[newX][newY] = Sokoban.PLAYER;
					move = true;
					emptyHoles--;
					if(emptyHoles == 0)
							gameWon();
					break;
	    default:
					push = false;
					move = false;
	    }
	    break;
	case Sokoban.HOLE_FILLED:
			push = true;
	    switch(grid[twoX][twoY]) {
	    case Sokoban.EMPTY:
		grid[twoX][twoY] = Sokoban.BOX;
		grid[newX][newY] = Sokoban.HOLE_PLAYER;
		move = true;
		emptyHoles++;
		break;
	    case Sokoban.HOLE:
		grid[twoX][twoY] = Sokoban.HOLE_FILLED;
		grid[newX][newY] = Sokoban.HOLE_PLAYER;
		move = true;
		break;
	    default:
		push = false;
		move = false;
	    }
	    break;
	default:
	    move = false;
	}
	if(move == true) {
	    switch(grid[playerX][playerY]) {
	    case Sokoban.PLAYER:
		grid[playerX][playerY] = Sokoban.EMPTY;
		break;
	    case Sokoban.HOLE_PLAYER:
		grid[playerX][playerY] = Sokoban.HOLE;
		break;
	    default:
	    }
	    notify(newX, newY);
	    notify(playerX, playerY);
			if(push)
					notify(twoX, twoY);
	    playerX = newX;
	    playerY = newY;
	    if(push)
					numPushes++;
	    pushes[numMoves] = push;
	    moves[numMoves++] = d;
	    dataListener.changed(this);
	}
    }
    
    public void back() {
	int move, backX, backY, forwardX, forwardY;
	
	if(numMoves <= 0)
	    return;
	
	backs++;
	
	boolean push = pushes[--numMoves];
	if(push)
	    numPushes--;
	
	move = moves[numMoves];
	
	switch(move) {
	case Sokoban.NORTH: 
	    backX = forwardX = playerX;
	    backY = playerY - 1;
	    forwardY = playerY + 1;
	    break;
	case Sokoban.SOUTH:
	    backX = forwardX = playerX;
	    backY = playerY + 1;
	    forwardY = playerY - 1;
	    break;
	case Sokoban.EAST:
	    backY = forwardY = playerY;
	    backX = playerX + 1;
	    forwardX = playerX - 1;
	    break;
	case Sokoban.WEST:
	    backY = forwardY = playerY;
	    backX = playerX - 1;
	    forwardX = playerX + 1;
	    break;
	default:
	    backX = forwardX = playerX;
	    backY = forwardY = playerY;
	}
	
	if(grid[backX][backY] == Sokoban.BOX && push) 
	    grid[backX][backY] = Sokoban.EMPTY;
	else if (grid[backX][backY] == Sokoban.HOLE_FILLED && push) { 
	    grid[backX][backY] = Sokoban.HOLE;
	    emptyHoles++;
	}
	
	if(push) {
	    if(grid[playerX][playerY] == Sokoban.HOLE_PLAYER) {
		grid[playerX][playerY] = Sokoban.HOLE_FILLED;
		emptyHoles--;
	    }
	    else 
		grid[playerX][playerY] = Sokoban.BOX;
	} else {
	    if(grid[playerX][playerY] == Sokoban.HOLE_PLAYER) 
		grid[playerX][playerY] = Sokoban.HOLE;
	    else 
		grid[playerX][playerY] = Sokoban.EMPTY;
	}
	
	if(grid[forwardX][forwardY] == Sokoban.HOLE)
	    grid[forwardX][forwardY] = Sokoban.HOLE_PLAYER;
	else
	    grid[forwardX][forwardY] = Sokoban.PLAYER;
	
	playerX = forwardX;
	playerY = forwardY;
	
	notifyE(backX, backY);
	notifyE(forwardX, forwardY);
	notifyE(playerX, playerY);
	dataListener.changed(this);	    

    }		
    
    public int totalMoves() {
	return numMoves;
    }
    
    public int totalPushes() {
	return numPushes;
    }

    public int getXY(int x, int y) {
	return grid[x][y];
    }

    public void gameWon() {
				System.out.println("You Won!");
				endListener.ended(this);
		}
    
    public void addListener(STile l, int x, int y) {
	listeners[x][y] = l;
    }
    
    private void notify(int x, int y) {
				System.out.println("Notified: " + x + "," + y);
				(listeners[x][y]).repaintNow();
    }

		private void notifyE(int x, int y) {
				(listeners[x][y]).repaint();
		}

		public void setEndListener(SokoEndListener endL) {
				endListener = endL;
		}

		public void setDataListener(SokoDataListener dataL) {
				dataListener = dataL;
		}


}

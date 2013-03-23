import java.awt.*;
import java.applet.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

public class Sokoban extends Applet implements KeyListener, Runnable{
    private Image[] images;
    private int level;
    private SokoData data;
    private Label moveLabel;
    private boolean next;
		
		private Thread sokoThread;

		private static int numLevels = 5;
    
    public final static int SPACE = 0;
		public final static int EMPTY = 0;
		public final static int HOLE = 1;
		public final static int PLAYER = 2;
		public final static int WALL = 3;
		public final static int BOX = 4;
		public final static int HOLE_FILLED = 5;
		public final static int HOLE_PLAYER = 6;
		
		public final static int NORTH = 0;
		public final static int EAST = 1;
		public final static int SOUTH = 2;
		public final static int WEST = 3;
		
		public final static int TILESIZE = 25;
		public final static int MAX_WIDTH = 20;
		public final static int MAX_HEIGHT = 17;

    public void init() {
	images = new Image[7];
	MediaTracker tracker = new MediaTracker(this);
	
				//load images
	images[0] = getImage(getCodeBase(), "space.jpg");
	tracker.addImage(images[0], 0);
	images[1] = getImage(getCodeBase(),"hole.jpg");
	tracker.addImage(images[1], 0);
	images[2] = getImage(getCodeBase(),"player.jpg");
	tracker.addImage(images[2], 0);
	images[3] = getImage(getCodeBase(),"wall.jpg");
	tracker.addImage(images[3], 0);
	images[4] = getImage(getCodeBase(),"box.jpg");
	tracker.addImage(images[4], 0);
	images[5] = getImage(getCodeBase(),"hole_filled.jpg");
	tracker.addImage(images[5], 0);
	images[6] = getImage(getCodeBase(),"hole_player.jpg");
	tracker.addImage(images[6], 0);
	try {	
	    tracker.waitForAll();
	} catch(InterruptedException e) {e.printStackTrace();}
	
    }

		public void keyTyped(KeyEvent e) {}
		public void keyPressed(KeyEvent e) {
				if(data != null)
						processKeyStroke(e);
		}
		public void keyReleased(KeyEvent e){}
				
		public void start() {
				
				if(sokoThread==null) {
						
						sokoThread = new Thread(this);
						
						sokoThread.start();
						
				}
				
		}
		
    public void run() {
				level = 1;
				play();
    }				
    
    public void replay() {
				removeAll();
				if(level != 0 && next)
						doHighScores();
				if(level == 90)
						level = 0;
				data = null;
				level++;
				// if(!next)
				play(); //otherwise Scorebox will call it
    }

    public void doHighScores() {
	//URL url = null, sco = null;
				//try{url = new URL(getCodeBase(), "highscores/scores."+level);
				//sco = new URL("http", getDocumentBase().getHost(), 8080, "/servlet/SokoServ");
				//System.out.println(sco);
	//}
	//		catch(MalformedURLException e) {System.out.println(e);}
	//		System.out.println(url);
				//SokoScoreBox popUp = new SokoScoreBox(this, data, url, sco, level);
    }
    
    private void processKeyStroke(KeyEvent e) {
	switch(e.getKeyCode()) {
	case KeyEvent.VK_DOWN:
	    data.move(SOUTH);
	    break;
	case KeyEvent.VK_RIGHT:
	    data.move(EAST);
	    break;
	case KeyEvent.VK_UP:
	    data.move(NORTH);
	    break;
	case KeyEvent.VK_LEFT:
	    data.move(WEST);
	    break;
	case KeyEvent.VK_BACK_SPACE:
	    data.back();
	default:
	    switch(e.getKeyChar()) {
	    case 's':
	    case 'S':
		data.move(SOUTH);
		break;
	    case 'd':
	    case 'D':
		data.move(EAST);
		break;
	    case 'w':
	    case 'W':
		data.move(NORTH);
		break;
	    case 'a':
	    case 'A':
		data.move(WEST);
		break;
	    case 'b':
	    case 'B':
					data.back();
					break;
			case 'n':
			case 'N':
					next = false;
					replay();
					break;
			case 'r':
			case 'R':
					next = false;
					level--;
					replay();
					break;
	    default:
	    }
	}
	
    }
    
    public void play() {
	Panel playPanel = new Panel(null);
	BufferedReader in = null;
	playPanel.addKeyListener(this);
	try {
	    URL url = new URL(getCodeBase(), "levels/screen."+level);
	    //URL sco = new URL("http://www.sparkmania.com:8080/servlet/SokoServ?Level="+level);
	    //	getAppletContext().showDocument(sco, "S");
			in = new BufferedReader(new InputStreamReader(url.openStream()));
	    System.out.println(url);
	} catch (FileNotFoundException e) {System.out.println(e);}
	catch (MalformedURLException e)
	    {System.out.println("URLException:" + e);}
	catch(IOException e) {System.out.println(e);}
	
	data = new SokoData(in, this);
	data.setEndListener(new SokoEndListener(this));

	for(int y = 0; y < MAX_HEIGHT; y++)
	    for(int x = 0; x < MAX_WIDTH; x++)
		playPanel.add(new STile(data, x, y, images, this));
	
	playPanel.setSize(MAX_WIDTH * TILESIZE, MAX_HEIGHT * TILESIZE);
	playPanel.setBackground(Color.blue);
	add(playPanel);
	playPanel.setVisible(true);
	
	moveLabel = new Label("Level: " + level + 
												" - Number of moves: 0 - Number of Pushes: 0      ");
	
	data.setDataListener(new SokoDataListener(moveLabel));
	
	add(moveLabel);

	validate();
	setVisible(true);
	playPanel.requestFocus();
    } 

		public void won() {
			next = true;
			replay();
		}

		public int getLevel() {
				return level;
		}

		public void sleep(int milliSeconds) {
				try{ sokoThread.sleep(milliSeconds); }
				catch(InterruptedException e) {}
		}
						
}

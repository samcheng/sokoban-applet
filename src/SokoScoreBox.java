import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;

public class SokoScoreBox extends Frame {
    private String[] highs;
    private int numMoves, numPushes, level;
    private String quote = null;
    private TextArea quoteArea;
    private TextField nameField;
		protected Sokoban world;
		private int bestMoves, bestPushes;
	private URL file, server;	

    public SokoScoreBox(Sokoban world, SokoData data, URL _file, URL _server, int level) {
				super("congratulations!");
				numMoves = data.totalMoves();
				this.world = world;
				this.level = level;
				numPushes = data.totalPushes();
				highs = new String[1000];
				BufferedReader in = null;
				file = _file;
				server = _server;

	try{
	in = new BufferedReader(new InputStreamReader(file.openStream()));
	}
	catch (FileNotFoundException e) {System.out.println(e);}
	catch (MalformedURLException e)
	    {System.out.println("URLException:" + e);}
	catch(IOException e) {System.out.println(e);}
	System.out.println(in);

	String line = null;
	bestPushes = Integer.MAX_VALUE;
	bestMoves = Integer.MAX_VALUE;
	try{
			quote = in.readLine();
			
			line = in.readLine();
			if(line != null) {
					bestMoves = Integer.decode(in.readLine()).intValue();
					bestPushes = Integer.decode(in.readLine()).intValue();
			}
	} catch (IOException e) {System.out.println(e);}
	
	System.out.println(bestPushes + "  " + bestMoves);
	Panel p, n, e;
	add(p = new Panel(new BorderLayout()));
	p.add(n = new Panel(new BorderLayout()), BorderLayout.NORTH);
	n.add(new Label("Congratulations!  You beat level " + level), BorderLayout.NORTH);
	n.add(new Label("With " + numPushes + " pushes and " + numMoves + " moves."), 
				BorderLayout.CENTER);
	n.add(new Label("If you were first you can change the quote for the level"), 
				BorderLayout.SOUTH);
	p.add(quoteArea = new TextArea(quote), BorderLayout.CENTER);  
	
	
	p.add(e = new Panel(new BorderLayout()), BorderLayout.EAST);
	e.add(new Label("Please enter your name:"), BorderLayout.NORTH);
	e.add(nameField = new TextField(), BorderLayout.CENTER);

	
	Button b;
	p.add(b = new Button("OK"), BorderLayout.SOUTH);
	b.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    writeFile();
		    dispose();
				play();
		}
	    });
	validate();
	pack();
	setVisible(true);
	if(numPushes > bestPushes || (numPushes == bestPushes && 
		 numMoves >= bestMoves)) 
			quoteArea.setEditable(false);
    }

		private void play() {world.play();}

		private void writeFile() {
				try{		
						if(nameField.getText().equals(""))
								nameField.setText("Anonymous");
						System.out.println("Wrote Out");
						URL url = new URL("http://128.12.53.21:8080/servlet/SokoServ");
						URLConnection urlCon = (URLConnection) server.openConnection(); 
					
						urlCon.setDoOutput(true);
						urlCon.setDoInput(true); 
				urlCon.setRequestProperty("Content-type", "application/x-www-form-urlencoded");

						urlCon.setUseCaches (false);
						urlCon.setDefaultUseCaches (false);
				System.out.println("Connecting to " + server);
	//urlCon.setRequestMethod("POST");
				ObjectOutputStream out = new ObjectOutputStream(urlCon.getOutputStream());
	out.writeObject(new SokoBean(level, numMoves, numPushes, quoteArea.getText(), nameField.getText()));
	urlCon.getInputStream();
				}catch(Exception e){ quoteArea.setText(e + "!"); } 
		}
}
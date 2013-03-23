import java.awt.*;
import java.awt.event.*;
import java.applet.*;
public class STile extends Component {
		private int x, y;
		private SokoData data;
		private Image[] images;
		private Thread animator;

    public STile(SokoData _data, int _x, int _y, 
								 Image _images[], Sokoban owner) {
	super();
	x = _x;
	y = _y;
	data = _data;
	images = _images;
	data.addListener(this, x, y); 
	setBounds(x * Sokoban.TILESIZE, y * Sokoban.TILESIZE, 
		  Sokoban.TILESIZE, Sokoban.TILESIZE);
	setVisible(true);
	addKeyListener(owner);
	addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
					if(data != null)
							data.move(x, y);
			}
	});

	requestFocus();
 
   
		}
		
		public void repaintNow() {
				paint(getGraphics());
		}

    public void paint(Graphics g) {
			 if(data != null && g != null)
					 g.drawImage(images[data.getXY(x, y)], 0,
											 0, Sokoban.TILESIZE, Sokoban.TILESIZE, 
											 Color.black, this);
		}

		public void update(Graphics g) {
				paint(g);
		}
}

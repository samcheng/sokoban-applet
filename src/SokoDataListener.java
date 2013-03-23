import java.awt.*;

public class SokoDataListener {
		private Label listener;
		
		public SokoDataListener(Label _listener) {
				listener = _listener;
		}

    public void changed(SokoData ended) {
	listener.setText("Level: " + ended.level() + " - " 
									 + "Number of moves: " + 
									 ended.totalMoves() + " - " +
									 "Number of pushes: " + 
									 ended.totalPushes());
    }
}

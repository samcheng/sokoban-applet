
public class SokoEndListener {
		private Sokoban listener;
		
		public SokoEndListener(Sokoban _listener) {
				listener = _listener;
		}

		public void ended(SokoData ended) {
		    listener.won();
		}
}

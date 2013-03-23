import java.io.*;
public class SokoBean implements Serializable {
		public int level,moves, pushes;
		public String quote, name;

		public SokoBean(int l, int m, int p, String q, String n) {
				level = l;
				moves = m;
				pushes = p;
				quote = q;
				name = n;
		}
}
				

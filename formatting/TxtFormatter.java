package formatting;

import java.io.*;

import exceptions.OutOfBounds;
import music.*;

public class TxtFormatter extends Formatter {

	public TxtFormatter(String s, Composition c) {
		super(s, c);
	}

	@Override
	public void exportFormat() {
	    try {
	    	BufferedWriter writer = new BufferedWriter(new FileWriter(directory));
			writer.write(toString());
			writer.close();
		} catch (IOException e) {}
	}
	
	public String toString() {
		StringBuilder sb=new StringBuilder();
		for (int i=0; i<composition.getSize(); i++) {
			MusicSymbol m=null;
			try {
				m = composition.getSymbol(i);
			} catch (OutOfBounds e) {continue;}
			Duration d=m.symDuration();
			if (m instanceof Pause) {
				if (d.equalDur(new Duration(1,4))) sb.append("|");
				else sb.append(" ");
			}
			else if (m instanceof Note) {
				if (d.equalDur(new Duration(1,4))) sb.append(""+composition.reversedMap.get(m.toString()));
				else {
					sb.append("["+composition.reversedMap.get((m.toString().toUpperCase()))+" ");
					while (++i<composition.getSize()) {
						MusicSymbol ms=null;
						try {
							ms = composition.getSymbol(i);
						} catch (OutOfBounds e) {break;}
						Duration dur=ms.symDuration();
						if (dur.equalDur(new Duration(1,8)) && ms instanceof Note) sb.append(composition.reversedMap.get(ms.toString().toUpperCase())+" ");
						else {
							i--;
							break;
						}
					}
					sb.append("]");
				}
			}
			else if (m instanceof Chord) {
				sb.append("[");
				Chord c=(Chord)m;
				Character chr;
				for (int j=0; j<c.size(); j++) {
					try {
						chr = composition.reversedMap.get(c.getNote(j).toString());
					} catch (OutOfBounds e) {continue;}
					sb.append(""+chr);
				}
				sb.append("]");
			}
		}
		return sb.toString();
	}

}

package music;

import java.util.ArrayList;
import exceptions.*;

public class Chord extends MusicSymbol {
	private ArrayList<Note> notes;
	
	public Chord() {
		super(new Duration(1,4), 1);
		notes=new ArrayList<Note>();
	}
	
	public void add(Note n) {
		for (int i=0; i<notes.size(); i++) 
			if (notes.get(i).equalNote(n)) return;
		n.setDuration(new Duration(1,4));
		notes.add(n);
	}
	
	public void add(Chord c) {
		for (int i=0; i<c.notes.size(); i++)
			add(c.notes.get(i));
	}
	
	public void add(MusicSymbol m) {
		if (m instanceof Chord) add((Chord)m);
		else if (m instanceof Note) add((Note)m);
	}
	
	public int size() {
		return notes.size();
	}
	
	public void clear() {
		notes.clear();
	}
	
	public boolean equalChord(Chord c) {
		int found=0;
		if (c.size()!=notes.size()) return false;
		for (int i=0; i<c.size(); i++) {
			for (int j=0; j<size(); j++) 
				if (c.notes.get(i).equalNote(notes.get(j))) {
					found++; break;
				}
		}
		if (found==notes.size()) return true;
		return false;
	}
	
	public Note getNote(int i) throws OutOfBounds {
		if (i<0 || i>=notes.size()) throw new OutOfBounds();
		return notes.get(i);
	}

	@Override
	public String toString() {
		if (notes.size()==1) return notes.get(0).toString();
		StringBuffer sb=new StringBuffer("[");
		for (int i=0; i<notes.size(); i++) {
			if (i!=0) sb.append(" ");
			sb.append(notes.get(i).toString());
		}
		return sb.append("]").toString();
	}
}
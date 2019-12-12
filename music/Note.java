package music;

public class Note extends MusicSymbol {
	private boolean sharp;
	private char height;
	
	public Note(String s, Duration d) {
		super(d);
		height=s.charAt(0);
		if (s.charAt(1)=='#') { sharp = true; octave=s.charAt(2)-48; }
		else { sharp =false; octave=s.charAt(1)-48;}
	}
	public String MIDIFormat() {
		return ""+height+(sharp? "#" : "") + octave;
	}
	
	public void setDuration(Duration duration) {
		d=duration;
	}
	
	public boolean equalNote(Note n) {
		return n.d.equalDur(d) && n.sharp==sharp && n.height==height && n.octave==octave;
	}
	
	public String toString() {
		if (d.equalDur(new Duration(1, 4))) 
			return (""+height).toUpperCase() + (sharp ? "#" : "") + octave; 
		else return (""+height).toLowerCase() + (sharp ? "#" : "") + octave; 
	}	
}
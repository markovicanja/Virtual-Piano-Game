package music;

public abstract class MusicSymbol {
	protected Duration d;
	protected int octave;
	
	public MusicSymbol(Duration dd, int o) { d=dd; octave=o;}
	public MusicSymbol() {d= new Duration(1,4); octave=3;}
	public MusicSymbol(Duration dd) {d=dd; octave =3;}
	public Duration symDuration() {return d;}
	public int getOctave() { return octave; }
	
	public abstract String toString(); 
	
}
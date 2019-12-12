package music;

public class Pause extends MusicSymbol {
	
	public Pause(Duration d){
		super(d,0);
	}
	public String toString() {
		if (d.equalDur(new Duration(1,4))) return "_"; 
		else return "-";
	}
}

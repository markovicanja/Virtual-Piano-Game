package formatting;
import music.Composition;

public abstract class Formatter {
	protected String directory;
	protected Composition composition;
	
	public Formatter(String s, Composition c) {
		directory=s; composition=c;
	}
	
	public abstract void exportFormat();
}
package music;

public class Duration {
	private int numerator, denominator;
	
	public Duration(int num, int den) {
		numerator=num; denominator=den;
	}
	
	public int numer() { return numerator; }
	public int denom() { return denominator; }
	
	public boolean equalDur(Duration d) {
		return numerator*1./denominator == d.numerator*1./d.denominator;
	}
//	public String toString() {
//		return ""+denominator;
//	}
}
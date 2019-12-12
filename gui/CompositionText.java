package gui;

import java.awt.*;
import exceptions.*;
import music.*;

public class CompositionText extends Canvas {
	private Composition comp;
	private static final int SymHeight=20, FourthWidth=40, EighthWidth=20;
	private static final Color FourthNote=new Color(255,0,0), EighthNote=new Color(102, 255, 102), FourthPause=new Color(195,0,0), EighthPause=new Color(0, 122, 8);
	private int filled=0;
	private int dx=0;
	private int printStr=0; //0-slova, 1-opis
	
	public CompositionText(Composition c) {
		comp=c; 
	}
	
	public void printString(int i) {
		printStr=i;
	}
	
	public void paint(Graphics g) {
		filled=0;
		int numOfEights=0;
		int maxChord=1;
		int width=0; Color color=Color.BLACK;
		if (comp==null) return;
		try {
			MusicSymbol sym;
			for (int i=0; i<comp.getSize(); i++) {
				sym=comp.getSymbol(i);
				if (sym.symDuration().equalDur(new Duration(1,4))) {
					width=FourthWidth; 
					if (sym instanceof Pause) color=FourthPause;
					else color=FourthNote;
				}
				else {
					width=EighthWidth;
					if (sym instanceof Pause) color=EighthPause;
					else color=EighthNote;
					numOfEights++;
				}
				
				if (sym instanceof Pause) {
					g.setColor(color);
					g.fillRect(filled+dx, getHeight()/2-10, width, SymHeight);
					filled+=width;
				}
				else if (sym instanceof Note) {
					Note n=(Note) sym;
					g.setColor(color);
					g.fillRect(filled+dx, getHeight()/2-10, width, SymHeight);
					g.setColor(Color.black);
					String s=n.toString();
					if (printStr==0) s=comp.reversedMap.get(s.toUpperCase()).toString();
					g.drawString(s, filled+dx+width/2-5, getHeight()/2+5);
					filled+=width;
				}
				else if (sym instanceof Chord) {
					Chord c=(Chord) sym;
					if (maxChord<c.size()) maxChord=c.size();
					int dy=c.size()/2;
					for (int j=0; j<c.size(); j++) {
						g.drawRect(filled+dx, getHeight()/2-dy*20, width, 20);
						g.setColor(color);
						g.fillRect(filled+dx, getHeight()/2-dy*20, width, 20);
						g.setColor(Color.BLACK);
						String s=c.getNote(j).toString();
						if (printStr==0) s=comp.reversedMap.get(s).toString();
						g.drawString(s, filled+dx+width/2-5, getHeight()/2-dy*20+15);
						dy-=1;
					}
					filled+=width;
				}
			}
			int x=0;
			int num=comp.getSize()-numOfEights/2+1;
			for (int i=0; i<num; i++) {
				g.setColor(Color.black);
				g.drawLine(x+dx, getHeight()/2+20*maxChord/2, x+dx, getHeight()/2+20*maxChord/2+20);
				x+=40;
			}
		}
		catch(OutOfBounds ob) {}
	}
	
	public void decDx(int d) {
		dx-=d;
	}
	
	public void setDx(int i) {
		dx=i;
	}
	
	public MusicSymbol getSymbol(int i) throws OutOfBounds{
		return comp.getSymbol(i);
	}
	
	public Composition getComposition() {
		return comp;
	}
}
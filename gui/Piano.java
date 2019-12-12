package gui;

import java.util.Map;
import java.util.HashMap;
import java.awt.*;
import java.awt.event.*;
import music.*;

public class Piano extends Canvas  {
	private Player player;
	private Key[] white=new Key[35];
	private Key[] black=new Key[25];
	private static final String heights[]={"C","D","E","F","G","A","B"};
	private static final String sharps[]= {"C#","D#","F#","G#","A#"};
	private boolean printNotes=false;
	private Map<Integer, Key> keyMap=new HashMap<Integer, Key>();
	private long time, lastRecordedTime, lastRecordedForPause;
	private Key pressedKey;
	private int pressedNote;
	private Composition recordedComposition;
	private boolean isRecording;
	
	private static class Key {
		int left, right, up, down, midi;
		Color color;
		
		public Key(int l, int r, int u, int d, int m, Color c) {
			left=l; right=r; up=u; down=d; midi=m; color=c;
		} 
	} 	
	public void startRecording() {
		recordedComposition=new Composition();
		isRecording = true;
	}
	
	public Composition stopRecording() {
		isRecording = false;
		return recordedComposition;
	}
	
	public Piano() {
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int x=e.getX(), y=e.getY();
				time=System.currentTimeMillis();
				for (int i=0; i<25; i++) {
					if (x>=black[i].left && x<=black[i].right && y>=black[i].up && y<=black[i].down) {
						pressedKey=black[i];
						pressedNote=black[i].midi;
						player.play(pressedNote);
						fillKey(pressedNote);
						return;
					}
				}
				for (int i=0; i<35; i++) {
					if (x>=white[i].left && x<=white[i].right && y>=white[i].up && y<=white[i].down) {
						pressedKey=white[i];
						pressedNote=white[i].midi;
						player.play(pressedNote);
						fillKey(pressedNote);
						return;
					}
				}
			}
			public void mouseReleased(MouseEvent e) {
				long currTime=System.currentTimeMillis();
				if (isRecording) {
					if (recordedComposition.getSize()>0 && currTime-lastRecordedForPause>=1000 && currTime-lastRecordedForPause<2000) 
						recordedComposition.add(new Pause(new Duration(1,8)));
					else if (recordedComposition.getSize()>0 && currTime-lastRecordedTime>=2000)
						recordedComposition.add(new Pause(new Duration(1,4)));
				}
				time=currTime-time;
				player.release(pressedNote);
				Duration dur=null;
				if (time<300) dur=new Duration(1,8);
				else dur=new Duration(1,4);
				try {
					player.play(pressedKey.midi, dur, false);
				} catch (InterruptedException e1) {}
				if (isRecording) {
					Note newNote=new Note(player.composition().reversedMidiMap.get(pressedNote), dur);
					recordedComposition.add(newNote);
					lastRecordedForPause=System.currentTimeMillis();
				}
			}
		});
		this.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				time=System.currentTimeMillis();
				char code=e.getKeyChar();
				String noteString = player.composition().charMap.get(code);
				if (noteString==null) return;
				int pressedNote = player.composition().midiMap.get(noteString);
				player.play(pressedNote);
				fillKey(pressedNote);
			}
			
			public void keyReleased(KeyEvent e) {
				long currTime=System.currentTimeMillis();
				if (isRecording) {
					if (recordedComposition.getSize()>0 && currTime-lastRecordedForPause>=1000 && currTime-lastRecordedForPause<2000) 
						recordedComposition.add(new Pause(new Duration(1,8)));
					else if (recordedComposition.getSize()>0 && currTime-lastRecordedTime>=2000)
						recordedComposition.add(new Pause(new Duration(1,4)));
				}
				time=currTime-time;
				char code=e.getKeyChar();
				Duration dur=null;
				if (time<300) dur=new Duration(1,8);
				else dur=new Duration(1,4);
				String noteString = player.composition().charMap.get(code);
				if (noteString==null) return;
				int pressedNote = player.composition().midiMap.get(noteString);
				player.release(pressedNote);
				try { player.play(pressedNote, dur, false);
				} catch (InterruptedException e1) {}
				if (isRecording) {
					Note newNote=new Note(player.composition().reversedMidiMap.get(pressedNote), dur);
					MusicSymbol m=recordedComposition.getLast();
					currTime=System.currentTimeMillis();
					if (currTime-lastRecordedTime<100 && m!=null && (m instanceof Note || m instanceof Chord)/* && m.symDuration().equalDur(new Duration(1,4)) /*&& dur.equalDur(new Duration(1,4))*/) { 
						recordedComposition.removeLast();
						Chord c=new Chord();
						newNote.setDuration(new Duration(1,4));
						c.add(m); c.add(newNote);
						recordedComposition.add(c);
					}
					else {
						recordedComposition.add(newNote);
						lastRecordedTime=System.currentTimeMillis();
					}
				}
				lastRecordedForPause=System.currentTimeMillis();
			}
		});
	}
	
	public void printNotes(boolean b) {
		printNotes=b;
	}
	public void setPlayer(Player p) {
		player=p;
	}
	
	public void fillKey(int midi) {
		Graphics g=getGraphics();
		Key k = keyMap.get(midi);
		if (k.color==Color.black) g.setColor(new Color(150,0,0));
		else g.setColor(new Color(200, 0, 0));
		g.fillRect(k.left, 0, k.right-k.left, k.down-k.up);
	}
	
	public void paint(Graphics g) {
		int x=0; 
		int width=getWidth()/35, height=getHeight();
		g.setColor(Color.black);
		g.setFont(new Font("Arial", Font.BOLD, 15));
		for (int i=0; i<35; i++) {
			String note=heights[i%7] + (2+i/7);
			int midi=player.composition().midiMap.get(note);
			white[i]=new Key(x, x+width, 0, height, midi, Color.white);
			keyMap.put(midi, white[i]);
			g.drawRect(x, 0, width, height);
			if (printNotes) g.drawString(note, x+width/3, height-20);
			x+=width;
		}
		
		x=3*width/4; 
		int blackWidth=width/2; height=2*height/3;
		for (int i=0; i<25; i++) {
			if ((i%5==0 || i%5==2) && i!=0) x+=width;
			String note=sharps[i%5] + (2+i/5);
			int midi=player.composition().midiMap.get(note);
			black[i]=new Key(x, x+blackWidth, 0, height, midi, Color.black);
			keyMap.put(midi, black[i]);
			g.fillRect(x, 0, blackWidth, height);
			if (printNotes) g.drawString(note, x+blackWidth/4, height+20);
			x+=2*blackWidth;
		}
	}
}
		
package gui;

import javax.sound.midi.*;
import java.util.ArrayList;
import music.*;
import exceptions.*;

public class Player extends Thread {
	private CompositionText ct;
	private Composition comp;
	private CurrentChord currChord=new CurrentChord();
	private int index;
	private MidiChannel channel;
	private boolean working, interrupted;
	private Piano piano;
	private final int decFourth=40, decEighth=20;
	private final long sleepFourth=400, sleepEighth=200;
	
	private class CurrentChord extends Chord implements Runnable {
		Thread thr=new Thread(this);
		
		public CurrentChord() {
			thr.start();
		}
		
		public synchronized void add(Note n) {
			super.add(n); 
		}
		
		public void run() {
			try {
				while (!Thread.interrupted() && !interrupted) {
					MusicSymbol m=null;
					try {
						m = comp.getSymbol(index);
					} catch (OutOfBounds e) { 
						working = false;
						ct.setDx(0);
						ct.repaint();
						index=0;
					 }
					super.clear();
					if (m instanceof Pause && comp.isLoaded() && !comp.isPlaying()) {
						if (m.symDuration().equalDur(new Duration(1,4))) {
							Thread.sleep(sleepFourth); ct.decDx(decFourth); index++; ct.repaint();
						}
						else {
							Thread.sleep(sleepEighth); ct.decDx(decEighth); index++; ct.repaint();
						}
					}
					else if (m instanceof Chord) Thread.sleep(500);
				}
			} catch (InterruptedException e) {}
		}
		public synchronized void stopThread() {
			thr.interrupt();
		}
	}
	
	public Player(CompositionText c) throws MidiUnavailableException {
		ct=c; 
		comp=ct.getComposition();
		channel=getChannel(1);
		working = false;
		start();
	}
	
	private static MidiChannel getChannel(int instrument) throws MidiUnavailableException {
		Synthesizer synthesizer = MidiSystem.getSynthesizer();
		synthesizer.open();
		return synthesizer.getChannels()[instrument];
	}

	public void play(final int note) {
		channel.noteOn(note, 50);
	}
	
	public void release(final int note) {
		channel.noteOff(note, 50);
	}
	
	public Composition composition() {
		return comp;
	}

	public void setPiano(Piano p) {
		piano=p;
	}
	
	public void play(int note, Duration d, boolean autoPlay) throws InterruptedException {
		int dec; long length;
		MusicSymbol m=null;
		try {
			m=comp.getSymbol(index);
		} catch (OutOfBounds e1) {
			m=null;
		}
		if (d.equalDur(new Duration(1, 8))) {
			length = sleepEighth; dec = decEighth;
		} else {
			length = sleepFourth; dec = decFourth;
		}
		if (autoPlay) {
			play(note);
			piano.fillKey(note);
			sleep(length);
			release(note);
			ct.decDx(dec);
		}
		else if (comp.isLoaded() && !comp.isPlaying() && (m instanceof Note) && m.symDuration().equalDur(d) && comp.midiMap.get(((Note)m).toString().toUpperCase())==note) {
			ct.decDx(dec); index++;
		}
		else if (comp.isLoaded() && !comp.isPlaying() && (m instanceof Chord)) {
			Chord c=(Chord)m;
			boolean found=false;
			for (int i=0; i<c.size(); i++) {
				try {
					if (comp.midiMap.get(c.getNote(i).toString())==note) {
						currChord.add(c.getNote(i)); found=true;
					}
				} catch (OutOfBounds e) {}
			}
			if (!found) currChord.clear();
			if (currChord.equalChord(c)) {
				ct.decDx(decFourth); index++; currChord.clear();
			}
		}
		ct.repaint();
		piano.repaint();
	}

	public void playChord(ArrayList<Integer> notes, boolean autoPlay) throws InterruptedException {
		for (int i = 0; i < notes.size(); i++) {
			play(notes.get(i));
			piano.fillKey(notes.get(i));
		}
		sleep(sleepFourth);
		for (int i = 0; i < notes.size(); i++)
			release(notes.get(i));
		ct.decDx(decFourth);
		piano.repaint();
		ct.repaint();
	}

	public void run() {
		try {
			while (!interrupted()) {
				for (index = 0; index < comp.getSize(); index++) {
					while (!working) synchronized (this) { wait(); }
					
					MusicSymbol m = ct.getSymbol(index);
					if (m instanceof Pause) {
						if (m.symDuration().equalDur(new Duration(1, 8))) {
							sleep(sleepEighth); ct.decDx(decEighth);
						} else {
							sleep(sleepFourth); ct.decDx(decFourth);
						}
					} else if (m instanceof Note) {
						Note n=(Note)m;
						int midi = comp.midiMap.get(n.toString().toUpperCase());
						play(midi, ct.getSymbol(index).symDuration(), true);
					} else {
						Chord chord = (Chord) m;
						ArrayList<Integer> notes = new ArrayList<Integer>();
						for (int j = 0; j < chord.size(); j++) {
							int midi = comp.midiMap.get(chord.getNote(j).toString());
							notes.add(midi);
						}
						playChord(notes, true);
					}
					sleep(100);
				}
				working = false;
				ct.setDx(0);
				ct.repaint();
			}
		} catch (InterruptedException | OutOfBounds e) {
		}
	}

	public synchronized void pauseThread() {
		working = false;
	}

	public synchronized void playThread() {
		working = true;
		if (currChord==null) currChord=new CurrentChord();
		notifyAll();
	}

	public synchronized void stopThread() {
		currChord.stopThread();
		currChord=null;
		interrupted=true;
		interrupt();
	}
}
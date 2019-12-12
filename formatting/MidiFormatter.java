package formatting;

import music.*;
import java.io.*;
import javax.sound.midi.*;

import exceptions.OutOfBounds;

public class MidiFormatter extends Formatter {
	private long actionTime=0, tpq=48;
	
	public MidiFormatter(String s, Composition c) {
		super(s, c);
	}

	@Override
	public void exportFormat() {
		try {
			Sequence s=new Sequence(Sequence.PPQ,24);
			Track t=s.createTrack();
			byte[] b= {(byte)0xF0, 0x7E, 0x7F, 0x09, 0x01, (byte)0xF7};
			SysexMessage ssm=new SysexMessage();
			ssm.setMessage(b, 6);
			MidiEvent me=new MidiEvent(ssm, actionTime);
			t.add(me);
			MetaMessage mtm=new MetaMessage();
			byte[] bt= {0x02,(byte)0x00,0x00};
			mtm.setMessage(0x51,bt,3);
			me=new MidiEvent(mtm,actionTime);
			t.add(me);
			ShortMessage shm=new ShortMessage();
			shm.setMessage(0xC0,0x00,0x00);
			me=new MidiEvent(shm,(long)0);
			t.add(me);
			actionTime=1;
			
			for (int i=0; i<composition.getSize(); i++) {
				MusicSymbol m=null;
				try {
					m = composition.getSymbol(i);
				} catch (OutOfBounds e) { continue; }
				int rhythm;
				if (m.symDuration().equalDur(new Duration(1,4))) rhythm = 2;
				else rhythm = 1;
				if (m instanceof Note) {
					Note n=(Note)m;
					int midi=composition.midiMap.get(n.toString().toUpperCase());
					
					shm=new ShortMessage();
					shm.setMessage(0x90,midi,100);
					me=new MidiEvent(shm,actionTime);
					t.add(me);
					shm=new ShortMessage();
					shm.setMessage(0x80,midi,100);
					me=new MidiEvent(shm,actionTime+tpq*rhythm);
					t.add(me);
				}
				else if (m instanceof Chord) {
					Chord c=(Chord)m;
					for (int j=0; j<c.size();j++) {
						Note note=null;
						try {
							note = c.getNote(j);
						} catch (OutOfBounds e) { continue; }
						int midi=composition.midiMap.get(note.toString().toUpperCase());
						long action=actionTime;
						shm=new ShortMessage();
						shm.setMessage(0x90,midi,100);
						me=new MidiEvent(shm,action);
						t.add(me);
						action+=tpq*rhythm;
						shm=new ShortMessage();
						shm.setMessage(0x80,midi,100);
						me=new MidiEvent(shm,action);
						t.add(me);
					}
				}
				actionTime+=tpq*rhythm;
			}
			actionTime+=tpq;
			mtm=new MetaMessage();
			byte[] bet={};
			mtm.setMessage(0x2F,bet,0);
			me=new MidiEvent(mtm,actionTime);
			t.add(me);
			File f=new File(directory+".mid");
			MidiSystem.write(s, 1, f);
		}
		catch(InvalidMidiDataException | IOException e) {}
	}

}
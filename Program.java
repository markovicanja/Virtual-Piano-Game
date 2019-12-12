import music.*;
import formatting.*;
import gui.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.midi.MidiUnavailableException;

public class Program extends Frame implements ActionListener, ItemListener {
	private Piano piano;
	private Composition comp, recorded;
	private CompositionText ct;
	private Player player;
	
	private Button play, pause, stop, loadComp, startRecording, endRecording, toBegin, export;
	private TextField compositionPath, exportPath;
	private Checkbox letters, notes, midi, txt, printNotes;
	private boolean txtExported, midiExported, closing;
	private ClosingDialog dialog=new ClosingDialog(this, "Napustanje programa", true);
	
	private class ClosingDialog extends Dialog implements ActionListener {
		private Label l;
		private Button da, ne;
		private TextField midi, txt;
		private Panel pan1, pan2;
		
		public ClosingDialog(Frame f, String s, boolean b) {
			super(f, s, b);
			setSize(500,180);
			setLocationRelativeTo(null);
			l=new Label("Da li ste sigurni da zelite da napustite program?",Label.CENTER);
			da=new Button("Da"); ne=new Button("Ne"); 
			da.addActionListener(this); ne.addActionListener(this);
			Panel buttons=new Panel();
			
			buttons.add(da); buttons.add(ne);
			this.add(l, "North");
			this.add(buttons, "Center");
			Panel south=new Panel(new GridLayout(2,1));
			this.add(south, "South");
			
			pan1=new Panel();
			pan1.setVisible(false);
			pan1.add(new Label("Midi:", Label.RIGHT));
			midi=new TextField("", 50); pan1.add(midi);
			
			pan2=new Panel();
			pan2.setVisible(false);
			pan2.add(new Label(" Txt:", Label.RIGHT));
			txt=new TextField("", 50); pan2.add(txt);
			south.add(pan1); south.add(pan2);
			
			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					setVisible(false);
				}
			});
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand()=="Da") {
				closing=true;
				if (!midiExported || !txtExported) {
					l.setText("Ako zelite da eksportujete kopmpozicije upisite izlazne putanje");
					da.setLabel("Eksportuj"); ne.setLabel("Izadji");
					if (!midiExported) pan1.setVisible(true);
					if (!txtExported) pan2.setVisible(true);
					validate();
				}
				else setVisible(false);
			}
			else if (e.getActionCommand()=="Ne"){
				this.setVisible(false);
				closing=false;
			}
			else if (e.getActionCommand()=="Eksportuj") {
				if (midi.getText()!="" && recorded!=null) {
					MidiFormatter mf=new MidiFormatter(midi.getText(), recorded);
					mf.exportFormat();
				}
				if (txt.getText()!="" && recorded!=null) {
					TxtFormatter tf=new TxtFormatter(txt.getText(), recorded);
					tf.exportFormat();
				}
			}
			else if (e.getActionCommand()=="Izadji") {
				this.setVisible(false);
			}
		}
	}
	
	public Program() {
		super("Piano");
		setSize(1500,800);
		this.setLocationRelativeTo(null);
		addComponents();
		dialog.setVisible(false);
		setVisible(true);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dialog.setVisible(true);
				if (closing) {
					if (player!=null) player.stopThread();
					dispose();
				}
			}
		}); 
	}
	
	public void addComponents() {
		comp=new Composition();
		//C:\\Users\\ma170420d\\Desktop\\POOP\\we_wich_you_a_merry_christmas.txt
		ct=new CompositionText(comp);
		try {
			player=new Player(ct);
		}
		catch (MidiUnavailableException m) {player=null;} 
		piano=new Piano();
		piano.setPlayer(player);
		player.setPiano(piano);
		
		Panel panel=new Panel(new GridLayout(2,1));
		Panel north=new Panel(new GridLayout(5,1)); north.setBackground(Color.LIGHT_GRAY);
		Panel center=new Panel(new BorderLayout());
		center.add(ct, "Center");
				
		panel.add(center);
		panel.add(piano);
		
		add(north, "North");
		add(panel, "Center");
		
		Panel[] panels=new Panel[5];
		for (int i=0; i<5; i++) {
			panels[i]=new Panel();
			north.add(panels[i]);
		}
		
		panels[0].add(new Label("Composition directory:", Label.CENTER));
		compositionPath=new TextField("C:\\Users\\ma170420d\\Desktop\\POOP\\we_wich_you_a_merry_christmas.txt", 50);
		panels[0].add(compositionPath);
		loadComp=new Button("Load");
		loadComp.addActionListener(this);
		panels[0].add(loadComp);
		CheckboxGroup cg=new CheckboxGroup();
		
		letters=new Checkbox("Letters", true, cg); letters.addItemListener(this);
		notes=new Checkbox("Notes", false, cg); notes.addItemListener(this);
		panels[0].add(letters); panels[0].add(notes);
		
		play=new Button("Play"); play.addActionListener(this); panels[1].add(play);
		pause=new Button("Pause"); pause.addActionListener(this); panels[1].add(pause);
		stop=new Button("Stop"); stop.addActionListener(this); panels[1].add(stop);
		
		printNotes=new Checkbox("Print notes on piano"); printNotes.addItemListener(this);
		panels[2].add(printNotes);
		toBegin=new Button("Return back"); toBegin.addActionListener(this);
		panels[2].add(toBegin);
		
		startRecording=new Button("Start recording"); startRecording.addActionListener(this); panels[3].add(startRecording);
		endRecording=new Button("Stop recording"); endRecording.addActionListener(this); panels[3].add(endRecording);
		endRecording.setEnabled(false);
		
		CheckboxGroup cb1=new CheckboxGroup();
		
		midi=new Checkbox("Midi", cb1, false); panels[4].add(midi);
		txt=new Checkbox("Txt", cb1, true); panels[4].add(txt);
		Label l=new Label("Export to: ", Label.CENTER); panels[4].add(l);
		exportPath=new TextField("C:\\Users\\ma170420d\\Desktop\\POOP\\anja.txt", 50); panels[4].add(exportPath);
		export=new Button("Export"); panels[4].add(export); export.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand()=="Load") {
			String directory=compositionPath.getText();
			comp.parse(directory);
			comp.setLoaded(true);
			ct.setDx(0);
			try {
				if (player!=null) {
					player.stopThread();
					player.join();
				}
				player=new Player(ct);
				piano.setPlayer(player);
				player.setPiano(piano);
			} catch (MidiUnavailableException | InterruptedException e1) {
				return;
			}
			play.setEnabled(true); pause.setEnabled(true); stop.setEnabled(true);
			ct.repaint();
		}
		else if (e.getActionCommand()=="Play") {
			try { if (player==null) player=new Player(ct);
			} catch (MidiUnavailableException e1) { return; }
			piano.setPlayer(player); player.setPiano(piano); player.playThread();
			comp.setPlaying(true);
			play.setEnabled(false); pause.setEnabled(true); stop.setEnabled(true);
		}
		else if (e.getActionCommand()=="Pause") {
			if (player!=null) player.pauseThread();
			comp.setPlaying(false);
			play.setEnabled(true); pause.setEnabled(false); stop.setEnabled(true);
		}
		else if (e.getActionCommand()=="Stop") {
			if (player!=null) player.stopThread();
			player=null;
			comp.setPlaying(false);
			play.setEnabled(false); pause.setEnabled(false); stop.setEnabled(false);
		}
		else if (e.getActionCommand()=="Return back") {
			ct.setDx(0);
			if (player!=null) {
				player.stopThread(); 
				try { player.join();}
				catch (InterruptedException e1) {}
			}
			try {
				player=new Player(ct);
			} catch (MidiUnavailableException e1) {return;}
			piano.repaint(); ct.repaint();
			comp.setPlaying(false);
			play.setEnabled(true); pause.setEnabled(true); stop.setEnabled(true);
		}
		else if (e.getActionCommand()=="Start recording") {
			piano.startRecording();
			startRecording.setEnabled(false);
			endRecording.setEnabled(true);
		}
		else if (e.getActionCommand()=="Stop recording") {
			recorded=piano.stopRecording();
			startRecording.setEnabled(true);
			endRecording.setEnabled(false);
//			System.out.println(recorded);
			midiExported=false; txtExported=false;
		}
		else if (e.getActionCommand()=="Export") {
			String directory=exportPath.getText();
			if (directory=="" || recorded==null) return;
			Formatter formatter=null;
			if (midi.getState()) {
				formatter=new MidiFormatter(directory, recorded);
				midiExported=true;
			}
			else {
				formatter=new TxtFormatter(directory, recorded);
				txtExported=true;
			}
			formatter.exportFormat();
		}
	}	
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource()==printNotes) {
			if (e.getStateChange()==ItemEvent.SELECTED) piano.printNotes(true);
			else piano.printNotes(false);
			piano.repaint();
		}
		else if (e.getSource()==letters) {
			if (e.getStateChange()==ItemEvent.SELECTED) ct.printString(0);
			ct.repaint();
		}
		else if (e.getSource()==notes) {
			if (e.getStateChange()==ItemEvent.SELECTED) ct.printString(1);
			ct.repaint();
		}
		piano.repaint();
	}
	
	public static void main(String[] args) {
		new Program();
	}
}

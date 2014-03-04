package com.arianjm.tfg.deskvoice.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.arianjm.tfg.deskvoice.Command;
import com.arianjm.tfg.deskvoice.CommandControl;

public class DeskVoice implements KeyListener{

	private JFrame frmToolbar;
	
	private JTextField txtProgram;
	private JTextField txtCommand;
	private static JTextField txtCommandName;
	private static JButton btnSpeak;
	private static JTextPane textPaneGrammar;
	private static JTextPane txtpnSpeak;
	private static CommandControl commandControl;
	private static JTextPane textPaneRecognized;
	private static JPanel panelWords;
	private JComboBox<String> comboBox;
	private JTextField textFieldPronunciation;
	private JTextField textFieldWord;
	private JLabel lblPressedKeys;
	
	private static boolean settingShortcuts = false;
	private String keys = "";
	private List<Integer> keyCodes = new ArrayList<Integer>();
	private List<String> pressedKeys = new ArrayList<String>();
	//private String[] pressedKeys = {"None", "None", "None"};
	private static boolean recording;
	private String programPath = null;

	/**
	 * Launch the application.
	 */
	public static void main(final String[] args) {
		recording = false;
		setCommandControl(new CommandControl(args));
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DeskVoice window = new DeskVoice();
					window.frmToolbar.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Create the application.
	 */
	public DeskVoice() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmToolbar = new JFrame();
		frmToolbar.setTitle("DeskVoice");
		frmToolbar.setBounds(100, 100, 700, 600);
		frmToolbar.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				switch(tabbedPane.getSelectedIndex()){
				case 0:
					//System.out.println("Speak");
					break;
				case 1:
					//System.out.println("Add Command");
					break;
				case 2:
					//System.out.println("Grammar");
					textPaneGrammar.setText(getCommandControl().getGrammar());
					break;
				case 3:
					//System.out.println("Recognized Words");
					textPaneRecognized.setText(getCommandControl().getRecognized());
					break;
				case 4:
					//System.out.println("Check pronunciation");
					break;
				case 5:
					//System.out.println("Recognized Words 2");
				default:
					System.out.println("Error");
				}
			}
		});
		frmToolbar.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		JPanel panelPrincipal = new JPanel();
		tabbedPane.addTab("Speak", null, panelPrincipal, null);
		panelPrincipal.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane_2 = new JScrollPane();
		panelPrincipal.add(scrollPane_2);
		
		btnSpeak = new JButton("");	
		btnSpeak.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Thread com = new Thread(getCommandControl());
				com.start();
			}
		});
		btnSpeak.setDisabledIcon(new ImageIcon(DeskVoice.class.getResource("/com/arianjm/tfg/deskvoice/img/mic-pressed.png")));
		btnSpeak.setToolTipText("Speak");
		btnSpeak.setIcon(new ImageIcon(DeskVoice.class.getResource("/com/arianjm/tfg/deskvoice/img/mic.png")));
		panelPrincipal.add(btnSpeak, BorderLayout.CENTER);
		
		txtpnSpeak = new JTextPane();
		txtpnSpeak.setText("Things that you said");
		panelPrincipal.add(txtpnSpeak, BorderLayout.SOUTH);
		
		JTextArea textAreaSpeakHelp = new JTextArea();
		textAreaSpeakHelp.setEditable(false);
		textAreaSpeakHelp.setBackground(SystemColor.menu);
		textAreaSpeakHelp.setText(DeskVoiceHelp.speakHelp);
		panelPrincipal.add(textAreaSpeakHelp, BorderLayout.EAST);
		
		JPanel panelAddCommand = new JPanel();
		tabbedPane.addTab("Add Command", null, panelAddCommand, "Add command");
		GridBagLayout gbl_panelAddCommand = new GridBagLayout();
		gbl_panelAddCommand.columnWidths = new int[]{45, 474, 101, 0};
		gbl_panelAddCommand.rowHeights = new int[] {0, 0, 0, 0, 0, 0};
		gbl_panelAddCommand.columnWeights = new double[]{0.0, 1.0, 1.0, Double.MIN_VALUE};
		gbl_panelAddCommand.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0};
		panelAddCommand.setLayout(gbl_panelAddCommand);
		
		JLabel lblName = new JLabel("Name:");
		GridBagConstraints gbc_lblName = new GridBagConstraints();
		gbc_lblName.anchor = GridBagConstraints.WEST;
		gbc_lblName.insets = new Insets(0, 0, 5, 5);
		gbc_lblName.gridx = 0;
		gbc_lblName.gridy = 0;
		panelAddCommand.add(lblName, gbc_lblName);
		
		txtCommandName = new JTextField();
		txtCommandName.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent arg0) {
				String[] words = txtCommandName.getText().split("\\s+");
				if(words.length == 0) return;
				if(words.length == 1) if (words[0].isEmpty()) return;
				
				for(String word : words){
					if(getCommandControl().getPronunciation(word).contains("is not in the dictionary")){
						txtCommandName.setText("The word '"+word+"' is not in the dictionary");
						break;
					}
				}
			}
			@Override
			public void focusGained(FocusEvent arg0) {
				txtCommandName.setText("");
			}
		});
		
		txtCommandName.setText("Name of the command");
		GridBagConstraints gbc_txtCommandName = new GridBagConstraints();
		gbc_txtCommandName.insets = new Insets(0, 0, 5, 5);
		gbc_txtCommandName.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtCommandName.gridx = 1;
		gbc_txtCommandName.gridy = 0;
		panelAddCommand.add(txtCommandName, gbc_txtCommandName);
		txtCommandName.setColumns(10);
		
		JLabel label = new JLabel("Category:");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.insets = new Insets(0, 0, 5, 5);
		gbc_label.anchor = GridBagConstraints.WEST;
		gbc_label.gridx = 0;
		gbc_label.gridy = 1;
		panelAddCommand.add(label, gbc_label);
		
		final JLabel lblCommand = new JLabel("Command:");
		lblCommand.setVisible(false);
		GridBagConstraints gbc_lblCommand = new GridBagConstraints();
		gbc_lblCommand.anchor = GridBagConstraints.WEST;
		gbc_lblCommand.insets = new Insets(0, 0, 5, 5);
		gbc_lblCommand.gridx = 0;
		gbc_lblCommand.gridy = 2;
		panelAddCommand.add(lblCommand, gbc_lblCommand);
		
		final JLabel lblProgram = new JLabel("Program:");
		GridBagConstraints gbc_lblProgram = new GridBagConstraints();
		gbc_lblProgram.anchor = GridBagConstraints.WEST;
		gbc_lblProgram.insets = new Insets(0, 0, 5, 5);
		gbc_lblProgram.gridx = 0;
		gbc_lblProgram.gridy = 3;
		panelAddCommand.add(lblProgram, gbc_lblProgram);
		
		final JLabel lblKey = new JLabel("Keyboard Shortcut:");
		GridBagConstraints gbc_lblKey = new GridBagConstraints();
		gbc_lblKey.insets = new Insets(0, 0, 5, 5);
		gbc_lblKey.gridx = 0;
		gbc_lblKey.gridy = 4;
		lblKey.setVisible(false);
		panelAddCommand.add(lblKey, gbc_lblKey);
		
		lblPressedKeys = new JLabel("None");
		GridBagConstraints gbc_lblPressedKeys = new GridBagConstraints();
		gbc_lblPressedKeys.insets = new Insets(0, 0, 5, 5);
		gbc_lblPressedKeys.gridx = 1;
		gbc_lblPressedKeys.gridy = 4;
		lblPressedKeys.setVisible(false);
		panelAddCommand.add(lblPressedKeys, gbc_lblPressedKeys);
		
		final JButton btnSearchProgram = new JButton("Browse...");
		btnSearchProgram.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				final JFileChooser fc = new JFileChooser();
				fc.setFileHidingEnabled(false);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("EXE", "exe");
				fc.setFileFilter(filter);
	            fc.showOpenDialog(frmToolbar);
	            
	            if(fc.getSelectedFile() != null){
		            File file = fc.getSelectedFile();
		            programPath = file.getAbsolutePath();
		            txtProgram.setText(programPath);
	            }else{
	            	txtProgram.setText("No program selected");
	            	programPath = null;
	            }
			}
		});
		//TODO Falta el listener del boton
		final JButton btnStartShortcut = new JButton("Start shortcut");
		btnStartShortcut.setVisible(false);
		btnStartShortcut.setFocusTraversalKeysEnabled(false);
		btnStartShortcut.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent arg0){
				if(btnStartShortcut.getText().equals("Start shortcut")){
					settingShortcuts = true;
					btnStartShortcut.setText("Finish shortcut");
				}else{
					settingShortcuts = false;
					btnStartShortcut.setText("Start shortcut");
					/*pressedKeys.clear();
					keyCodes.clear();
					keys = "";*/
				}
				
			}
		});
		btnStartShortcut.addKeyListener(this);
		
		comboBox = new JComboBox<String>();
		
		comboBox.setToolTipText("Select a category");
		comboBox.addItem("Program");
		comboBox.addItem("Command");
		comboBox.addItem("Keyboard Shortcut");
		
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.insets = new Insets(0, 0, 5, 5);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 1;
		gbc_comboBox.gridy = 1;
		panelAddCommand.add(comboBox, gbc_comboBox);
		
		comboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				System.out.println(comboBox.getSelectedItem());
				
				lblProgram.setVisible(false);
				btnSearchProgram.setVisible(false);
				txtProgram.setVisible(false);
				
				lblCommand.setVisible(false);
				txtCommand.setVisible(false);
				
				lblKey.setVisible(false);
				btnStartShortcut.setVisible(false);
				lblPressedKeys.setVisible(false);
				if(btnStartShortcut.getText().equals("Finish shortcut")){
					settingShortcuts = false;
					btnStartShortcut.setText("Start shortcut");
				}
				
				if(comboBox.getSelectedItem().equals("Command")){
					//Command
					lblCommand.setVisible(true);
					txtCommand.setVisible(true);
				}else if(comboBox.getSelectedItem().equals("Program")){
					//Program
					lblProgram.setVisible(true);
					btnSearchProgram.setVisible(true);
					txtProgram.setVisible(true);
				}else if(comboBox.getSelectedItem().equals("Keyboard Shortcut")){
					//Keyboard Shortcut
					if(!pressedKeys.isEmpty()) pressedKeys.clear();
					if(!keyCodes.isEmpty()) keyCodes.clear();
					keys = "";
					lblKey.setVisible(true);
					btnStartShortcut.setVisible(true);
					lblPressedKeys.setVisible(true);
					lblPressedKeys.setText("None");
				}
			}
		});
		
		txtCommand = new JTextField();
		txtCommand.setVisible(false);
		txtCommand.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				txtCommand.setText("");
			}
		});
		txtCommand.setText("Command");
		GridBagConstraints gbc_txtCommand = new GridBagConstraints();
		gbc_txtCommand.insets = new Insets(0, 0, 5, 5);
		gbc_txtCommand.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtCommand.gridx = 1;
		gbc_txtCommand.gridy = 2;
		panelAddCommand.add(txtCommand, gbc_txtCommand);
		txtCommand.setColumns(10);
		
		txtProgram = new JTextField();
		txtProgram.setColumns(10);
		GridBagConstraints gbc_txtProgram = new GridBagConstraints();
		gbc_txtProgram.insets = new Insets(0, 0, 5, 5);
		gbc_txtProgram.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtProgram.gridx = 1;
		gbc_txtProgram.gridy = 3;
		panelAddCommand.add(txtProgram, gbc_txtProgram);
		GridBagConstraints gbc_btnSearchProgram = new GridBagConstraints();
		gbc_btnSearchProgram.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnSearchProgram.insets = new Insets(0, 0, 5, 0);
		gbc_btnSearchProgram.gridx = 2;
		gbc_btnSearchProgram.gridy = 3;
		panelAddCommand.add(btnSearchProgram, gbc_btnSearchProgram);
		
		JButton btnAddCommand = new JButton("Add Command");
		btnAddCommand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(comboBox.getSelectedItem().equals("Command")){
					//Command
					if(txtCommand.getText().isEmpty()){
						txtCommand.setText("No command written");
						return;
					}else{
						getCommandControl().addCommand(txtCommandName.getText(),
								Command.Category.Command, txtCommand.getText());
					}
				}else if(comboBox.getSelectedItem().equals("Program")){
					//Program
					if(programPath == null){
						txtProgram.setText("No program selected");
						return;
					}
					if(!programPath.isEmpty())
						getCommandControl().addCommand(txtCommandName.getText(),
								Command.Category.Program, programPath);
				}else if(comboBox.getSelectedItem().equals("Keyboard Shortcut")){
					//Shortcut
					String commandCodes = "";
					for(int code: keyCodes) commandCodes += code + " ";
					if(commandCodes.isEmpty()) return;
					getCommandControl().addCommand(txtCommandName.getText(),
							Command.Category.Shortcut, commandCodes);
					
					if(btnStartShortcut.getText().equals("Finish shortcut")){
						settingShortcuts = false;
						btnStartShortcut.setText("Start shortcut");
					}
				}
			}
		});
		
		GridBagConstraints gbc_btnStartShortcut = new GridBagConstraints();
		gbc_btnStartShortcut.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnStartShortcut.insets = new Insets(0, 0, 5, 0);
		gbc_btnStartShortcut.gridx = 2;
		gbc_btnStartShortcut.gridy = 4;
		panelAddCommand.add(btnStartShortcut, gbc_btnStartShortcut);
		GridBagConstraints gbc_btnAddCommand = new GridBagConstraints();
		gbc_btnAddCommand.insets = new Insets(0, 0, 5, 5);
		gbc_btnAddCommand.gridx = 1;
		gbc_btnAddCommand.gridy = 5;
		panelAddCommand.add(btnAddCommand, gbc_btnAddCommand);
		
		JScrollPane scrollPane_3 = new JScrollPane();
		GridBagConstraints gbc_scrollPane_3 = new GridBagConstraints();
		gbc_scrollPane_3.gridwidth = 3;
		gbc_scrollPane_3.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_3.gridx = 0;
		gbc_scrollPane_3.gridy = 6;
		panelAddCommand.add(scrollPane_3, gbc_scrollPane_3);
		
		JTextPane txtPaneCommandHelp = new JTextPane();
		txtPaneCommandHelp.setBackground(SystemColor.menu);
		txtPaneCommandHelp.setEditable(false);
		txtPaneCommandHelp.setText(DeskVoiceHelp.addCommandHelp);
		scrollPane_3.setViewportView(txtPaneCommandHelp);
		
		JPanel panelGrammar = new JPanel();
		tabbedPane.addTab("Grammar", null, panelGrammar, null);
		panelGrammar.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		panelGrammar.add(scrollPane, BorderLayout.CENTER);
		
		textPaneGrammar = new JTextPane();
		scrollPane.setViewportView(textPaneGrammar);
		textPaneGrammar.setText(getCommandControl().getGrammar());
		
		JButton btnReloadGrammar = new JButton("Reload Grammar");
		btnReloadGrammar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setTextPaneGrammar(getCommandControl().getGrammar());
			}
		});
		panelGrammar.add(btnReloadGrammar, BorderLayout.SOUTH);
		
		JScrollPane scrollPane_4 = new JScrollPane();
		panelGrammar.add(scrollPane_4, BorderLayout.EAST);
		
		JTextPane txtPaneGrammarHelp = new JTextPane();
		txtPaneGrammarHelp.setBackground(SystemColor.menu);
		txtPaneGrammarHelp.setEditable(false);
		txtPaneGrammarHelp.setText(DeskVoiceHelp.grammarHelp);
		scrollPane_4.setViewportView(txtPaneGrammarHelp);
		
		JPanel panelRecognized = new JPanel();
		tabbedPane.addTab("Recognized Words", null, panelRecognized, null);
		panelRecognized.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane_1 = new JScrollPane();
		panelRecognized.add(scrollPane_1, BorderLayout.CENTER);
		
		textPaneRecognized = new JTextPane();
		scrollPane_1.setViewportView(textPaneRecognized);
		textPaneRecognized.setText(getCommandControl().getRecognized());
		
		JButton btnSaveWords = new JButton("Save Recognized Words");
		btnSaveWords.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				getCommandControl().setRecognized(textPaneRecognized.getText());
			}
		});
		panelRecognized.add(btnSaveWords, BorderLayout.SOUTH);
		
		JScrollPane scrollPane_5 = new JScrollPane();
		panelRecognized.add(scrollPane_5, BorderLayout.EAST);
		
		JTextPane txtPaneRecognizedHelp = new JTextPane();
		txtPaneRecognizedHelp.setBackground(SystemColor.menu);
		txtPaneRecognizedHelp.setEditable(false);
		txtPaneRecognizedHelp.setText(DeskVoiceHelp.recognizedHelp);
		scrollPane_5.setViewportView(txtPaneRecognizedHelp);
		
		JPanel panelPronunciation = new JPanel();
		tabbedPane.addTab("Pronunciation", null, panelPronunciation, null);
		panelPronunciation.setLayout(null);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(80, 80, 475, 78);
		panelPronunciation.add(panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[] {0, 0, 70, 0, 0, 0, 200, 0};
		gbl_panel_1.rowHeights = new int[]{0, 0, 0, 0};
		gbl_panel_1.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		gbl_panel_1.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);
		
		JLabel lblWord = new JLabel("Word:");
		GridBagConstraints gbc_lblWord = new GridBagConstraints();
		gbc_lblWord.insets = new Insets(0, 0, 5, 5);
		gbc_lblWord.gridx = 1;
		gbc_lblWord.gridy = 1;
		panel_1.add(lblWord, gbc_lblWord);
		
		textFieldWord = new JTextField();
		GridBagConstraints gbc_textFieldWord = new GridBagConstraints();
		gbc_textFieldWord.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldWord.gridwidth = 2;
		gbc_textFieldWord.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldWord.gridx = 2;
		gbc_textFieldWord.gridy = 1;
		panel_1.add(textFieldWord, gbc_textFieldWord);
		textFieldWord.setColumns(10);
		
		JLabel lblPronunciation = new JLabel("Pronunciation:");
		GridBagConstraints gbc_lblPronunciation = new GridBagConstraints();
		gbc_lblPronunciation.insets = new Insets(0, 0, 5, 5);
		gbc_lblPronunciation.gridx = 5;
		gbc_lblPronunciation.gridy = 1;
		panel_1.add(lblPronunciation, gbc_lblPronunciation);
		
		textFieldPronunciation = new JTextField();
		GridBagConstraints gbc_textFieldPronunciation = new GridBagConstraints();
		gbc_textFieldPronunciation.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldPronunciation.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldPronunciation.gridx = 6;
		gbc_textFieldPronunciation.gridy = 1;
		panel_1.add(textFieldPronunciation, gbc_textFieldPronunciation);
		textFieldPronunciation.setColumns(10);
		
		JButton btnSearchPronunciation = new JButton("Search");
		btnSearchPronunciation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(textFieldWord.getText().length() <= 50)
					textFieldPronunciation.setText(getCommandControl().getPronunciation(textFieldWord.getText()));
				else{
					textFieldPronunciation.setText("Try a shorter word");
					textFieldWord.setText("");
				}
			}
		});
		GridBagConstraints gbc_btnSearchPronunciation = new GridBagConstraints();
		gbc_btnSearchPronunciation.insets = new Insets(0, 0, 0, 5);
		gbc_btnSearchPronunciation.gridx = 5;
		gbc_btnSearchPronunciation.gridy = 2;
		panel_1.add(btnSearchPronunciation, gbc_btnSearchPronunciation);
		
		JScrollPane scrollPane_6 = new JScrollPane();
		scrollPane_6.setBounds(10, 228, 659, 128);
		panelPronunciation.add(scrollPane_6);
		
		JTextPane txtPanePronunciationHelp = new JTextPane();
		txtPanePronunciationHelp.setBackground(SystemColor.menu);
		txtPanePronunciationHelp.setEditable(false);
		txtPanePronunciationHelp.setText(DeskVoiceHelp.pronunciationHelp);
		scrollPane_6.setViewportView(txtPanePronunciationHelp);
	}
	
	private GridBagConstraints createGBC(int x, int y){
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(0, 0, 5, 5);
		gbc.gridx = x;
		gbc.gridy = y;
		
		return gbc;
	}

	/**
	 * @return the commandControl
	 */
	public static CommandControl getCommandControl() {
		return commandControl;
	}

	/**
	 * @param commandControl the commandControl to set
	 */
	public static void setCommandControl(CommandControl commandControl) {
		DeskVoice.commandControl = commandControl;
	}

	public static void setTranscript(String transcript){
		txtpnSpeak.setText(transcript);
	}
	
	public static void setTextPaneGrammar(String grammar){
		textPaneGrammar.setText(grammar);
	}
	
	public static void setRecording(boolean rec){
		recording = rec;
		if(recording){
			btnSpeak.setEnabled(false);
		}else{
			btnSpeak.setEnabled(true);
		}
	}

	public static void notInDictionary(String word) {
		txtCommandName.setText("Sorry '"+word+"' is not in the dictionary");
		
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if(settingShortcuts){
			int keyCode = arg0.getKeyCode();
			String keyName = KeyEvent.getKeyText(arg0.getKeyCode());
			keyCodes.add(keyCode);
			pressedKeys.add(keyName);
			
			String keys = "";
			for(int i = 0 ; i<pressedKeys.size() ; i++){
				if(i == pressedKeys.size()-1)
					keys += keyName;
				else
					keys += pressedKeys.get(i) + " + ";
			}

			lblPressedKeys.setText(keys);
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}
}

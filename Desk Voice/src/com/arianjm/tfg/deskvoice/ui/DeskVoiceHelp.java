package com.arianjm.tfg.deskvoice.ui;

public class DeskVoiceHelp {
	public static final String speakHelp = "Welcome to DeskVoice!\n\n" +
											"This is the main view.\n\n" +
											"To start using DeskVoice just\n" +
											"press the button and start speaking,\n" +
											"whenever you want to stop just say\n" +
											"'stop recording' and DeskVoice will\n" +
											"stop listening, to start again press\n" +
											"the button";
	
	public static final String addCommandHelp = "In this tab you can add commands for DeskVoice to understand.\n\n" +
												"In the 'Name' field you write the word you want DeskVoice to understand.\n" +
												"In the 'Categories' field you have select if you want to add a program or a command.\n" +
												"If you choose to add a Program, press 'Browse...' and find the .exe file you want to open\n" +
												"If you choose to add a Command, write the Command in the field.\n" +
												"If you choose to add a Keyboard Shortcut, press 'Start Shortcut', then press " +
												"the key combination secuentially key by key, then press 'Finish Shortcut'.\n" +
												"Finnally press 'Add Command'.";
	
	public static final String grammarHelp = "In this tab you can see how\n" +
											"DeskVoice's grammar works,\n" +
											"although any change here will\n" +
											"be undone next time you open\n" +
											"DeskVoice since the grammar\n" +
											"file is dynamically created\n" +
											"each time you open DeskVoice\n" +
											"or add a command or\n" +
											"recognized word.";
	
	public static final String recognizedHelp = "In this tab you can see all the words\n" +
												"and commands DeskVoice understands.\n\n" +
												"There is an explanation of how this\n" +
												"file works in the firsts lines.\n" +
												"You may edit this file and save it\n" +
												"to test different things.\n\n" +
												"If you just want to add new\n" +
												"commands is recomended to do\n" +
												"it from the 'Add Command' menu.";
	
	public static final String pronunciationHelp = "In this section you can search for any word, and if it is in the dictionary " +
													"the program will give you the pronunciation that DeskVoice will understand.";
}

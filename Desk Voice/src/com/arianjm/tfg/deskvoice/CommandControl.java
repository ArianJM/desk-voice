package com.arianjm.tfg.deskvoice;

import java.awt.EventQueue;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.arianjm.tfg.deskvoice.Command.Category;
import com.arianjm.tfg.deskvoice.ui.DeskVoice;

import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;

public class CommandControl implements Runnable{

	static {
		if(System.getProperty("sun.arch.data.model").equals("32")){
			System.loadLibrary("deskvoice32");
			System.out.println("Loaded 32 bit version");
		}else if(System.getProperty("sun.arch.data.model").equals("64")){
			System.loadLibrary("deskvoice64");
			System.out.println("Loaded 64 bit version");
		}else System.out.println("Couldn't determine if JVM is 32 or 64 bit");
	}
	
	//commandsDictionary: Palabra con objeto Command correspondiente
	private static ConcurrentHashMap<String, Command> commandsDictionary = new ConcurrentHashMap<String, Command>();
	//dictionaryWords: Mapa palabra y pronunciacion
	private static ConcurrentHashMap<String, String> dictionaryWords = new ConcurrentHashMap<String, String>(150000, 0.9f);
	private static final String sep = File.separator;
	private static final String directory = System.getProperty("user.dir");
	private static String recognizedWords = directory+sep+"config"+sep+"accepted-words.txt";
	private static String wordsDictionary = directory+sep+"config"+sep+"dictionary";
	private static String grammar = directory+sep+"config"+sep+"grammar.gram";
	private static Executor ex = new Executor();
	private static GrammarBuilder gb = new GrammarBuilder();
	
	private ConfigurationManager cm;
	private Microphone mic;
	private Recognizer recognizer;
	private static Robot robot;

	public CommandControl(String[] args){
		System.setProperty("file.encoding", "UTF-8");
		File configFile = new File(directory+sep+"config"+sep+"command.config.xml");
		cm = new ConfigurationManager(configFile.getAbsolutePath());
		
		recognizer = (Recognizer) cm.lookup("recognizer");
		recognizer.allocate();
		
		mic = (Microphone) cm.lookup("microphone");
		try {
			robot = new Robot();
			initializeRecognizedWords();
			initializeDictionaryMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void reinitializeSphinx(){
		gb = new GrammarBuilder();
		recognizer.deallocate();

		File configFile = new File(directory+sep+"config"+sep+"command.config.xml");
		cm = new ConfigurationManager(configFile.getAbsolutePath());
		
		recognizer = (Recognizer) cm.lookup("recognizer");
		recognizer.allocate();
		mic = (Microphone) cm.lookup("microphone");
		try {
			initializeRecognizedWords();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Reinitialized");
	}
	
	public void run(){
		DeskVoice.setRecording(true);
		String transcript = "";
		if(recognizer.getState() != Recognizer.State.READY){
			setTranscript("An error has occurred, please restart DeskVoice");
			return;
		}
		mic.clear();
		mic.startRecording();
		setTranscript("Start speaking");
		while(!transcript.equalsIgnoreCase("stop recording") && !transcript.equalsIgnoreCase("quit desk voice")
				&& !transcript.equalsIgnoreCase("quit") && !transcript.equalsIgnoreCase("exit")){
			
			System.out.println("Start speaking. Say 'Quit' to quit.");
			Result result = this.recognizer.recognize();
			
			if(result != null && !result.getBestFinalResultNoFiller().isEmpty()){
				transcript = result.getBestFinalResultNoFiller();
				System.out.println("You said: '" + transcript + "'");
				analize(transcript);
			}else{
				transcript = "Nothing recognized";
				System.out.println(transcript);
			}
			setTranscript(transcript);
		}
		mic.stopRecording();
		DeskVoice.setRecording(false);
	}
	
	private void setTranscript(String transcript){
		class SetTranscript implements Runnable {
			String transcript;
			SetTranscript(String trs) { transcript = trs;}
			
			public void run(){
				try {
					String spoken = transcript.substring(0, 1).toUpperCase() + transcript.substring(1);
					DeskVoice.setTranscript(spoken);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		EventQueue.invokeLater(new SetTranscript(transcript));
	}
	
	/**
	 * Analizes what you say and acts acordingly
	 * @param transcript
	 */
	public static void analize(String transcript){
		/*
		 * TODO: Cambiar este método para que tan solo
		 * divida la transcripción por cada espacio 
		 */
		Enumeration<String> keys = commandsDictionary.keys();
		while(keys.hasMoreElements()){
			String actualElement = keys.nextElement();
			if(transcript.contains(actualElement.toLowerCase())){
				Command com = commandsDictionary.get(actualElement);
				if(com.hasExecutable()){
					System.out.println("Attempting to execute '"+com.getExecute()+"'");
					ex.execute(com);
					break;
				}else if(com.isAction()){
					multimediaKey(com.getActionKey());
				}else if(com.isShortCut()){
					List<Integer> shortcuts = com.getShortcuts();
					for(int shortcut: shortcuts) robot.keyPress(shortcut);
					try {Thread.sleep(333);}
					catch (InterruptedException e) {e.printStackTrace();}
					for(int shortcut: shortcuts) robot.keyRelease(shortcut);
				}
			}
		}
	}

	public static native void multimediaKey(int key);
	public static native void press(int key);
	public static native void release(int key);
	
	
	public static void initializeDictionaryMap(){
		File dictionaryFile = new File(wordsDictionary);
		try{
			System.out.println("Reading dictionary file");
			BufferedReader dictReader = new BufferedReader(new InputStreamReader(new FileInputStream(dictionaryFile), "UTF-8"));
			String line = dictReader.readLine();
			while(line != null){
				String[] splitted = line.split("[\\s]+", 2);
				dictionaryWords.put(splitted[0], splitted[1]);
				line = dictReader.readLine();
			}
			System.out.println("There are "+dictionaryWords.size()+" words in the dictionary");
			dictReader.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Initializes data
	 * @throws Exception
	 */
	public static void initializeRecognizedWords() throws Exception{
		File recognizedFile = new File(recognizedWords);
		BufferedReader recognizedReader = new BufferedReader(new InputStreamReader(new FileInputStream(recognizedFile), "UTF-8"));
		try{
			String line = recognizedReader.readLine();
			System.out.println("Reading accepted words File");

			while(line != null){
				if(line.startsWith("#") || line.isEmpty() || line.startsWith("-")){
					line = recognizedReader.readLine();
					continue;
				}
				String[] splitted = line.split("[\\s]*[>][\\s]*");
				if(splitted.length<2 || splitted.length>3){
					line = recognizedReader.readLine();
					continue;
				}
				Category cat = null;
				if(splitted[1].equalsIgnoreCase("program")) cat = Category.Program;
				else if(splitted[1].equalsIgnoreCase("action")) cat = Category.Action;
				else if(splitted[1].equalsIgnoreCase("command")) cat = Category.Command;
				else if(splitted[1].equalsIgnoreCase("shortcut")) cat = Category.Shortcut;
				else System.out.println("Problem defining category");
				
				if(splitted.length == 3){
						Command com = new Command(splitted[0], cat, splitted[2]);
						commandsDictionary.put(splitted[0], com);
				}else if(splitted.length == 2){
						Command com = new Command(splitted[0], cat);
						commandsDictionary.put(splitted[0], com);
				}

				line = recognizedReader.readLine();
			}
		}finally{
			recognizedReader.close();
		}
		System.out.println("There are "+commandsDictionary.size()+" recognized words in the program");
	}

	
	/**
	 * With proper data, adds a command to the recognized ones
	 */
	public void addCommand(String name, Command.Category cat, String command){
		if(name.isEmpty()) return;
		String[] words = name.split("[\\s]+");
		for(String word : words){
			if(dictionaryWords.get(word.toUpperCase()) == null){
				notInDictionary(word);
				return;
			}
		}
		try{
			switch(cat){
			case Program:
				if(command.isEmpty() || command.equals("No program selected")){
					System.out.println("No command selected");
					return;
				}
			break;
			case Command:
				if(command.isEmpty() || command.equals("No program selected")){
					System.out.println("No command selected");
					return;
				}
			break;
			case Shortcut:
				if(command.isEmpty()){
					System.out.println("No shortcut selected");
					return;
				}
			break;
			default:
				break;
			}
			String line = "\n"+name+" > " + cat.toString() + " > " + command;
			FileWriter fstream = new FileWriter(recognizedWords, true);
			BufferedWriter wfile = new BufferedWriter(fstream);
			wfile.write(line);
			wfile.close();
			System.out.println("Command Added: "+line);
			reinitializeSphinx();
		}catch(Exception e){
			System.err.println("Error adding command: "+e.getMessage());
		}
	}

	private void notInDictionary(String word){
		class NotInDictionary implements Runnable {
			String word;
			NotInDictionary(String w) { word = w;}
			
			public void run(){
				try {
					DeskVoice.notInDictionary(word);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		EventQueue.invokeLater(new NotInDictionary(word));
	}
	
	/**
	 * Prepares to close the program
	 */
	public void close() {
		System.out.println("Deallocating");
		recognizer.deallocate();
		System.out.println("Recognizer deallocated");
	}

	public String getGrammar() {
		File grammarFile = new File(grammar);
		String returnStr = "Error";
		try{
			BufferedReader grammarReader = new BufferedReader(new FileReader(grammarFile));
			String line = grammarReader.readLine();
			returnStr = "";
			while(line != null){
				returnStr += line+"\n";
				line = grammarReader.readLine();
			}
			grammarReader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return returnStr;
	}

	public void setGrammar(String text) {
		try{
			FileWriter fstream = new FileWriter(grammar);
			BufferedWriter wfile = new BufferedWriter(fstream);
			wfile.write(text);
			wfile.close();
			System.out.println("New grammar Setted:\n");
			System.out.println(text);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public String getRecognized() {
		File recognizedFile = new File(recognizedWords);
		String returnStr = "Error";
		try{
			BufferedReader recognizedReader = new BufferedReader(new FileReader(recognizedFile));
			String line = recognizedReader.readLine();
			returnStr = "";
			while(line != null){
				returnStr += line+"\n";
				line = recognizedReader.readLine();
			}
			recognizedReader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return returnStr;
	}

	public void setRecognized(String text) {
		try{
			FileWriter fstream = new FileWriter(recognizedWords);
			BufferedWriter wfile = new BufferedWriter(fstream);
			wfile.write(text);
			wfile.close();
			System.out.println("New recognized words setted:\n");
			reinitializeSphinx();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public ConcurrentHashMap<String, Command> getCommandsDictionary(){
		return commandsDictionary;
	}

	public String getPronunciation(String word) {
		System.out.println("Searching: "+word);
		word = word.toUpperCase();
		if(dictionaryWords.containsKey(word)){
			return dictionaryWords.get(word);
		}
		return "The word wasn't found";
	}
}

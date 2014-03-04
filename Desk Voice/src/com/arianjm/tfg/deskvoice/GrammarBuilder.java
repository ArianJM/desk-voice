package com.arianjm.tfg.deskvoice;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class GrammarBuilder {
	private static final String sep = File.separator;
	private static final String directory = System.getProperty("user.dir");
	private static String acceptedWordsPath = directory+sep+"config"+sep+"accepted-words.txt";
	private static String grammarPath = directory+sep+"config"+sep+"grammar.gram";
	private static HashMap<String, ArrayList<String>> rulesMap = new HashMap<String, ArrayList<String>>();
	private static HashMap<String, ArrayList<String>> rulesOptionals = new HashMap<String, ArrayList<String>>();
	private static String header = "#JSGF V1.0;\n"
									+"/**\n"
									+" * Automatically created by DeskVoice,\n"
									+" * any change here might be undone\n"
									+" * next time you open DeskVoice.\n"
									+" */\n\n"
									+"grammar gram;\n\n";

	public GrammarBuilder(){
		this(acceptedWordsPath);
	}

	public GrammarBuilder(String acceptedWords){
		setAcceptedWords(acceptedWords);
		initializeRulesMap();
		for(String key: rulesMap.keySet()){
			println("<"+key+">: "+rulesMap.get(key));
		}
		buildGrammarFile();
	}
	
	private static void initializeRulesMap(){
		File acceptedWordsFile = new File(acceptedWordsPath);
		String readLine = "";
		rulesMap.clear();
		rulesOptionals.clear();
		try{
			BufferedReader bf = new BufferedReader(new FileReader(acceptedWordsFile));
			ArrayList<String> ruleValues;
			
			while((readLine = bf.readLine()) != null){
				if(readLine.startsWith("#") || readLine.isEmpty()) continue;
				if(readLine.startsWith("-")){
					String[] splittedKey = readLine.split("[-][\\s]*");
					if(splittedKey.length == 2){
						ArrayList<String> rule = getKeyAndAuxiliars(splittedKey[1]);
						rulesMap.put(rule.get(0), new ArrayList<String>());
						ArrayList<String> optionals = new ArrayList<String>();
						
						for(int i=1 ; i<rule.size() ; i++) optionals.add(rule.get(i));
						
						rulesOptionals.put(rule.get(0), optionals);
					}
				}else{
					String[] splittedValue = readLine.split("[\\s]*[>][\\s]*");
					if(splittedValue.length<2 || splittedValue.length>3) continue;
					ruleValues = rulesMap.get(splittedValue[1]);
					ruleValues.add(splittedValue[0]);
					rulesMap.put(splittedValue[1], ruleValues);
				}
			}
			bf.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void buildGrammarFile(){
		File grammarFile = new File(grammarPath);
		try{
			BufferedWriter bf = new BufferedWriter(new FileWriter(grammarFile));
			bf.write(header);
			bf.write("public <quit> = ( Quit | Exit | Stop recording | Quit desk voice );\n");
			for(String key : rulesMap.keySet()){
				String toWrite = "public <"+key+"Rule> = ";
				ArrayList<String> optionals = rulesOptionals.get(key);
				for(int i=0 ; i<optionals.size() ; i++){
					if(i == 0) toWrite+="[";
					if(i == optionals.size()-1) toWrite+=optionals.get(i)+"] ";
					else toWrite+=optionals.get(i)+" | ";
				}
				toWrite+="<"+key+">;\n";
				bf.write(toWrite);
			}
			
			int nRules, cont = 0;
			boolean hasRules;
			for(String key: rulesMap.keySet()){
				hasRules=false;
				String toWrite = "<"+key+"> = (";
				
				nRules = rulesMap.get(key).size();
				if(nRules > 0) hasRules = true;
				cont = 0;
				for(String rule: rulesMap.get(key)){
					cont++;
					if(nRules == cont) toWrite+=rule+");\n";	//Ultima regla
					else toWrite+=rule+" | ";	//No es la ultima regla
				}
				if(hasRules) bf.write(toWrite);
				else bf.write("<"+key+"> = <VOID> ;\n");
			}
			bf.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}


	private static ArrayList<String> getKeyAndAuxiliars(String key) {
		String[] keySplit = key.split("[\\s,]+");
		ArrayList<String> result = new ArrayList<String>();
		for(String sp : keySplit){
			sp = sp.replace("(", "");
			sp = sp.replace(")", "");
			result.add(sp);
		}
		return result;
	}

	/**
	 * @return the acceptedWords
	 */
	public static String getAcceptedWords() {
		return acceptedWordsPath;
	}

	/**
	 * @param acceptedWords the acceptedWords to set
	 */
	public static void setAcceptedWords(String acceptedWords) {
		GrammarBuilder.acceptedWordsPath = acceptedWords;
	}

	/**
	 * @return the grammarPath
	 */
	public static String getGrammarPath() {
		return grammarPath;
	}

	/**
	 * @param grammarPath the grammarPath to set
	 */
	public static void setGrammarPath(String grammarPath) {
		GrammarBuilder.grammarPath = grammarPath;
	}
	
	private static void println(String print){
		System.out.println("Grammar Builder: "+print);
	}
}

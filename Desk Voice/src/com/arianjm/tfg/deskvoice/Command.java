package com.arianjm.tfg.deskvoice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Command {

	public enum Category{
		Program, Command, Shortcut, Action
	}
	
	private String command;
	private HashSet<String> categories;
	private Category category;
	private String execute;
	private List<Integer> shortcuts = new ArrayList<Integer>();
	
	public Command(String com, Category cat){
		setCommand(com);
		setCategory(cat);
		setExecute("");
	}
	public Command(String com, Category cat, String data){
		setCommand(com);
		setCategory(cat);
		switch(cat){
		case Program:
			setExecute(data);
			break;
		case Command:
			setExecute(data);
			break;
		case Shortcut:
			setExecute("");
			String[] shortStrings = data.split("\\s");
			for(String s:shortStrings)
				shortcuts.add(Integer.parseInt(s));
			//System.out.println("For: "+getCommand()+" shortcut is: "+shortcuts.toString());
			break;
		}
	}

	public String toString(){
		String str = getCommand();
		if(!getExecute().isEmpty()) str+=" ("+getExecute()+"): ";
		else str+=": ";
		str+=getCategory();
		return str;
	}

	public boolean hasExecutable(){
		if(getExecute().isEmpty()) return false;
		else return true;
	}
	public boolean isCommand(){
		if(category.equals(Category.Command)) return true;
		else return false;
	}
	public boolean isAction(){
		if(category.equals(Category.Action)) return true;
		else return false;
	}
	public boolean isProgram(){
		if(category.equals(Category.Program)) return true;
		else return false;
	}
	public boolean isShortCut(){
		if(category.equals(Category.Shortcut)) return true;
		else return false;
	}
	
	public String getCommand(){
		return this.command;
	}
	public String getCategory(){
		for(String cat : this.categories)
			return cat;
		return null;
	}
	public String getExecute(){
		return this.execute;
	}
	public List<Integer> getShortcuts(){
		return this.shortcuts;
	}
	public void setCommand(String com){
		this.command = com;
	}
	public void setCategory(Category cat){
		this.category = cat;
	}
	public void setExecute(String exe){
		this.execute = exe;
	}
	public int getActionKey() {
		switch(command.toLowerCase()){
		case "mute":
			return 0xAD;
		case "volume up":
			return 0xAF;
		case "volume down":
			return 0xAE;
		case "play":
			return 0xB3;
		case "pause":
			return 0xB3;
		case "next":
			return 0xB0;
		case "previous":
			return 0xB1;
		case "stop":
			return 0xB2;
		case "back":
			return 0xA6;
		case "forward":
			return 0xA7;
		case "refresh":
			return 0xA8;
		case "search":
			return 0xAA;
		}
		return 0;
	}
}

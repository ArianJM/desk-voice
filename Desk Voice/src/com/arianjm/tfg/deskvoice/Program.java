package com.arianjm.tfg.deskvoice;

public class Program{
	private String program;
	private String command;

	public Program(){
		setProgram("");
		setCommand("");
	}
	public Program(String program, String command){
		setProgram(program);
		setCommand(command);
	}

	public String toString(){
		return "Program: "+getProgram()+", Command: "+getCommand();
	}

	public String getProgram(){
		return this.program;
	}
	public String getCommand(){
		return this.command;
	}
	public void setProgram(String prog){
		this.program = prog;
	}
	public void setCommand(String com){
		this.command = com;
	}
}

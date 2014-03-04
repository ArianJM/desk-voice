package com.arianjm.tfg.deskvoice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Executor{

	private Runtime rt;
	private Process process;
	private BufferedReader br;

	public Executor(){
		rt = Runtime.getRuntime();
	}
	
	public void execute(String command){
		try{
			String[] commandArray = {"cmd", "/c", command};
			process = rt.exec(commandArray);
			br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void execute(Command command){
		try{
			String[] commandArray;
			if(command.isCommand()){
				commandArray = new String[] {"cmd", "/c", "\""+command.getExecute()+"\""};
			}else{
				commandArray = new String[] {"cmd", "/c", command.getExecute()};
			}
			process = rt.exec(commandArray);
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public String readLine(){
		try {
			return br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Integer destroyProcess(){
		RuntimeMXBean rtb = ManagementFactory.getRuntimeMXBean();
		String processName = rtb.getName();
		System.out.println(processName);
		Integer pid = tryPattern1(processName);

		return pid;
		//rt.exec("taskkill /f ")
	}
	
	private static Integer tryPattern1(String processName) {
	    Integer result = null;
			
	    /* tested on: */
	    /* - windows xp sp 2, java 1.5.0_13 */
	    /* - mac os x 10.4.10, java 1.5.0 */
	    /* - debian linux, java 1.5.0_13 */
	    /* all return pid@host, e.g 2204@antonius */
			
	    Pattern pattern = Pattern.compile("^([0-9]+)@.+$", Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(processName);
	    if (matcher.matches()) {
		result = new Integer(Integer.parseInt(matcher.group(1)));
	    }
	    return result;
	 }
	
	/*public String getCommand(){
		return command;
	}

	public setCommand(String command){
		this.command = command;
	}*/
}

package edu.arizona.sirls.util;

public class ExecCommmand {
	
	/**
	 * create table files and return the status
	 * @param cmd
	 * @return
	 */
    public int execShellCmd(String cmd) {
    	int exitValue = -1;
        try {  
            Runtime runtime = Runtime.getRuntime();  
            Process process = runtime.exec(new String[] { "/bin/sh", "-c", cmd });  
            exitValue = process.waitFor();
            if (exitValue == 0) {
            	System.out.println("Run command successfully.");
            } else {
            	System.out.println("Run command '" + cmd +
            			"' failed.");
            }
        } catch (Exception e) {  
            System.out.println(e);  
        }
        return exitValue;
    }  
}

package edu.arizona.biosemantics.oto.oto.mail;

import java.util.ArrayList;


public class Test {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		NotifyEmail send = new NotifyEmail();
		ArrayList<String> files = new ArrayList<String>();
		files.add("test1");
		files.add("test2");
		send.sendNewGlossaryCommitNotification("huangfengq@gmail.com", "test",
				files);

	}
}

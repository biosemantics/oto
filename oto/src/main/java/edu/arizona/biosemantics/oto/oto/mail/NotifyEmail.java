package edu.arizona.biosemantics.oto.oto.mail;

import java.io.IOException;
import java.security.Security;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import edu.arizona.biosemantics.oto.oto.Configuration;

public class NotifyEmail {
	private String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
	private Configuration configuration;

	public NotifyEmail() throws IOException {
		this.configuration = Configuration.getInstance();
	}

	public Boolean sendNewRegistrationNotification(String username,
			String useremail) throws Exception {
		Boolean returnvalue = false;
		String sendto = configuration.getNewRegistrationRecipient();
		String content = "Dear OTO Administrator, <br></br> "
				+ "There is a new registration at OTO website that requires your action. <br>"
				+ " User name: " + username + "<br> User email: " + useremail
				+ "<br></br>OTO Notification System";
		content = packContent(content);
		String subject = "New registration in OTO";
		returnvalue = sendEmail(sendto, subject, content);
		return returnvalue;
	}

	protected String packContent(String content) {
		String content_pre = "<html><head></head><body><center>"
				+ "<table width=98% style='border: 1px solid #5599FF'>"
				+ "<tr><td>";
		String content_pro = "</td></tr></table></center></body></html>";

		return content_pre + content + content_pro;
	}

	public boolean sendPasswordInfo(String email, String fname,
			String newPassword) throws Exception {
		boolean rv = false;
		String sendto = email;
		String content = "Dear "
				+ fname
				+ ", <br></br> "
				+ "Your password of OTO has been reset to be: "
				+ newPassword
				+ " <br></br>"
				+ "You may change your password in 'Settings' after you log in <a href='http://biosemantics.arizona.edu/ONTNEW/'>OTO</a><br>"
				+ "<br></br>OTO Notification System";
		content = packContent(content);
		String subject = "OTO: password reset";
		rv = sendEmail(sendto, subject, content);

		return rv;
	}

	public boolean sendNewGlossaryCommitNotification(String sendTo,
			String dataset, ArrayList<String> files) throws Exception {
		boolean rv = false;
		String sendto = sendTo;
		String content = "Dear OTO Administrator, <br></br><br></br> "
				+ "There is a new glossary commit on GitHub. <br>"
				+ "&nbsp;&nbsp;Datast Name: " + dataset + "<br>"
				+ "&nbsp;&nbsp;Updated Files: <br>";
		for (String file : files) {
			content += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + file + "<br>";
		}

		content += "<br/>Click <a href='https://github.com/biosemantics/glossaries/tree/development'>here</a> to see the new commit now. <br/>";

		content += "<br></br><br/>OTO Notification System";
		content = packContent(content);
		String subject = "OTO: New " + dataset + " commit on GitHub";
		rv = sendEmail(sendto, subject, content);

		return rv;
	}

	public Boolean sendEmail(String receipient, String subject, String content)
			throws Exception {

		Boolean returnvalue = false;
		String sendto = receipient;

		Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
		// Get a Properties object
		Properties props = System.getProperties();
		props.setProperty("mail.smtp.host", configuration.getOtoEmailSmtp());
		props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
		props.setProperty("mail.smtp.socketFactory.fallback", "false");
		props.setProperty("mail.smtp.port", "465");
		props.setProperty("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.auth", "true");

		Session session = Session.getDefaultInstance(props,
				new Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(configuration.getOtoEmailUser(), configuration.getOtoEmailPassword());
					}
				});

		Message msg = new MimeMessage(session);
		InternetAddress[] address = null;

		if ((configuration.getOtoEmailShowname() == null) || (configuration.getOtoEmailShowname().equals(""))) {
			msg.setFrom(new InternetAddress(configuration.getOtoEmailDisplayAddress()));
		} else {
			msg.setFrom(new InternetAddress(configuration.getOtoEmailDisplayAddress(), configuration.getOtoEmailShowname()));
		}

		address = InternetAddress.parse(sendto, false);
		msg.setRecipients(Message.RecipientType.TO, address);
		msg.setSubject(subject);
		msg.setSentDate(new Date());
		Multipart mp = new MimeMultipart();
		MimeBodyPart mbp = new MimeBodyPart();
		mbp.setContent(content, "text/html;charset=GB2312");
		mp.addBodyPart(mbp);
		msg.setContent(mp);
		Transport transport = session.getTransport("smtp");

		try {
			transport.connect(configuration.getOtoEmailSmtp(), configuration.getOtoEmailUser(), configuration.getOtoEmailPassword());
			transport.sendMessage(msg, msg.getAllRecipients());
			returnvalue = true;
		} catch (Exception e) {
			System.out.println("Error in sending email: " + e.getMessage());
		} finally {
			transport.close();
		}
		return returnvalue;
	}
}

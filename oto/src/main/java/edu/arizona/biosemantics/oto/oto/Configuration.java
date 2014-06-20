package edu.arizona.biosemantics.oto.oto;
//
import java.io.IOException;
import java.util.Properties;

public class Configuration {

	private static Configuration instance;

	public static Configuration getInstance() throws IOException {
		if(instance == null)
			instance = new Configuration();
		return instance;
	}

	private int minPasswordLength;
	private String otoEmailUser;
	private String otoEmailPassword;
	private String otoEmailDisplayAddress;
	private String otoEmailSmtp;
	private String otoEmailShowname;
	private String newRegistrationRecipient;
	private String databaseName;
	private String databaseUser;
	private String databasePassword;
	private String bioportalUrl;
	private String bioportalUserId;
	private String bioportalApiKey;
	private String gitUser;
	private String gitPassword;
	private String gitAuthorName;
	private String gitAuthorEmail;
	private String gitCommitterName;
	private String gitCommitterEmail;
	private String gitRepository;
	private String gitLocalPath;
	private String glossaryFilePath;
	private String glossaryCommitRecipient;
	private String sourceFilePath;
	private String userFilePath;
	private String url;
	private String toPrintTime;
	private String rowPerPage;
	private String mysqlDumpLocation;
	private String backupFilepath;
	private String downloadUrl;
	private String os;
	
	public Configuration() throws IOException {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Properties properties = new Properties();
		properties.load(loader.getResourceAsStream("config.properties"));
		this.minPasswordLength = Integer.parseInt(properties.getProperty("MIN_PASSWORD_LENGTH"));
		
		this.otoEmailUser = properties.getProperty("OTO_EMAIL_USER");
		this.otoEmailPassword = properties.getProperty("OTO_EMAIL_PASSWORD");
		this.otoEmailDisplayAddress = properties.getProperty("OTO_EMAIL_DISPLAY_ADDRESS");
		this.otoEmailSmtp = properties.getProperty("OTO_EMAIL_SMTP");
		this.otoEmailShowname = properties.getProperty("OTO_EMAIL_SHOWNAME");
		this.newRegistrationRecipient = properties.getProperty("NEW_REGISTRATION_RECIPIENT");
		
		this.databaseName = properties.getProperty("databaseName");
		this.databaseUser = properties.getProperty("databaseUser");
		this.databasePassword = properties.getProperty("databasePassword");
 
		this.bioportalUrl = properties.getProperty("bioportalUrl");
		this.bioportalUserId = properties.getProperty("bioportalUserId");
		this.bioportalApiKey = properties.getProperty("bioportalApiKey");

		this.gitUser = properties.getProperty("gitUser");
		this.gitPassword = properties.getProperty("gitPassword");
		this.gitAuthorName = properties.getProperty("gitAuthorName");
		this.gitAuthorEmail = properties.getProperty("gitAuthorEmail");
		this.gitCommitterName = properties.getProperty("gitCommitterName");
		this.gitCommitterEmail = properties.getProperty("gitCommitterEmail");
		this.gitRepository = properties.getProperty("gitRepository");
		this.gitLocalPath = properties.getProperty("gitLocalPath");

		this.glossaryFilePath = properties.getProperty("GLOSSARY_FILE_PATH");
		this.glossaryCommitRecipient = properties.getProperty("GLOSSARY_COMMIT_RECIPIENT");
		this.sourceFilePath = properties.getProperty("SOURCE_FILE_PATH");
		this.userFilePath = properties.getProperty("USER_FILE_PATH");
		this.url = properties.getProperty("URL");
		this.toPrintTime = properties.getProperty("TO_PRINT_TIME");
		this.rowPerPage = properties.getProperty("ROW_PER_PAGE");
		this.mysqlDumpLocation = properties.getProperty("MYSQLDUMP_LOCATION");
		this.backupFilepath = properties.getProperty("BACKUP_FILE_PATH");
		this.downloadUrl = properties.getProperty("DOWNLOAD_URL");
		this.os = properties.getProperty("OS");
	}

	public int getMinPasswordLength() {
		return minPasswordLength;
	}

	public String getOtoEmailUser() {
		return otoEmailUser;
	}

	public String getOtoEmailPassword() {
		return otoEmailPassword;
	}

	public String getOtoEmailDisplayAddress() {
		return otoEmailDisplayAddress;
	}

	public String getOtoEmailSmtp() {
		return otoEmailSmtp;
	}

	public String getOtoEmailShowname() {
		return otoEmailShowname;
	}

	public String getNewRegistrationRecipient() {
		return newRegistrationRecipient;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public String getDatabaseUser() {
		return databaseUser;
	}

	public String getDatabasePassword() {
		return databasePassword;
	}

	public String getBioportalUrl() {
		return bioportalUrl;
	}

	public String getBioportalUserId() {
		return bioportalUserId;
	}

	public String getBioportalApiKey() {
		return bioportalApiKey;
	}

	public String getGitUser() {
		return gitUser;
	}

	public String getGitPassword() {
		return gitPassword;
	}

	public String getGitAuthorName() {
		return gitAuthorName;
	}

	public String getGitAuthorEmail() {
		return gitAuthorEmail;
	}

	public String getGitCommitterName() {
		return gitCommitterName;
	}

	public String getGitCommitterEmail() {
		return gitCommitterEmail;
	}

	public String getGitRepository() {
		return gitRepository;
	}

	public String getGitLocalPath() {
		return gitLocalPath;
	}

	public String getGlossaryFilePath() {
		return glossaryFilePath;
	}

	public String getGlossaryCommitRecipient() {
		return glossaryCommitRecipient;
	}

	public String getSourceFilePath() {
		return sourceFilePath;
	}

	public String getUserFilePath() {
		return userFilePath;
	}

	public String getUrl() {
		return url;
	}

	public String getToPrintTime() {
		return toPrintTime;
	}

	public String getRowPerPage() {
		return rowPerPage;
	}

	public String getMysqlDumpLocation() {
		return mysqlDumpLocation;
	}

	public String getBackupFilePath() {
		return backupFilepath;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public String getOs() {
		return os;
	}
		
}

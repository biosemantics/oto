package edu.arizona.biosemantics.oto.common.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import edu.arizona.biosemantics.oto.model.GlossaryDownload;
import edu.arizona.biosemantics.oto.model.TermCategory;
import edu.arizona.biosemantics.oto.model.TermSynonym;

import org.apache.log4j.Logger;
import au.com.bytecode.opencsv.CSVReader;


public class FileReader {

	private static final Logger LOGGER = Logger.getLogger(FileReader.class);

	/**
	 * This method gets the file information from the transformed folder
	 */
	public String getFileInfo(String fileName) {
		File file = new File(fileName);
		StringBuilder text = new StringBuilder();
		String NL = System.getProperty("line.separator");
		Scanner scanner = null;

		try {
			scanner = new Scanner(new FileInputStream(file));
			while (scanner.hasNextLine()) {
				text.append(scanner.nextLine() + NL);
			}
		} catch (Exception exe) {
			exe.printStackTrace();
			LOGGER.error("error reading file ", exe);
		} finally {
			scanner.close();
		}

		return text.toString();
	}

	/**
	 * This method reads a glossary file of some version in csv format and constructs a GlossaryDownload object
	 * and serialize and save the object to the given location. 
	 * 
	 * Typical use case: 
	 * get glossary from https://raw.githubusercontent.com/biosemantics/glossaries/master/Plant/latest/Plant_glossary_term_category.csv
	 * Remove all comments (starting with #) and the header (first row in content)
	 * Save the csv file at inputPath
	 * Headers in csv file: "term","category","hasSyn","sourceDataset","termID"
	 * get synonym from https://github.com/biosemantics/glossaries/blob/master/Plant/latest/Plant_glossary_syns.csv
	 * Remove all comments (starting with #) and the header (first row in content)
	 * Save the csv file at inputPath
	 * Fields in csv file:"term","category","synonym","termID"
	 */

	public void serializeCSVGlossary (String categoryInputPath, String synonymInputPath, String outputPath, String version){
		GlossaryDownload glossary = new GlossaryDownload();
		glossary.setVersion(version);
		List<TermCategory> termCategories = new ArrayList<TermCategory>();
		List<TermSynonym> termSynonyms = new ArrayList<TermSynonym>();
		CSVReader reader = null;
		ObjectOutputStream out = null; 
		try {
			reader = new CSVReader(new java.io.FileReader(categoryInputPath));
			List<String[]> lines = reader.readAll();
			for(String[] line : lines) {
				String term = line[0].trim().toLowerCase();
				String category = line[1].trim().toLowerCase();
				String hasSyn = line[2].trim().toLowerCase(); //"0" or "1"
				String termID = line[3].trim().toLowerCase();
				TermCategory TCEntry = new TermCategory(term, category, 
						hasSyn.compareTo("0")==0? Boolean.FALSE : Boolean.TRUE,
								"", termID);
				termCategories.add(TCEntry);
			}
			reader = new CSVReader(new java.io.FileReader(synonymInputPath));
			lines = reader.readAll();
			for(String[] line : lines) {
				String term = line[0].trim().toLowerCase();
				String category = line[1].trim().toLowerCase();
				String synonym = line[2].trim().toLowerCase(); 
				String termID = line[3].trim().toLowerCase();
				TermSynonym TSEntry = new TermSynonym(term, category, synonym, termID);
				termSynonyms.add(TSEntry);
			}	
			glossary.setTermCategories(termCategories);
			glossary.setTermSynonyms(termSynonyms);

			out = new ObjectOutputStream(new FileOutputStream(outputPath));  
			out.writeObject(glossary); 
		}catch(Exception exe){
			exe.printStackTrace();
			LOGGER.error("error reading glossary file ", exe);
		}finally {
			if(reader !=null){
				try{
					reader.close();
				}catch(IOException ex){}
			}
			if(out !=null){
				try{
					out.close();
				}catch(IOException ex){}
			} 
		}
	}

	public static void main(String[] argv){
		//try 
		FileReader reader = new FileReader();
		String categoryInputPath = "C:/Users/hongcui/git/charaparser-web/glossarydownload/Plant_glossary_term_category.csv";
		String synonymInputPath = "C:/Users/hongcui/git/charaparser-web/glossarydownload/Plant_glossary_syns.csv";
		String outputPath = "C:/Users/hongcui/git/charaparser-web/glossarydownload/GlossaryDownload.Plant.ser";
		String version = "latest";
		reader.serializeCSVGlossary(categoryInputPath, synonymInputPath, outputPath, version);
		
		try{
		FileInputStream file = new FileInputStream(outputPath); 
        ObjectInputStream in = new ObjectInputStream(file); 
          
        // Method for deserialization of object 
        GlossaryDownload gloss = (GlossaryDownload)in.readObject(); 
        System.out.println("deserialized glossary :"+gloss.getVersion());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}

package fuzzy.fuzzification;

import java.io.FileOutputStream;
import java.io.IOException;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import fuzzy.FuzzySet;

public class FuzzyAttribute {
	String table;
	String name;
	FuzzySet[] fuzzySet;
	
	public int[] RetrieveData(){
		int[] result = new int[10];
		//result[] = "select distinct name from table"
		return result;
	}
	
	public FuzzySet[] CreateFuzzyRangesBasedOnCluster(){
		//To Do
		return null;
	}
	
	public FuzzySet[] CreateFuzzyRangesBasedOnGA(){
		//To Do
		return null;
	}
	public void outFuzzySetsToXML() throws SAXException, IOException
	{
		FileOutputStream fos = new FileOutputStream("c:\\QueryBuilderTemp\\fuzzy.xml");
		// XERCES 1 or 2 additionnal classes.
		OutputFormat of = new OutputFormat("XML","ISO-8859-1",true);
		of.setIndent(1);
		of.setIndenting(true);
		of.setDoctype(null,"users.dtd");
		XMLSerializer serializer = new XMLSerializer(fos,of);

		// SAX2.0 ContentHandler.
		ContentHandler hd = serializer.asContentHandler();
		hd.startDocument();
		
		// Processing instruction sample.
		//hd.processingInstruction("xml-stylesheet","type=\"text/xsl\" href=\"users.xsl\"");
		
		// USER attributes.
		AttributesImpl atts = new AttributesImpl();
		// USERS tag.
		hd.startElement("","","FUZZIFY",atts);
		// USER tags.
		for (int i=0;i<fuzzySet.length;i++)
		{
		  atts.clear();
		  atts.addAttribute("","","LABEL","CDATA",fuzzySet[i].getLabel());
		  hd.startElement("","","TERM",atts);
		  
		  hd.startElement("","","FROM",null);
		  hd.characters(Double.toString(fuzzySet[i].getFrom()).toCharArray(),0,Double.toString(fuzzySet[i].getFrom()).length());
		  hd.endElement("","","FROM");
		  
		  hd.startElement("","","R",null);
		  hd.characters(Double.toString(fuzzySet[i].getR()).toCharArray(),0,Double.toString(fuzzySet[i].getR()).length());
		  hd.endElement("","","R");
		  
		  hd.startElement("","","TO",null);
		  hd.characters(Double.toString(fuzzySet[i].getTo()).toCharArray(),0,Double.toString(fuzzySet[i].getTo()).length());
		  hd.endElement("","","TO");
		  
		  hd.endElement("","","TERM");
		}
		hd.endElement("","","FUZZIFY");
		hd.endDocument();
		fos.close();		
	}	
}

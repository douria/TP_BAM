package jus.aor.mobilagent.hostel;

import java.io.File;
import java.io.IOException;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import Common.Hotel;
import Common._Chaine;
import jus.aor.mobilagent.kernel._Service;

public class Chaine implements _Chaine, _Service {
	
	public Chaine(Object... args) {
		try {
			DocumentBuilder build = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	    	Document document;
			document = build.parse(new File("Repository/"+args[0]));

	    	NodeList hotels = document.getElementsByTagName("Hotel");
	    	for(int i = 0; i<hotels.getLength(); i++) {
	    		NamedNodeMap m = hotels.item(i).getAttributes();
	    		add(m.getNamedItem("name").getNodeValue(), m.getNamedItem("localisation").getNodeValue());
	    	}
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	ArrayList<Hotel> li = new ArrayList<>();
	
	public void add(String name, String localisation) {
		li.add(new Hotel(name, localisation));
	}
	
	@Override
	public List<Hotel> get(String localisation) {
		List<Hotel> filtre = new ArrayList<Hotel>();
		for(Hotel h : this.li)
			if(localisation.equals(h.localisation))
				filtre.add(h);

		return filtre;
	}

	@Override
	public Object call(Object... params) throws IllegalArgumentException {
		return get((String)params[0]);
	}

}

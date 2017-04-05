package jus.aor.mobilagent.hostel;

import java.io.File;
import java.io.IOException;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import Common.Hotel;
import Common.Numero;
import Common._Annuaire;
import Common._Chaine;
import jus.aor.mobilagent.kernel._Service;

public class Annuaire implements _Annuaire, _Service {
	
	public Annuaire(Object... args) {
		try {
			DocumentBuilder build = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	    	Document document;
			document = build.parse(new File("Repository/"+args[0]));

	    	NodeList hotels = document.getElementsByTagName("Telephone");
	    	for(int i = 0; i<hotels.getLength(); i++) {
	    		NamedNodeMap m = hotels.item(i).getAttributes();
	    		add(m.getNamedItem("name").getNodeValue(), m.getNamedItem("numero").getNodeValue());
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

	HashMap<String, String> li = new HashMap<String, String>();
	
	public void add(String name, String num) {
		li.put(name, num);
	}
	
	@Override
	public Numero get(String name) {
		return new Numero(li.get(name));
	}

	@Override
	public Object call(Object... params) throws IllegalArgumentException {
		ArrayList<Numero> ret = new ArrayList<>();
		for(Hotel i : ((ArrayList<Hotel>)params[0])) {
			ret.add(get(i.name));
		}
		return ret;//get(((ArrayList<Hotel>)params[0]).get(0).name);
	}

}

package Server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import Common._Chaine;

public class Server {

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		System.setProperty("java.security.policy","file:./rmipolicy.policy");
		System.setProperty("java.rmi.server.codebase","file:./bin");
	    int port = 1099;
	    // installation d'un securityManager
	    LocateRegistry.createRegistry(port);

    	System.out.println("Mise en place du Security Manager ...");
    	if (System.getSecurityManager() == null) {
    		System.setSecurityManager(new RMISecurityManager());
    	}

    	System.out.println("Serveur lancé");
	    
    	Chaine chaine = new Chaine();
    	DocumentBuilder build = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    	Document document = build.parse(new File("Repository/DataStore/Hotels1.xml"));
    	NodeList hotels = document.getElementsByTagName("hotel");
    	for(int i = 0; i<hotels.getLength(); i++) {
    		NamedNodeMap m = hotels.item(i).getAttributes();
    		chaine.add(m.getNamedItem("name").getNodeValue(), m.getNamedItem("localisation").getNodeValue());
    	}
    	_Chaine skeleton = (_Chaine) UnicastRemoteObject.exportObject((_Chaine)chaine, 1099);
		Naming.rebind("chaine", skeleton);
	    
	    while(true);
	}

}

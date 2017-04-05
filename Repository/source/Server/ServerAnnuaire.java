package Server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
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

import Common._Annuaire;
import Common._Chaine;
import jus.aor.mobilagent.hostel.Annuaire;

public class ServerAnnuaire {

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, AlreadyBoundException {
		System.setProperty("java.security.policy","file:./rmipolicy.policy");
		System.setProperty("java.rmi.server.codebase","file:./bin");
		//System.setProperty("java.rmi.server.hostname","127.0.0.1");
		if(args.length < 1) {
			System.out.println("LookForHotel <localisation de la recherche> <numero de l'hotel>");
			System.exit(1);
		}
		int num = Integer.parseInt(args[1]);
	    int port = 1099 + num;
	    // installation d'un securityManager
	    LocateRegistry.createRegistry(port);

    	System.out.println("Mise en place du Security Manager ...");
    	if (System.getSecurityManager() == null) {
    		System.setSecurityManager(new RMISecurityManager());
    	}

    	System.out.println("Serveur lancé");
	    
    	Annuaire annu = new Annuaire(args);
    	_Annuaire skeleton = (_Annuaire) UnicastRemoteObject.exportObject((_Annuaire)annu, port);
		Naming.bind("rmi://localhost:"+port+"/annuaire", skeleton);
	    //System.out.println("Hotel in Paris loaded: "+chaine.get("Paris").size());
	    //while(true);
	}

}

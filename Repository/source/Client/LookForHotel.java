package Client;
import java.rmi.RMISecurityManager;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;

import Common._Chaine;

/**
 * J<i>ava</i> U<i>tilities</i> for S<i>tudents</i>
 */

/**
 * Représente un client effectuant une requête lui permettant d'obtenir les numéros de téléphone des hôtels répondant à son critère de choix.
 * @author  Morat
 */
public class LookForHotel{
	/** le critère de localisaton choisi */
	private String localisation;
	private int num;
	// ...
	/**
	 * Définition de l'objet représentant l'interrogation.
	 * @param args les arguments n'en comportant qu'un seul qui indique le critère
	 *          de localisation
	 */
	public LookForHotel(String... args){
		if(args.length < 2) {
			System.out.println("LookForHotel <localisation de la recherche>");
			System.exit(1);
		}
		localisation = args[0];
		num = Integer.parseInt(args[1]);
	}
	/**
	 * réalise une intérrogation
	 * @return la durée de l'interrogation
	 * @throws RemoteException
	 */
	public long call() {
		long start = System.currentTimeMillis();
		System.setProperty("java.security.policy","file:./rmipolicy.policy");
		System.setProperty("java.rmi.server.codebase","file:./bin");
		// installation d'un securityManager 
		System.out.println("Mise en place du Security Manager ...");
    	if (System.getSecurityManager() == null) {
    		System.setSecurityManager(new RMISecurityManager());
    	}
		// D�marrage de la communication
    	try{
    		_Chaine cha;
			synchronized (LookForHotel.class){
				Registry registry = LocateRegistry.getRegistry(1099);
	            cha = (_Chaine) registry.lookup("chaine");
				System.out.println("Read");
			}
			System.out.println(cha.get(localisation).size());
		}catch(Throwable e){
			e.printStackTrace();/*synchronized (Customer.class){
				System.out.println("Provider error: " + e);
			}*/
		}
		return System.currentTimeMillis() - start;
	}

	// ...
}

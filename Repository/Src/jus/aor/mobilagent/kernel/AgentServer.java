package jus.aor.mobilagent.kernel;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;


/*AgentServer décrit la partie opérationnelle du fonctionnement de ce serveur.
Elle contient la boucle (méthode run) de réception des agents mobiles.
* La classe Server est une classe qui protège la précédente, permet de gérer
l’AgentServer et fournit les primitives d’ajout d’un service et de déploiement d’un
agent.*/
public class AgentServer implements Runnable {
    //Port on which the AgentServer is running
	private int Port;
	//name of the server
	private String Name;
	// List of services in the AgentService 
	private Map<String, _Service<?>> Services;

	
	protected AgentServer(String name , int port ){
		this.Port=port;
		this.Name=name;
		this.Services = new HashMap<String, _Service<?>>();
	}
	/**
	 * Retourne l'adresse de l'AgentServer
	 *
	 * @return URI de l'AgentServer
	 */
	public URI site() {
		try {
			return new URI("//localhost:" + this.Port);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}
    /**
     * add a service to this agentserver
     * 
     */
	protected void addService(String service_name, _Service<?> service) {
		this.Services.put(service_name, service);
	}
	
	/**
     * get a service to this agentserver
     * 
     */
	protected _Service<?> getService(String service_name) {
		return this.Services.get(service_name);
	}
	
	@Override
	public String toString() {
		return String.format("[AgentServer: name=%s, port=%d]", this.Name, this.Port);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}

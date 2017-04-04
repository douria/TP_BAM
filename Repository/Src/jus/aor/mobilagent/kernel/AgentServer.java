package jus.aor.mobilagent.kernel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;


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
		boolean alive = true;
		try {
			// create a socket Server
			ServerSocket SocketServer = new ServerSocket(this.Port);

			Starter.getLogger().log(Level.INFO, String.format("AgentServer %s started", this));
			while (alive) {
				Starter.getLogger().log(Level.FINE, String.format("AgentServer %s: about to accept", this));
				// Accept on incoming Agents
				Socket SocketClient = SocketServer.accept();
				Starter.getLogger().log(Level.FINE, String.format("AgentServer %s accepted an agent", this));

				// load the repository and the agent
				_Agent wAgent = this.getAgent(SocketClient);
				wAgent.reInit(this, this.Name);

				Starter.getLogger().log(Level.INFO, String.format("AgentServer %s received agent %s", this, wAgent));

				new Thread(wAgent).start();

				SocketClient.close();
			}
			SocketServer.close();
		} catch (IOException aException) {
			Starter.getLogger().log(Level.INFO, String.format("An IO exception occured in %s", this), aException);
		} catch (ClassNotFoundException aException) {
			Starter.getLogger().log(Level.INFO, String.format("A class was not found in %s", this), aException);
		}
		
	}

}

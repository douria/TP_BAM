package jus.aor.mobilagent.kernel;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
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

	/**
	 * Retrieve _Agent from socket
	 * 
	 *@param Socket 
	 * 
	 */
	private _Agent getAgent(Socket Socket) throws IOException, ClassNotFoundException {
		// Creation of BAMClassLoader
		BAMAgentClassLoader ClassLoader = new BAMAgentClassLoader(this.getClass().getClassLoader());

		// Creation of an InputStream, an ObjectInputStream and an
		// AgentInputStream
		InputStream InputStream = Socket.getInputStream();
		ObjectInputStream ObjectInputStream = new ObjectInputStream(InputStream);
		AgentInputStream AgentInputStream = new AgentInputStream(InputStream, ClassLoader);

		// Retrieve the Jar and integrate it
		Jar Jar = (Jar) ObjectInputStream.readObject();
		ClassLoader.integrateCode(Jar);

		// Retrieve the _Agent using the AgentInputStream
		_Agent Agent = (_Agent) AgentInputStream.readObject();

		AgentInputStream.close();
		ObjectInputStream.close();
		InputStream.close();

		return Agent;
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
				// Accept the incoming Agents
				Socket SocketClient = SocketServer.accept();
				//logger keeps a trace of the bahaviour of the app 
				//so we use it to make the connection
				Starter.getLogger().log(Level.FINE, String.format("AgentServer %s accepted an agent", this));

				// load the repository and the agent
				_Agent Agent = this.getAgent(SocketClient);
				//Initialise l'agent lors de son déploiement sur un des serveurs du bus
				Agent.reInit(this, this.Name);
				Starter.getLogger().log(Level.INFO, String.format("AgentServer %s received agent %s", this, Agent));
				//we run the Agent
				new Thread(Agent).start();

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

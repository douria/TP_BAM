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
public class AgentServer extends Thread {
	private boolean isalive;
    //Port on which the AgentServer is running
	private int port;
	//name of the server
	private String name;
	// List of services in the AgentService 
	private Map<String, _Service<?>> services;
	

	
	protected AgentServer(String name , int port ){
		this.port=port;
		this.name=name;
		this.services = new HashMap<String, _Service<?>>();
	}
	/**
	 * Retourne l'adresse de l'AgentServer
	 *
	 * @return URI de l'AgentServer
	 */
	public URI site() {
		URI uri = null;
		try {
			uri = new URI("//localhost:" + this.port);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return uri;
	}
    /**
     * add a service to this agentserver
     * 
     */
	protected void addService(String service_name, _Service<?> service) {
		this.services.put(service_name, service);
	}
	
	/**
     * get a service to this agentserver
     * 
     */
	protected _Service<?> getService(String service_name) {
		return this.services.get(service_name);
	}
	
	@Override
	public String toString() {
		String s = "[AgentServer: name= " +this.name+ " , port= "+this.port;
		return s;
	}

	/**
	 * Retrieve _Agent from socket
	 * 
	 *@param Socket 
	 * 
	 */
	private _Agent getAgent(Socket Socket) throws IOException, ClassNotFoundException {

		//try {
			// Creation of BAMClassLoader
			BAMAgentClassLoader classLoader = new BAMAgentClassLoader(this.getClass().getClassLoader());

			// Creation of an InputStream, an ObjectInputStream and an
			// AgentInputStream
			InputStream is = Socket.getInputStream();
			ObjectInputStream ois = new ObjectInputStream(is);
			AgentInputStream ais = new AgentInputStream(is, classLoader);

			// Retrieve the Jar and integrate it
			Jar Jar = (Jar) ois.readObject();
			classLoader.integrateCode(Jar);
			//System.out.println(ais.readObject().toString());
			// Retrieve the _Agent using the AgentInputStream
			_Agent Agent = (_Agent) ais.readObject();

			ais.close();
			ois.close();
			is.close();
			
			return Agent;
		/*}
		catch (IOException e) {e.printStackTrace();} 
		catch (ClassNotFoundException e) {e.printStackTrace();}
		return null;*/
		
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		isalive = true;
		try {
			// create a socket Server
			ServerSocket socketServer = new ServerSocket(this.port);
			Starter.getLogger().log(Level.INFO, String.format("AgentServer %s started", this));
			while (isalive) {
				//Starter.getLogger().log(Level.FINE, String.format("AgentServer %s: about to accept", this));
				// Accept the incoming Agents
				Socket socketClient = socketServer.accept();
				//logger keeps a trace of the bahaviour of the app 
				//so we use it to make the connection
				Starter.getLogger().log(Level.FINE, String.format("AgentServer %s accepted an agent", this));

				// load the repository and the agent
				_Agent agent = this.getAgent(socketClient);
				//Initialise l'agent lors de son déploiement sur un des serveurs du bus
				//agent.reInit(this, this.name);
				Starter.getLogger().log(Level.INFO, String.format("AgentServer %s received agent %s", this, agent));
				//we run the Agent
				new Thread(agent).start();

				socketClient.close();
			}
			socketServer.close();
		} catch (IOException aException) {
			Starter.getLogger().log(Level.INFO, String.format("An IO exception occured in %s", this), aException);
		} catch (ClassNotFoundException aException) {
			Starter.getLogger().log(Level.INFO, String.format("A class was not found in %s", this), aException);
		}
		
	}

}

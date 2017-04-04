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
public class AgentServer extends Thread implements Runnable  {
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
	private _Agent getAgent(Socket socket) throws IOException, ClassNotFoundException {
		
		/*
		 	AgentInputStream input = new AgentInputStream(clientsocket.getInputStream(), bamLoader);
			
			//AgentInputStream lecteurJar = new Agent(client.getInputStream());
			Object o = input.readObject();
			if(o instanceof Jar)
			{
				Jar jarjar = (Jar) o;
				bamLoader.integrateCode(jarjar);
				Object possibleAgent = input.readObject();
				
				if(possibleAgent instanceof _Agent)
				{
					_Agent agent = (_Agent) possibleAgent;
					agent.reInit(this, name);
					return agent;
				}
				else
				{
					throw new ClassNotFoundException();
				}
			}
			else
			{
				throw new ClassNotFoundException();
			}
			
		} 
		catch (IOException e) {e.printStackTrace();} 
		catch (ClassNotFoundException e) {e.printStackTrace();}
		
return null;
		 */
		try {
			// Creation of BAMClassLoader
			BAMAgentClassLoader classLoader = new BAMAgentClassLoader(this.getContextClassLoader());
			AgentInputStream ais = new AgentInputStream(socket.getInputStream(), classLoader);
			Object obj = ais.readObject();
			if(obj instanceof Jar)
			{
				Jar jarjar = (Jar) obj;
				classLoader.integrateCode(jarjar);
				Object possibleAgent = ais.readObject();
				
				if(possibleAgent instanceof _Agent)
				{
					_Agent agent = (_Agent) possibleAgent;
					agent.reInit(this, Name);
					ais.close();
					return agent;
				}
				else
				{
					ais.close();
					throw new ClassNotFoundException();
				}
			}
			else
			{
				ais.close();
				throw new ClassNotFoundException();
			}
			
		}
		catch (IOException e) {e.printStackTrace();} 
		catch (ClassNotFoundException e) {e.printStackTrace();}
		
		return null;

	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		boolean alive = true;
		try {
			// create a socket Server
			ServerSocket socketServer = new ServerSocket(this.Port);

			Starter.getLogger().log(Level.INFO, String.format("AgentServer %s started", this));
			while (alive) {
				Starter.getLogger().log(Level.FINE, String.format("AgentServer %s: about to accept", this));
				// Accept the incoming Agents
				Socket SocketClient = socketServer.accept();
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
			socketServer.close();
		} catch (IOException aException) {
			Starter.getLogger().log(Level.INFO, String.format("An IO exception occured in %s", this), aException);
		} catch (ClassNotFoundException aException) {
			Starter.getLogger().log(Level.INFO, String.format("A class was not found in %s", this), aException);
		}
		
	}

}

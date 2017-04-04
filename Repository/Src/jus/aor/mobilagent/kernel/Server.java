/**
 * J<i>ava</i> U<i>tilities</i> for S<i>tudents</i>
 */
package jus.aor.mobilagent.kernel;

import java.io.ObjectOutputStream;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.omg.CORBA.portable.OutputStream;

import jus.aor.mobilagent.kernel.BAMAgentClassLoader;
import jus.aor.mobilagent.kernel._Agent;

/**
 * Le serveur principal permettant le lancement d'un serveur d'agents mobiles et les fonctions permettant de déployer des services et des agents.
 * @author     Morat
 */
public final class Server implements _Server {
	/** le nom logique du serveur */
	protected String name;
	/** le port où sera ataché le service du bus à agents mobiles. Pafr défaut on prendra le port 10140 */
	protected int port=10140;
	/** le server d'agent démarré sur ce noeud */
	protected AgentServer agentServer;
	/** le nom du logger */
	protected String loggerName;
	/** le logger de ce serveur */
	protected Logger logger=null;
	/**
	 * Démarre un serveur de type mobilagent 
	 * @param port le port d'écuote du serveur d'agent 
	 * @param name le nom du serveur
	 */
	public Server(final int port, final String name){
		this.name=name;
		try {
			this.port=port;
			/* mise en place du logger pour tracer l'application */
			loggerName = "jus/aor/mobilagent/"+InetAddress.getLocalHost().getHostName()+"/"+this.name;
			logger=Logger.getLogger(loggerName);
			/* démarrage du server d'agents mobiles attaché à cette machine */
			this.agentServer = new AgentServer(this.name, this.port);
			this.logger.log(Level.INFO, String.format("Starting AgentServer %s", this));
			agentServer.start();
			/* temporisation de mise en place du server d'agents */
			Thread.sleep(1000);
		}catch(Exception ex){
			logger.log(Level.FINE," erreur durant le lancement du serveur"+this,ex);
			return;
		}
	}
	/**
	 * Ajoute le service caractérisé par les arguments
	 * @param name nom du service
	 * @param classeName classe du service
	 * @param codeBase codebase du service
	 * @param args arguments de construction du service
	 */
	public final void addService(String name, String classeName, String codeBase, Object... args) {
		try {
			//create a BAM loader
			BAMServerClassLoader ClassLoader = new BAMServerClassLoader(new URL[] {},
													this.getClass().getClassLoader());
			//add the path to the jar
			ClassLoader.addURL(new URL(codeBase));

			// gets the class in which the method is defined 
			Class<?> ClassService = Class.forName(classeName, true, ClassLoader);

			// gets the constructor of this class 
			Constructor<?> Constructor = ClassService.getConstructor(Object[].class);
			// instantiate the new object
			_Service<?> Service = (_Service<?>) Constructor.newInstance(new Object[] { args });

			// adds the service to the list of services
			this.agentServer.addService(name, Service);

		}catch(Exception ex){
			logger.log(Level.FINE," erreur durant le lancement du serveur"+this,ex);
			return;
		}
	}
	/**
	 * deploie l'agent caractérisé par les arguments sur le serveur
	 * @param wclassName service's class
	 * @param args arguments de construction de l'agent
	 * @param codeBase codebase du service
	 * @param etapeAddress la liste des adresse des étapes
	 * @param etapeAction la liste des actions des étapes
	 */
	public final void deployAgent(String wclassName, Object[] args, String codeBase, List<String> etapeAddress, List<String> etapeAction) {
		_Agent Agent = null;
		try {
			//we create a new BAM Agent Class Loader
			//and create a new URI so that we can get the Path of the jar
			BAMAgentClassLoader ClassLoader = new BAMAgentClassLoader(new URI(codeBase).getPath(),
					this.getClass().getClassLoader());

			// we get the class back
			Class<?> ClassAgent = Class.forName(wclassName, true, ClassLoader);

			// we get the constructor again
			Constructor<?> Constructor = ClassAgent.getConstructor(Object[].class);
			// Instantiate the object
			Agent = (_Agent) Constructor.newInstance(new Object[] { args });
			// sets up (initialises) The Agent
			Agent.init(this.agentServer, this.name);
			
			int Size = etapeAction.size();
			for (int i = 0; i < Size; i++) {
				// adds the steps "etapes" in the agent's list 
				Field Champ = ClassAgent.getDeclaredField(etapeAction.get(i));
				// makes sure that the field is available
				Champ.setAccessible(true);
				// get the action
				_Action Action = (_Action) Champ.get(Agent);
				// add the steps
				Agent.addEtape(new Etape(new URI(etapeAddress.get(i)), Action));
				
			// start running the agent
			this.startAgent(Agent, ClassLoader);
			}
		}catch(Exception ex){
			logger.log(Level.FINE," erreur durant le lancement du serveur"+this,ex);
			return;
		}
	}
	/**
	 * Primitive permettant de "mover" un agent sur ce serveur en vue de son exécution
	 * immédiate.
	 * @param agent l'agent devant être exécuté
	 * @param loader le loader à utiliser pour charger les classes.
	 * @throws Exception
	 */
	protected void startAgent(_Agent agent, BAMAgentClassLoader loader) throws Exception {
		URI AgentServerSite = this.agentServer.site();
		//new connection!!!!!!!!!!! using sockets !!!!!
		Socket Sock = new Socket(AgentServerSite.getHost(), AgentServerSite.getPort());

		// Creation of a Stream and an ObjectOutputStream to destination
		java.io.OutputStream OutputStream = Sock.getOutputStream();
		ObjectOutputStream ObjectOutputStream = new ObjectOutputStream(OutputStream);
		ObjectOutputStream ObjectOutputStream2 = new ObjectOutputStream(OutputStream);

		// Retrieve byte code to send
		Jar BaseCode = loader.extractCode();

		// Send Jar's path to the 
		ObjectOutputStream.writeObject(BaseCode);
		// Send Agent (this)
		ObjectOutputStream2.writeObject(agent);

		// Close the sockets
		//end the connection
		ObjectOutputStream2.close();
		ObjectOutputStream.close();
		Sock.close();
	}
}

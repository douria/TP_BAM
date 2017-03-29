package jus.aor.mobilagent.kernel;

import java.util.logging.Level;

public class Agent implements _Agent {
  protected Route way;
  private String pServerName;
  private AgentServer pAgentServer;
	@Override
	/* 
	 * run la methode java
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		Starter.getLogger().log(Level.INFO, String.format("Agent %s starting execution", this));

		// If there is something to do
		if (this.way.hasNext()) {
			//we get the next way for this agent
			Etape wNextStep = this.way.next();
			Starter.getLogger().log(Level.FINE, String.format("Agent %s running step %s", this, wNextStep));
			//execute the action 
			wNextStep.action.execute();
			//we moove on to the next one if there is one otherwise we go back to
			//the first step which is also supposed to be the last one
			if (this.way.hasNext()) {
				// Send Agent to next AgentServer
				this.move();
			} else {
				// There is nothing left to do, Agent has finished
				Starter.getLogger().log(Level.FINE, String.format("Agent %s has finished its route", this));
			}
		} else {
			// If this is reached, it means that the Agent had nothing to do. So
			// just log it ended
			Starter.getLogger().log(Level.INFO, String.format("Agent %s had already finished", this));
		}
		
	}
	/**
	 * This methode allows the agent to move to the next server when 
	 * he has finished the execution of all the tasks of one way 
	 * 
	 */

   private void move() {
		//ask the teacher !!!!!
	}
		

   

	/*  
    * On declare les variable qu'il faut : un agent doit avoir une route qui lui ai propre
    * ensuite il doit aussi pouvoir communiquer avec l'agent server afin d'utiliser ses
    * services
    * 
    * @see jus.aor.mobilagent.kernel._Agent#init(jus.aor.mobilagent.kernel.AgentServer, java.lang.String)
    */
	@Override
	public void init(AgentServer agentServer, String serverName) {
		this.pAgentServer = agentServer;
		this.pServerName = serverName;
		this.way = new Route(new Etape(this.pAgentServer.site(), _Action.NIHIL));
		//the first step to do for an agent is NIHIL
		this.way.add(new Etape(this.pAgentServer.site(), _Action.NIHIL));
		
		
	}
   /*
    * (non-Javadoc)
    * @see jus.aor.mobilagent.kernel._Agent#reInit(jus.aor.mobilagent.kernel.AgentServer, java.lang.String)
    */
	@Override
	public void reInit(AgentServer server, String serverName) {
		this.pAgentServer=server;
		this.pServerName=serverName;
	}
    /*
     * (non-Javadoc)
     * @see jus.aor.mobilagent.kernel._Agent#addEtape(jus.aor.mobilagent.kernel.Etape)
     */
	@Override
	public void addEtape(Etape etape) {
		this.way.add(etape);
		// TODO Auto-generated method stub
		
	}

	@Override
	public _Action retour() {
		// TODO Auto-generated method stub
		return null;
	}
  /*
   * useful for debug
   * @see java.lang.Object#toString()
   */
	
	@Override
	public String toString() {
		return String.format("[Agent: route=%s, location=%s]", this.way, this.pServerName);
	}
}

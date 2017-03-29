package jus.aor.mobilagent.kernel;

public class Agent implements _Agent {
  protected Route way;
	@Override
	/* 
	 * run la methode java
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		// TODO Auto-generated method stub
		
	}
   /*  
    * (non-Javadoc)
    * @see jus.aor.mobilagent.kernel._Agent#init(jus.aor.mobilagent.kernel.AgentServer, java.lang.String)
    */
	@Override
	public void init(AgentServer agentServer, String serverName) {
		// TODO Auto-generated method stub
		
	}
   /*
    * (non-Javadoc)
    * @see jus.aor.mobilagent.kernel._Agent#reInit(jus.aor.mobilagent.kernel.AgentServer, java.lang.String)
    */
	@Override
	public void reInit(AgentServer server, String serverName) {
		// TODO Auto-generated method stub
		
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

}

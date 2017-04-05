package jus.aor.mobilagent.hostel;

import java.net.URI;


import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import Common.Hotel;
import Common.Numero;
import jus.aor.mobilagent.kernel._Action;
import jus.aor.mobilagent.kernel.Agent;
import jus.aor.mobilagent.kernel.Starter;

/**
 * Classe de test élémentaire pour le bus à agents mobiles
 * @author  Morat
 */
public class HotelAgent extends Agent{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2725697946274938967L;
	private String _localisation;
	protected ArrayList<Hotel> _hotels = new ArrayList<>();
	protected ArrayList<Numero> _nums = new ArrayList<>();
	protected long time;

	 /**
	  * construction d'un agent de type hello.
	  * @param args aucun argument n'est requis
	  */
	 public HotelAgent(Object... args) {
		 _localisation = (String) args[0];
		 time = System.currentTimeMillis();
	 }
	 /**
	 * l'action à entreprendre sur les serveurs visités  
	 */
	protected _Action doIt = new _Action(){

		private static final long serialVersionUID = -9129644307555501553L;

		@Override
		public void execute() {
			Starter.getLogger().log(Level.INFO, "Executing collection action");
			_hotels.addAll((ArrayList<Hotel>) pAgentServer.getService("Hotels").call(_localisation));
			//System.out.println(_hotels.size());
		}

		@Override
		public String toString() {
			return new String("LookingForHotel");
		}
	};
	 /**
	 * l'action à entreprendre sur les serveurs visités  
	 */
	protected _Action findTelephone = new _Action(){


		/**
		 * 
		 */
		private static final long serialVersionUID = 15231819542050637L;

		@Override
		public void execute() {
			Starter.getLogger().log(Level.INFO, "Executing collection action");
			_nums = (ArrayList<Numero>) pAgentServer.getService("Telephones").call(_hotels);
			//System.out.println(_hotels.size());
		}

		@Override
		public String toString() {
			return new String("LookingForHotel");
		}
	};
	/* (non-Javadoc)
	 * @see jus.aor.mobilagent.kernel.Agent#retour()
	 * 
	 */
	@Override
	public _Action retour(){
		return new _Action() {

			private static final long serialVersionUID = 8112403583439231794L;

			@Override
			public void execute() {
				Starter.getLogger().log(Level.INFO, "Executing Retour action!");
				Starter.getLogger().log(Level.INFO, "Hotel seen "+_hotels.size());
				Starter.getLogger().log(Level.INFO, "Numero seen "+_nums.size());
				Starter.getLogger().log(Level.INFO,  "Temps total :"+(System.currentTimeMillis()-time));
			}

			@Override
			public String toString() {
				return new String("BACK");
			}
		};
	}
	// ...
}

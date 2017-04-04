package Server;

import java.rmi.Remote;
import java.util.ArrayList;
import java.util.List;

import Common.Hotel;
import Common._Chaine;

public class Chaine implements _Chaine, Remote {

	ArrayList<Hotel> li = new ArrayList<>();
	
	public void add(String name, String localisation) {
		li.add(new Hotel(name, localisation));
	}
	
	@Override
	public List<Hotel> get(String localisation) {
		return li;
	}

}

package Server;

import java.rmi.Remote;
import java.util.ArrayList;
import java.util.List;

import Common.Hotel;
import Common._Chaine;

public class Chaine implements _Chaine {

	ArrayList<Hotel> li = new ArrayList<>();
	
	public void add(String name, String localisation) {
		li.add(new Hotel(name, localisation));
	}
	
	@Override
	public List<Hotel> get(String localisation) {
		List<Hotel> filtre = new ArrayList<Hotel>();
		for(Hotel h : this.li)
			if(localisation.equals(h.localisation))
				filtre.add(h);

		return filtre;
	}

}

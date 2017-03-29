package jus.aor.mobilagent.kernel;

import java.net.URL;
import java.net.URLClassLoader;
//This class loader is used to load classes and resources from
//a search path of URLs referring to both JAR files and directories.
public class BAMServerClassLoader extends URLClassLoader {
    
	public BAMServerClassLoader(URL[] urls, ClassLoader c) {
		super(urls);
	}
	//toutes les methodes utilsées sont definies dans la classe mere 
	@Override
	protected void addURL(URL url) {
		super.addURL(url);
	}

	@Override
	public String toString() {
		return super.toString();
	}

}
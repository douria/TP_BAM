package jus.aor.mobilagent.kernel;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarException;
import java.util.jar.JarOutputStream;

//this class allow the agent to load the needed jars to execute it tasks.
public class BAMAgentClassLoader extends ClassLoader {


	/** Classes loaded from Jar files while executing integrateCode()
	 * 
	 *  
	 */
	private Map<String, byte[]> Classes;

	/**
	 *we create the classloader of an agent and also it map of classes 
	 * @param aParent
	 *            Parent classloader
	 */
	public BAMAgentClassLoader(ClassLoader aParent) {
		super(aParent);
		this.Classes = new HashMap<String, byte[]>();
	}

	/**
	 * This function creates a new jar that it finds in the agent's jarfilepath 
	 * And creates a new classloader instanciation
	 * @param aParent
	 *            Parent classloader
	 * @throws IOException
	 * @throws JarException
	 */
	
	public BAMAgentClassLoader(String aJarFilePath, ClassLoader aParent) throws JarException, IOException {
		this(aParent);
		Jar aJar = new Jar(aJarFilePath);
		this.integrateCode(aJar);
	}

	/**
	 * Return the name of a class based on the given file path
	 *
	 * @param aClassFilePath
	 *            path to a file containing a class
	 * @return String
	 */
	private String className(String aClassFilePath) {
		return aClassFilePath.replace(".class", "").replace("/", ".");
	}

	/**
	 * Return a Jar object containing all the classes found in the
	 * BamAgentClassLoader
	 *
	 * To do this, a temporary Jar file is created.
	 *
	 * @return Jar containing all classes in the current BamAgentClassLoader
	 * @throws JarException
	 * @throws IOException
	 */
	public Jar extractCode() throws JarException, IOException {
		//we create a new file that contains the jar 
		File TmpJar = File.createTempFile("temporaryJar", ".jar");

		// Try to create an OutputStream and JarOutputStream on the temporary
		// Jar file just created
		try (JarOutputStream JarOutputStream = new JarOutputStream(new FileOutputStream(TmpJar))) {
			//we create all the classes that were in the initial jar  
			for (String ClassName : this.Classes.keySet()) {
				// Create an entry and write the byteCode of the class
				// for every class in Classes
				//Begins writing a new JAR file entry and positions the stream 
				//to the start of the entry data. This method will also close any previous entry.
				JarOutputStream.putNextEntry(new JarEntry(ClassName));
				//we write the whole content of the class in the jar stream 
				JarOutputStream.write(this.Classes.get(ClassName));
			}
			JarOutputStream.close();
		}
		// Create the Jar object using the Jar file
		return new Jar(TmpJar.getPath());
	}

	/**
	 *When creating the MobileAgent we put on it hashmap all the methods and 
	 *classes it needs to run it program
	 *Then we create the jar that will make it translate from a server 
	 *to another
	 *
	 * @param aJar
	 *            Jar to integrate
	 */
	public void integrateCode(Jar jar) {
		for (Entry<String, byte[]> Entry : jar) {
			String ClassName = this.className(Entry.getKey());
			this.Classes.put(ClassName, Entry.getValue());

			// Define and resolve the class to be able to use it
			Class<?> Class = this.defineClass(ClassName, Entry.getValue(), 0, Entry.getValue().length);
			super.resolveClass(Class);
		}
		
	}

}

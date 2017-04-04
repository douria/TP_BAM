package jus.aor.mobilagent.kernel;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

class AgentInputStream extends ObjectInputStream {
	    // we use a classloader
		BAMAgentClassLoader loader;
		//a Constructor that creates an AngentInputStream to send it later 
		AgentInputStream(InputStream is, BAMAgentClassLoader cl) throws IOException {
			super(is);
			this.loader = cl;
		}

		@Override
		protected Class<?> resolveClass(ObjectStreamClass cl) throws IOException, ClassNotFoundException {
			return this.loader.loadClass(cl.getName());
		}
}


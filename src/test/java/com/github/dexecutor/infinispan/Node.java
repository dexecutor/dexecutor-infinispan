package com.github.dexecutor.infinispan;

/**
 * 
 * Terminal #1
 * mvn test-compile exec:java -Djava.net.preferIPv4Stack=true -Dexec.mainClass="com.github.dexecutor.infinispan.Node" -Dexec.classpathScope="test" -Dexec.args="s node-A"
 * 
 * Terminal #2
 * mvn test-compile exec:java -Djava.net.preferIPv4Stack=true -Dexec.mainClass="com.github.dexecutor.infinispan.Node" -Dexec.classpathScope="test" -Dexec.args="s node-B"
 * 
 * Terminal #3
 * mvn test-compile exec:java  -Dexec.classpathScope="test" -Djava.net.preferIPv4Stack=true -Dexec.mainClass="com.github.dexecutor.infinispan.Node" -Dexec.args="m node-C"
 * 
 * @author Nadeem Mohammad
 *
 */
public class Node {

	public static void main(String[] args) throws Exception {
		new Job().run(isMaster(args[0]), args[1], "dist");
	}

	private static boolean isMaster(String string) {
		return string.equalsIgnoreCase("m");
	}
}
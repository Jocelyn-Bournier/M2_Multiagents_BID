package StellarMining;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class Main {
        public static void main(String[] args) {

        Runtime runtime = Runtime.instance();
        Profile config = new ProfileImpl("localhost", 8888, null);
        config.setParameter("gui", "true");
        AgentContainer mc = runtime.createMainContainer(config); // Recuperation du container principal
        try {
            AgentController agent1 = mc.createNewAgent("miner1", "StellarMining.MiningAgent", null);
            AgentController agent2 = mc.createNewAgent("miner2", "StellarMining.MiningAgent", null);
            agent1.start();
            agent2.start();
        } catch (StaleProxyException ignored) {}
    }
}

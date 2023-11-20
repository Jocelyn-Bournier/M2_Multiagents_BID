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
        MiningAgent miner1; // Agent mineur 1
        MiningAgent miner2; // Agent mineur 2

        Env env = new Env();

        try {
            Object[] param_videur = { env, mc };
            gerant = mc.createNewAgent("Gerant", Gerant.class.getName(), param_videur);
            gerant.start();
        } catch (StaleProxyException ignored) {
        }
    }}
}

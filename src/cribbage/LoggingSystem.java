package cribbage;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

import java.io.*;
import java.util.Properties;

public class LoggingSystem {

    private static LoggingSystem singleInstance = null;
    private Properties cribbageProperties = new Properties();
    private File file;

    private LoggingSystem(){
        // Create new file cribbage.txt to start logging
        file = new File("cribbage.log");
        try {
			file.createNewFile();
		} catch (IOException e) {
			System.out.println("File creating error");
			e.printStackTrace();
		}
        // Delete old content in file
        try {
			FileWriter fw = new FileWriter(file);
			fw.close();
		} catch (IOException e) {
			System.out.println("File writing error");
			e.printStackTrace();
		}
        // Load properties (for player info)
        loadProperties();
    }

    private void WriteToFile(String statement){
        try {
            // Write given string to log file
            FileWriter fw = new FileWriter(file, true);
            fw.write(statement);
            fw.close();
        } catch (IOException e){
            System.out.println("File writing error");
            e.printStackTrace();
        }
    }

    public void loadProperties() {
        try (FileReader inStream = new FileReader("cribbage.properties")) {
            this.cribbageProperties.load(inStream);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static LoggingSystem getInstance() {
        if (singleInstance == null) {
            singleInstance = new LoggingSystem();
        }
        return singleInstance;
    }

    // convert int to string of player number "Pn"
    private String p(int n){
        return "P"+n;
    }

    public void logSeed(){
        // Log seed property from properties file
        String seed = cribbageProperties.getProperty("Seed");  //Seed property
        WriteToFile("Seed," + seed + "\n");
    }

    public void logPlayer(int n){
        // Log player type from properties file
        String playerType, player;
        for(int i = 0; i<n; i++){
            player = "Player"+i;
            playerType = cribbageProperties.getProperty(player);
            WriteToFile(playerType+","+p(i)+'\n');
        }
    }

    public void logDeal(int playerNumber, Hand hand){
        // Log hand dealt to a single player
        String cardList;
        cardList = Cribbage.canonical(hand);
        WriteToFile("deal,"+p(playerNumber)+","+cardList);
    }

    public void logDiscard(int playerNumber, Hand discarded){
        // Log cards discarded
        String cardList;
        cardList = Cribbage.canonical(discarded);
        WriteToFile("discard,"+p(playerNumber)+","+cardList);
    }

    // Log starter card
    public void logStarter(String rank, String suit){
        WriteToFile("starter,"+rank+suit);
    }

    // Log an individual play
    public void logPlay(int playerNumber, int playValue, Card card){
        WriteToFile("play,"+p(playerNumber)+","+playValue+","+card.getRank()+card.getSuit());
    }

    // Log a score update during play
    public void logScore(int playerNumber, int totScore, int score, String type){
        WriteToFile("score,"+p(playerNumber)+","+totScore+","+score+","+type);
    }

    // Log a score update during show (include hand of relevant cards)
    public void logScore(int playerNumber, int totScore, int score, String type, Hand hand){
        WriteToFile("score,"+p(playerNumber)+","+totScore+","+score+","+type+","+Cribbage.canonical(hand));
    }

    public void logShow(int playerNumber, Card starter, Hand hand){
        WriteToFile("show,"+p(playerNumber)+","+starter.getRank()+starter.getSuit()+"+"+Cribbage.canonical(hand));
    }

}
package modblacklister.modblacklister;

import com.google.inject.Inject;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;
import org.slf4j.Logger;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;

import java.io.*;
import java.util.ArrayList;

@Plugin(
        id = "modblacklister",
        name = "ModBlacklister",
        authors = {
                "MahmutKocas"
        }
)
public class ModBlacklister {

    private NetHandlerPlayServer con = null;
    private NetworkManager nm = null;
    private NetworkDispatcher nd = null;


    private ArrayList<String> SuspectList;

    @Inject
    private Logger logger;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        logger.info("WE ARE IN!");
        SuspectList = new ArrayList<>();
        File folder = new File ("ModBlackLister/");
        if(!folder.exists())
            if(folder.mkdir())
                createConfig();
            else
                logger.info("Something went bad. I am unable to create the config folder.");


    }

    @Listener
    public void onConnection(ClientConnectionEvent.Join e){
        EntityPlayerMP player = (EntityPlayerMP) e.getTargetEntity();
        String playerName = ((EntityPlayerMP) e.getTargetEntity()).getDisplayNameString();
        try {
        ArrayList<String> modlist = getModList(player);
        ArrayList<String> blackList = getBannedMods();

        new Thread(() -> {
            for(String bannedMod : blackList){
                for(String modName : modlist)
                    if(modName.contains(bannedMod) && !bannedMod.trim().equals("")){
                        try {
                            writeUser(playerName,modlist,bannedMod,modName);
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        e.getTargetEntity().kick();
                    }
            }
        }).start();

        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        } catch (NoSuchFieldException e1) {
            e1.printStackTrace();
        }

    }

    //Creates Folder and shit
    private void createConfig() {
        try {
            File file = new File("ModBlackLister/blackListedMods.cfg");
            OutputStream os = new FileOutputStream(file);
            Writer writer = new OutputStreamWriter(os);


            writer.write("" +
                    "# This is a config file for Mod Black Lister\n" +
                    "# To use this file:\n" +
                    "# Go Server Console and see how mod name looks at Console when player joins your server\n" +
                    "# Enter the name you see then press ENTER!\n" +
                    "# If you type 'ray' here, it will look for mod name contains 'ray'\n" +
                    "# So it will also detect 'xray' word\n" +
                    "# Mod per Line\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Gets Player's Mod List
    private ArrayList<String> getModList(EntityPlayerMP player) throws NoSuchFieldException, IllegalAccessException {

        // Thanks to rodel77, found his topic on internet
        con = (NetHandlerPlayServer) player.getClass().getField("field_71135_a").get(player);
        nm = (NetworkManager) con.getClass().getField("field_147371_a").get(con);
        nd = NetworkDispatcher.get(nm);
        // Thx.

        ArrayList<String> modlist = new ArrayList<>();

        nd.getModList().entrySet().forEach(value -> {
            modlist.add(value.getKey());
        });

        return modlist;
    }

    //Gets Banned Mod List from file
    private ArrayList<String> getBannedMods() throws IOException {
        File file = new File("ModBlackLister/blackListedMods.cfg");

        //IF FILE NOT EXISTS CREATES WITH INFORMATION
        if(!file.exists()){
            File folder = new File ("ModBlackLister/");
            if(!folder.exists()){
                folder.mkdir();
                createConfig();
            }
        }

        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        ArrayList<String> bannedList = new ArrayList<>();

        String current;
        while((current = br.readLine()) != null){
            if(current.trim().charAt(0) != '#' || current.trim().equals("")){
                bannedList.add(current);
            }
        }
        br.close();
        return bannedList;
    }

    //Gets Suspected Users
    private void getSuspectList(){

        try {
            File file = new File("");
            FileReader fr = new FileReader(file);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    //Writes kicked user
    private void writeUser(String playerName, ArrayList<String> modlist, String blackMod,String modName) throws IOException {
        File file = new File("ModBlackLister/KickedUsers.log");
        OutputStream os = new FileOutputStream(file,true);
        Writer writer = new OutputStreamWriter(os);

        new Thread(() -> {
            try {
                String toWrite = "Player : " + playerName + " kicked.\n" +
                        "\tBlaclisted Mod / Client Mod : "+ blackMod + "/" + modName +
                        "\n\tmodlist{ " + modlist +" }\n";


                writer.write(toWrite);
                writer.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }).start();


    }



}

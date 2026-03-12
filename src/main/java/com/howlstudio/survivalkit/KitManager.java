package com.howlstudio.survivalkit;
import com.hypixel.hytale.component.Ref; import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.nio.file.*; import java.util.*;
public class KitManager {
    private final Path dataDir;
    private final List<String> kitItems=new ArrayList<>();
    private final Set<UUID> givenTo=new HashSet<>();
    private boolean autoGiveFirstJoin=true;
    public KitManager(Path d){this.dataDir=d;try{Files.createDirectories(d);}catch(Exception e){}setDefaultKit();load();}
    private void setDefaultKit(){kitItems.add("Wooden Sword x1");kitItems.add("Wooden Pickaxe x1");kitItems.add("Wooden Axe x1");kitItems.add("Bread x8");kitItems.add("Torch x16");}
    public int getKitItemCount(){return kitItems.size();}
    public boolean hasReceived(UUID uid){return givenTo.contains(uid);}
    public void giveKit(PlayerRef ref){givenTo.add(ref.getUuid());ref.sendMessage(Message.raw("§6[SurvivalKit] §rYou received your starter kit!"));for(String item:kitItems)ref.sendMessage(Message.raw("  + §e"+item));save();}
    public void save(){try{StringBuilder sb=new StringBuilder("autoGive="+autoGiveFirstJoin+"\n");for(String i:kitItems)sb.append("item="+i+"\n");for(UUID u:givenTo)sb.append("given="+u+"\n");Files.writeString(dataDir.resolve("kit.txt"),sb.toString());}catch(Exception e){}}
    private void load(){try{Path f=dataDir.resolve("kit.txt");if(!Files.exists(f))return;kitItems.clear();for(String l:Files.readAllLines(f)){if(l.startsWith("item="))kitItems.add(l.substring(5));else if(l.startsWith("given="))try{givenTo.add(UUID.fromString(l.substring(6)));}catch(Exception e){}else if(l.startsWith("autoGive="))autoGiveFirstJoin=Boolean.parseBoolean(l.substring(9));}}catch(Exception e){}}
    public AbstractPlayerCommand getKitAdminCommand(){
        return new AbstractPlayerCommand("starterkit","[Admin] Manage starter kit. /starterkit list|add <item>|reset <player>|toggle"){
            @Override protected void execute(CommandContext ctx,Store<EntityStore> store,Ref<EntityStore> ref,PlayerRef playerRef,World world){
                String[]args=ctx.getInputString().trim().split("\\s+",2); String sub=args.length>0?args[0].toLowerCase():"list";
                switch(sub){
                    case"list"->{playerRef.sendMessage(Message.raw("[StarterKit] Items (autoGive="+autoGiveFirstJoin+"):"));for(int i=0;i<kitItems.size();i++)playerRef.sendMessage(Message.raw("  "+(i+1)+". "+kitItems.get(i)));}
                    case"add"->{if(args.length<2)break;kitItems.add(args[1]);save();playerRef.sendMessage(Message.raw("[StarterKit] Added: "+args[1]));}
                    case"toggle"->{autoGiveFirstJoin=!autoGiveFirstJoin;save();playerRef.sendMessage(Message.raw("[StarterKit] Auto-give first join: "+autoGiveFirstJoin));}
                    case"given"->{playerRef.sendMessage(Message.raw("[StarterKit] Kit given to "+givenTo.size()+" players."));}
                    default->playerRef.sendMessage(Message.raw("Usage: /starterkit list|add <item>|toggle|given"));
                }
            }
        };
    }
    public boolean isAutoGive(){return autoGiveFirstJoin;}
}

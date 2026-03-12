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
    private final Map<String,Kit> kits=new LinkedHashMap<>();
    private final Map<UUID,Map<String,Long>> claimed=new HashMap<>();
    public KitManager(Path d){this.dataDir=d;try{Files.createDirectories(d);}catch(Exception e){}loadDefaults();load();}
    private void loadDefaults(){
        kits.put("starter",new Kit("starter","Starter Kit","Basic survival gear for new players",0,List.of("sword","pickaxe","axe","food_x16","torches_x32")));
        kits.put("pvp",new Kit("pvp","PvP Kit","Combat gear",3*3600_000L,List.of("iron_sword","iron_armor","golden_apple_x2","bow","arrows_x32")));
        kits.put("builder",new Kit("builder","Builder Kit","Building essentials",6*3600_000L,List.of("wood_x64","stone_x64","glass_x32","torches_x64","shovel","pickaxe")));
    }
    public int getKitCount(){return kits.size();}
    public boolean canClaim(UUID uid,Kit k){
        if(k.getCooldownMs()<0)return true;
        Map<String,Long> m=claimed.computeIfAbsent(uid,x->new HashMap<>());
        Long last=m.get(k.getId());if(last==null)return true;
        if(k.isOneTime())return false;
        return System.currentTimeMillis()-last>=k.getCooldownMs();
    }
    public long cooldownLeft(UUID uid,Kit k){
        Map<String,Long> m=claimed.getOrDefault(uid,Map.of());
        Long last=m.get(k.getId());if(last==null)return 0;
        return Math.max(0,k.getCooldownMs()-(System.currentTimeMillis()-last));
    }
    public void claim(UUID uid,Kit k){claimed.computeIfAbsent(uid,x->new HashMap<>()).put(k.getId(),System.currentTimeMillis());save();}
    public void save(){try{StringBuilder sb=new StringBuilder();for(Kit k:kits.values())sb.append(k.toConfig()).append("\n");Files.writeString(dataDir.resolve("kits.txt"),sb.toString());}catch(Exception e){}}
    private void load(){try{Path f=dataDir.resolve("kits.txt");if(!Files.exists(f))return;kits.clear();for(String l:Files.readAllLines(f)){Kit k=Kit.fromConfig(l);if(k!=null)kits.put(k.getId(),k);}}catch(Exception e){}}
    public AbstractPlayerCommand getKitCommand(){
        return new AbstractPlayerCommand("kit","Claim a kit. /kit list | /kit <name>"){
            @Override protected void execute(CommandContext ctx,Store<EntityStore> store,Ref<EntityStore> ref,PlayerRef playerRef,World world){
                String input=ctx.getInputString().trim();
                if(input.isEmpty()||input.equalsIgnoreCase("list")){
                    playerRef.sendMessage(Message.raw("=== Available Kits ==="));
                    for(Kit k:kits.values()){
                        boolean can=canClaim(playerRef.getUuid(),k);
                        String cd=can?"§aAvailable":"§c"+(k.isOneTime()?"Claimed":"CD: "+cooldownLeft(playerRef.getUuid(),k)/60_000+"m")+"§r";
                        playerRef.sendMessage(Message.raw("  §6"+k.getName()+"§r — "+k.getDescription()+" ["+cd+"§r]"));
                    }
                    return;
                }
                Kit k=kits.get(input.toLowerCase());
                if(k==null){playerRef.sendMessage(Message.raw("[Kit] Unknown kit. Use /kit list."));return;}
                if(!canClaim(playerRef.getUuid(),k)){
                    long left=cooldownLeft(playerRef.getUuid(),k);
                    playerRef.sendMessage(Message.raw("[Kit] "+(k.isOneTime()?"Already claimed.":"Cooldown: "+left/60_000+"m remaining.")));return;}
                claim(playerRef.getUuid(),k);
                playerRef.sendMessage(Message.raw("[Kit] Claimed: §6"+k.getName()+"§r! Items: "+String.join(", ",k.getItems())));
                System.out.println("[SurvivalKit] "+playerRef.getUsername()+" claimed kit: "+k.getId());
            }
        };
    }
}

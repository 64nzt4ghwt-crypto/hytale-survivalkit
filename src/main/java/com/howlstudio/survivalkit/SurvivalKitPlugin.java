package com.howlstudio.survivalkit;
import com.hypixel.hytale.server.core.command.system.CommandManager;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
/** SurvivalKit — Give new players a configurable starter kit. One-time claim, cooldown kits, admin kit editor. */
public final class SurvivalKitPlugin extends JavaPlugin {
    private KitManager kits;
    public SurvivalKitPlugin(JavaPluginInit init){super(init);}
    @Override protected void setup(){
        System.out.println("[SurvivalKit] Loading...");
        kits=new KitManager(getDataDirectory());
        new KitListener(kits).register();
        CommandManager.get().register(kits.getKitCommand());
        System.out.println("[SurvivalKit] Ready. "+kits.getKitCount()+" kits configured.");
    }
    @Override protected void shutdown(){if(kits!=null)kits.save();System.out.println("[SurvivalKit] Stopped.");}
}

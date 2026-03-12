package com.howlstudio.survivalkit;
import com.hypixel.hytale.server.core.command.system.CommandManager;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
/** SurvivalKit — Auto-give starter kit to new players on first join. Configurable kit contents. */
public final class SurvivalKitPlugin extends JavaPlugin {
    private KitManager mgr;
    public SurvivalKitPlugin(JavaPluginInit init){super(init);}
    @Override protected void setup(){
        System.out.println("[SurvivalKit] Loading...");
        mgr=new KitManager(getDataDirectory());
        new KitListener(mgr).register();
        CommandManager.get().register(mgr.getKitAdminCommand());
        System.out.println("[SurvivalKit] Ready. "+mgr.getKitItemCount()+" starter items.");
    }
    @Override protected void shutdown(){if(mgr!=null)mgr.save();System.out.println("[SurvivalKit] Stopped.");}
}

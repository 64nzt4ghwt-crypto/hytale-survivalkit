package com.howlstudio.survivalkit;
import java.util.*;
public class Kit {
    private final String id,name,description;
    private final long cooldownMs; // 0=one-time, -1=no cooldown
    private final List<String> items;
    public Kit(String id,String name,String description,long cooldownMs,List<String> items){
        this.id=id;this.name=name;this.description=description;this.cooldownMs=cooldownMs;this.items=items;
    }
    public String getId(){return id;} public String getName(){return name;}
    public String getDescription(){return description;} public long getCooldownMs(){return cooldownMs;}
    public List<String> getItems(){return items;}
    public boolean isOneTime(){return cooldownMs==0;}
    public String toConfig(){return id+"|"+name+"|"+description+"|"+cooldownMs+"|"+String.join(",",items);}
    public static Kit fromConfig(String s){String[]p=s.split("\\|",5);if(p.length<5)return null;
        List<String> items=new ArrayList<>(Arrays.asList(p[4].split(",")));
        return new Kit(p[0],p[1],p[2],Long.parseLong(p[3]),items);}
}

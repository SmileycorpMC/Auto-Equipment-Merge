package net.smileycorp.autoequipmerge;

import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {
    
    public static int itemMatch, nbtMatch = 1;
    
    public static void syncConfig(Configuration config) {
        try {
            config.load();
            itemMatch = config.getInt("itemMatch", "General", 1, 1, 2, "Which mode to merge items? 1) same item, 2) same tool type");
            nbtMatch = config.getInt("nbtMatch", "General", 1, 1, 4, "Which mode to merge items? 1) match equivalent nbt, 2) merge nbt tags, 3), ignore nbt, 4) remove nbt");
        } catch (Exception e) {
        } finally {
            config.save();
        }
    }
    
}

package net.smileycorp.autoequipmerge;

import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {

    public static final ConfigHandler instance = new ConfigHandler();
    public static final ConfigHandler clientInstance = new ConfigHandler();

    public int itemMatch, nbtMatch;
    public boolean hotbarOnly, mergeArmorSlots, mergeOffhand, mergePastFull, pickupOnly;
    
    public void syncConfig(Configuration config) {
        try {
            config.load();
            itemMatch = config.getInt("itemMatch", "General", 1, 1, 2, "Which mode to merge items? 1) same item, 2) same tool type");
            nbtMatch = config.getInt("nbtMatch", "General", 1, 1, 4, "Which mode to merge items? 1) match equivalent nbt, 2) merge nbt tags, 3), ignore nbt, 4) remove nbt");
            hotbarOnly = config.getBoolean("hotbarOnly", "General", false, "Whether to only merge items in the hotbar, instead of full inventory?");
            mergeArmorSlots = config.getBoolean("mergeArmorSlots", "General", true, "Whether to merge items in armour slots? Ignores hotbarOnly");
            mergeOffhand = config.getBoolean("mergeOffhand", "General", true, "Whether to merge items in the offhand? Ignores hotbarOnly");
            mergePastFull = config.getBoolean("mergePastFull", "General", false, "Whether to merge items even if the item to merge into's durability would be full?");
            pickupOnly = config.getBoolean("pickupOnly", "General", true, "Whether to merge only items picked up, or all items added to the player inventory?");
        } catch (Exception e) {
        } finally {
            config.save();
        }
    }

    public void writeToBuf(ByteBuf buf) {
        buf.writeByte(itemMatch);
        buf.writeByte(nbtMatch);
        buf.writeBoolean(hotbarOnly);
        buf.writeBoolean(mergeArmorSlots);
        buf.writeBoolean(mergeOffhand);
        buf.writeBoolean(mergePastFull);
        buf.writeBoolean(pickupOnly);
    }

    public void readFromBuf(ByteBuf buf) {
        itemMatch = buf.readByte();
        nbtMatch = buf.readByte();
        hotbarOnly = buf.readBoolean();
        mergeArmorSlots = buf.readBoolean();
        mergeOffhand = buf.readBoolean();
        mergePastFull = buf.readBoolean();
        pickupOnly = buf.readBoolean();
        buf.release();
    }

    public boolean isClientSide() {
        return this == clientInstance;
    }

    public static ConfigHandler getInstance(World world) {
        return world.isRemote ? clientInstance : instance;
    }

}

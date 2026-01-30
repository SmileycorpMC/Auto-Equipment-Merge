package net.smileycorp.autoequipmerge.client;

import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.Tuple;

import java.util.Map;

public class ItemMergeRenderer {
    
    private static ItemMergeRenderer INSTANCE;
    private final EntityPlayerSP player;
    
    private final Map<Integer, Tuple<Float, Integer>> entries = Maps.newHashMap();
    private int current_slot = -1;
    
    private ItemMergeRenderer(EntityPlayerSP player) {
        this.player = player;
    }
    
    public static ItemMergeRenderer getInstance() {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (INSTANCE == null) INSTANCE = new ItemMergeRenderer(player);
        else if (INSTANCE.player != player) INSTANCE = new ItemMergeRenderer(player);
        return INSTANCE;
    }
    
    public void setupRenderer(byte slot, int amount) {
        System.out.println(slot + ", " + amount);
        entries.put((int) slot, new Tuple(player.world.getTotalWorldTime() + Minecraft.getMinecraft().getRenderPartialTicks(), amount));
    }
    
    public boolean shouldRenderAnimations(int slot) {
        Tuple<Float, Integer> tuple = entries.get(slot);
        if (tuple == null) return false;
        if (player.world.getTotalWorldTime() + Minecraft.getMinecraft().getRenderPartialTicks() > tuple.getFirst() + 30) {
            entries.remove(slot);
            return false;
        }
        current_slot = slot;
        return true;
    }
    
    public boolean hasCurrentSlot() {
        return current_slot >= 0;
    }
    
    public Tuple<Float, Integer> getCurrentSlot() {
        return entries.get(current_slot);
    }
    
    public void clearCurrentSlot() {
        current_slot = -1;
    }
    
    public float getTimePercentage() {
        return (player.world.getTotalWorldTime() + Minecraft.getMinecraft().getRenderPartialTicks() - getCurrentSlot().getFirst()) / 30f;
    }
    
    public float getTimeDamage() {
        Tuple<Float, Integer> tuple = getCurrentSlot();
        return (1f -((player.world.getTotalWorldTime() + Minecraft.getMinecraft().getRenderPartialTicks() - tuple.getFirst()) / 30f))
                * (float) tuple.getSecond();
    }
    
    public boolean renderSwell() {
        if (!hasCurrentSlot()) return false;
        return player.world.getTotalWorldTime() + Minecraft.getMinecraft().getRenderPartialTicks() > getCurrentSlot().getFirst();
    }
    
}

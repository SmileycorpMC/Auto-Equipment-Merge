package net.smileycorp.autoequipmerge;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

@Mod(modid = Constants.MODID, name = Constants.NAME, version = Constants.VERSION)
@Mod.EventBusSubscriber(modid = Constants.MODID)
public class AutoEquipmentMerge {
    
    @SubscribeEvent
    public static void pickup(PlayerEvent.ItemPickupEvent event) {
        EntityPlayer player = event.player;
        if (player == null) return;
        if (player.world.isRemote) return;
        ItemStack stack = event.getStack();
        if (!stack.isItemStackDamageable() |!stack.isItemDamaged()) return;
        if (tryToMerge(stack, player.getArmorInventoryList())) return;
        tryToMerge(stack, player.inventory.mainInventory);
    }
    
    private static boolean tryToMerge(ItemStack toAdd, Iterable<ItemStack> inventory) {
        for (ItemStack stack : inventory) {
            if (stack.getItem() != toAdd.getItem()) continue;
            if (stack.hasTagCompound() != toAdd.hasTagCompound()) continue;
            if (stack.hasTagCompound()) if (stack.getTagCompound().equals(toAdd.getTagCompound())) continue;
            if (!stack.areCapsCompatible(toAdd)) continue;
            int damage = toAdd.getItemDamage();
            if (stack.getItemDamage() + damage > toAdd.getMaxDamage()) damage = stack.getMaxDamage() - toAdd.getItemDamage();
            if (toAdd.getItemDamage() < damage) toAdd.shrink(1);
            else toAdd.setItemDamage(toAdd.getItemDamage() - damage);
            stack.setItemDamage(stack.getItemDamage() + damage);
            return true;
        }
        return false;
    }
    
    
}

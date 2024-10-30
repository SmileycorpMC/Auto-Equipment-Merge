package net.smileycorp.autoequipmerge;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Collections;
import java.util.List;

@Mod(modid = Constants.MODID, name = Constants.NAME, version = Constants.VERSION, acceptableRemoteVersions="*")
@Mod.EventBusSubscriber(modid = Constants.MODID)
public class AutoEquipmentMerge {
    
    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        ConfigHandler.syncConfig(new Configuration(event.getSuggestedConfigurationFile()));
    }
    
    @SubscribeEvent
    public static void pickup(EntityItemPickupEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player == null) return;
        if (player.world.isRemote) return;
        ItemStack stack = event.getItem().getItem();
        if (!stack.isItemStackDamageable() |!stack.isItemDamaged()) return;
        if (ConfigHandler.mergeArmorSlots) if (tryToMerge(player, stack, player.inventory.armorInventory)) return;
        if (tryToMerge(player, stack, player.inventory.mainInventory.subList(0, 9))) return;
        if (ConfigHandler.mergeOffhand) if (tryToMerge(player, stack, player.inventory.offHandInventory)) return;
        if (!ConfigHandler.hotbarOnly) tryToMerge(player, stack, player.inventory.mainInventory.subList(9, player.inventory.mainInventory.size()));
    }
    
    private static boolean tryToMerge(EntityPlayer player, ItemStack toAdd, List<ItemStack> inventory) {
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.get(i);
            if (!matches(toAdd, stack)) continue;
            if (!matchesNBT(toAdd, stack)) continue;
            int damage = toAdd.getMaxDamage() - toAdd.getItemDamage() + 1;
            if (stack.getItemDamage() - damage >= 0) {
                toAdd.shrink(1);
                stack.setItemDamage(stack.getItemDamage() - damage);
                player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_PICKUP, player.getSoundCategory(), 0.2f,
                        (player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 1.4f + 2);
            }
            else {
                toAdd.setItemDamage(stack.getMaxDamage() - stack.getItemDamage());
                stack.setItemDamage(0);
            }
            if (ConfigHandler.nbtMatch == 2 && stack.hasTagCompound()) stack.getTagCompound().merge(toAdd.getTagCompound());
            if (ConfigHandler.nbtMatch == 4) stack.setTagCompound(null);
            return true;
        }
        return false;
    }
    
    private static boolean matches(ItemStack toAdd, ItemStack stack) {
        if (!stack.isItemStackDamageable() || stack.isItemDamaged()) return false;
        if (ConfigHandler.itemMatch == 2) {
            Item item = stack.getItem();
            Item addItem = toAdd.getItem();
            if (item instanceof ItemSword && addItem instanceof ItemSword) return true;
            if (item.getEquipmentSlot(stack) != null && item.getEquipmentSlot(stack) == addItem.getEquipmentSlot(toAdd)) return true;
            if (item instanceof ItemArmor && addItem instanceof ItemArmor)
                if (((ItemArmor) item).getEquipmentSlot() == ((ItemArmor) addItem).getEquipmentSlot()) return true;
            if (!Collections.disjoint(item.getToolClasses(stack), addItem.getToolClasses(toAdd))) return true;
        }
        return stack.getItem() == toAdd.getItem();
    }
    
    private static boolean matchesNBT(ItemStack toAdd, ItemStack stack) {
        if (ConfigHandler.nbtMatch != 1) return true;
        if (stack.hasTagCompound() != toAdd.hasTagCompound()) return false;
        if (stack.hasTagCompound()) if (!stack.getTagCompound().equals(toAdd.getTagCompound())) return false;
        return stack.areCapsCompatible(toAdd);
    }
    
}

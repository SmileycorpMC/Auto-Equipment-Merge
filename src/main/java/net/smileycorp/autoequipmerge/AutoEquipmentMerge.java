package net.smileycorp.autoequipmerge;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.smileycorp.autoequipmerge.network.Inventory;
import net.smileycorp.autoequipmerge.network.NetworkHandler;

import java.util.Collections;
import java.util.List;

@Mod(modid = Constants.MODID, name = Constants.NAME, version = Constants.VERSION)
@Mod.EventBusSubscriber(modid = Constants.MODID)
public class AutoEquipmentMerge {
    
    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        NetworkHandler.init();
        ConfigHandler.instance.syncConfig(new Configuration(event.getSuggestedConfigurationFile()));
    }

    @SubscribeEvent
    public static void logIn(PlayerEvent.PlayerLoggedInEvent event) {
        EntityPlayer player = event.player;
        if (player == null) return;
        if (player.world.isRemote) return;
        NetworkHandler.sendSyncMessage((EntityPlayerMP) player);
    }
    
    @SubscribeEvent
    public static void pickup(EntityItemPickupEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player == null) return;
        tryToMerge(player, event.getItem().getItem(), -1);
    }

    public static boolean tryToMerge(EntityPlayer player, ItemStack stack, int slot) {
        ConfigHandler config = ConfigHandler.getInstance(player.world);
        if (!stack.isItemStackDamageable() || (!config.mergePastFull &! stack.isItemDamaged())) return false;
        if (config.mergeArmorSlots && tryToMerge(player, stack, player.inventory.armorInventory, Inventory.ARMOUR, slot)) return true;
        if (tryToMerge(player, stack, player.inventory.mainInventory.subList(0, 9), Inventory.HOTBAR, slot)) return true;
        if (config.mergeOffhand && tryToMerge(player, stack, player.inventory.offHandInventory, Inventory.OFFHAND, slot)) return true;
        return !config.hotbarOnly && tryToMerge(player, stack, player.inventory.mainInventory.subList(9, player.inventory.mainInventory.size()), Inventory.MAIN, slot);
    }
    
    public static boolean tryToMerge(EntityPlayer player, ItemStack toAdd, List<ItemStack> inventory, Inventory type, int slot) {
        ConfigHandler config = ConfigHandler.getInstance(player.world);
        for (int i = 0; i < inventory.size(); i++) {
            int slot1 = type.getSlot((byte) i);
            if (slot1 == slot) continue;
            ItemStack stack = inventory.get(i);
            if (!matches(config, toAdd, stack)) continue;
            if (!matchesNBT(config, toAdd, stack)) continue;
            if (config.isClientSide()) return true;
            int damage = Math.max(toAdd.getMaxDamage() - toAdd.getItemDamage(), 0);
            if (config.mergePastFull || stack.getItemDamage() - damage >= 0) {
                toAdd.shrink(1);
                stack.setItemDamage(Math.max(stack.getItemDamage() - damage, 0));
                player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_PICKUP, player.getSoundCategory(), 0.2f,
                        (player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 1.4f + 2);
            }
            else {
                damage = stack.getItemDamage();
                toAdd.setItemDamage(stack.getMaxDamage() - stack.getItemDamage());
                stack.setItemDamage(0);
            }
            if (config.nbtMatch == 2 && stack.hasTagCompound()) stack.getTagCompound().merge(toAdd.getTagCompound());
            if (config.nbtMatch == 4) stack.setTagCompound(null);
            NetworkHandler.sendMergeMessage((EntityPlayerMP) player, (byte) slot1, damage, stack.getItemDamage());
            return true;
        }
        return false;
    }
    
    private static boolean matches(ConfigHandler config, ItemStack toAdd, ItemStack stack) {
        if (toAdd == stack) return false;
        if (!stack.isItemStackDamageable() || (!config.mergePastFull &! stack.isItemDamaged())) return false;
        if (config.itemMatch == 2) {
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
    
    private static boolean matchesNBT(ConfigHandler config, ItemStack toAdd, ItemStack stack) {
        if (config.nbtMatch != 1) return true;
        if (stack.hasTagCompound() != toAdd.hasTagCompound()) return false;
        if (stack.hasTagCompound()) if (!stack.getTagCompound().equals(toAdd.getTagCompound())) return false;
        return stack.areCapsCompatible(toAdd);
    }
    
}

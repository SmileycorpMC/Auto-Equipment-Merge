package net.smileycorp.autoequipmerge;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Collections;

@Mod(modid = Constants.MODID, name = Constants.NAME, version = Constants.VERSION)
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
        if (tryToMerge(player, stack, player.inventory.armorInventory)) return;
        if (tryToMerge(player, stack, player.inventory.mainInventory)) return;
        tryToMerge(player, stack, player.inventory.offHandInventory);
    }
    
    private static boolean tryToMerge(EntityPlayer player, ItemStack toAdd, NonNullList<ItemStack> inventory) {
        for (int i = 0; i < Math.min(inventory.size(), 9); i++) {
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
            switch (ConfigHandler.nbtMatch) {
                case 2:
                    stack.getTagCompound().merge(toAdd.getTagCompound());
                    break;
                case 4:
                    stack.setTagCompound(null);
                    break;
            }
            return true;
        }
        return false;
    }
    
    private static boolean matches(ItemStack toAdd, ItemStack stack) {
        if (ConfigHandler.itemMatch == 2) {
            Item item = stack.getItem();
            Item addItem = toAdd.getItem();
            if (item instanceof ItemSword && addItem instanceof ItemSword) return true;
            if (item.getEquipmentSlot(stack) != null && item.getEquipmentSlot(stack) == addItem.getEquipmentSlot(toAdd)) return true;
            return !Collections.disjoint(item.getToolClasses(stack), addItem.getToolClasses(toAdd));
        }
        return stack.getItem() == toAdd.getItem();
    }
    
    private static boolean matchesNBT(ItemStack toAdd, ItemStack stack) {
        if (ConfigHandler.nbtMatch != 1) return true;
        if (stack.hasTagCompound() != toAdd.hasTagCompound()) return false;
        if (stack.hasTagCompound() &! stack.getTagCompound().equals(toAdd.getTagCompound())) return false;
        return stack.areCapsCompatible(toAdd);
    }
    
}

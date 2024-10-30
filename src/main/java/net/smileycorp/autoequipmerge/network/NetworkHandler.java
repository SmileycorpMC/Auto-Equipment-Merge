package net.smileycorp.autoequipmerge.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.smileycorp.autoequipmerge.Constants;

public class NetworkHandler {
    
    private static final SimpleNetworkWrapper NETWORK_INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Constants.MODID);
    
    public static void init() {
        NETWORK_INSTANCE.registerMessage(EquipmentMergeMessage::process, EquipmentMergeMessage.class, 0, Side.CLIENT);
    }
    
    public static void sendMessage(EntityPlayerMP player, byte slot, int damage) {
        NETWORK_INSTANCE.sendTo(new EquipmentMergeMessage(slot, damage), player);
    }
    
}

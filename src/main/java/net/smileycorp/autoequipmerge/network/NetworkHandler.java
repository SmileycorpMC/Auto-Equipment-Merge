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
        NETWORK_INSTANCE.registerMessage(SyncSettingsMessage::process, SyncSettingsMessage.class, 1, Side.CLIENT);
    }
    
    public static void sendMergeMessage(EntityPlayerMP player, byte slot, int change, int damage) {
        NETWORK_INSTANCE.sendTo(new EquipmentMergeMessage(slot, change, damage), player);
    }

    public static void sendSyncMessage(EntityPlayerMP player) {
        NETWORK_INSTANCE.sendTo(new SyncSettingsMessage(), player);
    }
    
}

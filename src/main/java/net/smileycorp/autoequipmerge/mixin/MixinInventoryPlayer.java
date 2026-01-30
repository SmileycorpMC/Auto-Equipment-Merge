package net.smileycorp.autoequipmerge.mixin;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.smileycorp.autoequipmerge.AutoEquipmentMerge;
import net.smileycorp.autoequipmerge.ConfigHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryPlayer.class)
public class MixinInventoryPlayer {

    @Shadow public EntityPlayer player;

    @Inject(at = @At("HEAD"), method = "setInventorySlotContents", cancellable = true)
    public void autoequipmerge$setInventorySlotContents(int slot, ItemStack stack, CallbackInfo callback) {
        if (ConfigHandler.getInstance(player.world).pickupOnly) return;
        if (AutoEquipmentMerge.tryToMerge(player, stack)) callback.cancel();
    }
    
}

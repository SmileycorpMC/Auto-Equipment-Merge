package net.smileycorp.autoequipmerge.mixin;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.smileycorp.autoequipmerge.client.ItemMergeRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiContainer.class)
public class MixinGuiContainer {
    
    @Inject(at = @At("HEAD"), method = "drawSlot")
    public void autoequipmerge$renderSlot$HEAD(Slot slot, CallbackInfo ci) {
        if (!slot.getHasStack()) return;
        if (slot.inventory instanceof InventoryPlayer) ItemMergeRenderer.getInstance().shouldRenderAnimations(slot.getSlotIndex());
    }
    
    @Inject(at = @At("RETURN"), method = "drawSlot")
    public void autoequipmerge$renderSlot$RETURN(Slot slot, CallbackInfo ci) {
        if (ItemMergeRenderer.getInstance().hasCurrentSlot()) ItemMergeRenderer.getInstance().clearCurrentSlot();
    }
    
}

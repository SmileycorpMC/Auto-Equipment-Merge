package net.smileycorp.autoequipmerge.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.smileycorp.autoequipmerge.client.ItemMergeRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class MixinItem {
    
    @Inject(at = @At("HEAD"), method = "getDurabilityForDisplay", remap = false, cancellable = true)
    public void autoequipmerge$getDurabilityForDisplay(ItemStack stack, CallbackInfoReturnable<Double> callback) {
        if (ItemMergeRenderer.getInstance().hasCurrentSlot()) callback.setReturnValue(((double)stack.getItemDamage()
                + ItemMergeRenderer.getInstance().getTimeDamage()) / (double)stack.getMaxDamage());
    }
    
}

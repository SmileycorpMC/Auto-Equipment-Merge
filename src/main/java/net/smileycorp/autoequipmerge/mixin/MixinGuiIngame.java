package net.smileycorp.autoequipmerge.mixin;

import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.smileycorp.autoequipmerge.client.ItemMergeRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GuiIngame.class)
public abstract class MixinGuiIngame {
    
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiIngame;renderHotbarItem(IIFLnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;)V"), method = "renderHotbar", locals = LocalCapture.CAPTURE_FAILHARD)
    public void autoequipmerge$renderHotbar$renderHotbarItem(ScaledResolution sr, float partialTicks, CallbackInfo ci, EntityPlayer entityplayer, ItemStack itemstack, EnumHandSide enumhandside, int i, float f, int j, int k, int l) {
        ItemMergeRenderer.getInstance().shouldRenderAnimations(l);
    }
    
    @Inject(at = @At("RETURN"), method = "renderHotbarItem")
    public void autoequipmerge$renderSlot$RETURN(int x, int y, float pt, EntityPlayer player, ItemStack stack, CallbackInfo ci) {
        if (ItemMergeRenderer.getInstance().hasCurrentSlot()) ItemMergeRenderer.getInstance().clearCurrentSlot();
    }
    
    
}

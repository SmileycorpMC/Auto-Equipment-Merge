package net.smileycorp.autoequipmerge.mixin;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.smileycorp.autoequipmerge.client.ItemMergeRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderItem.class)
public class MixinRenderItem {
    
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderItem;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/IBakedModel;)V"), method = "renderItemModelIntoGUI")
    public void autoequipmerge$renderItemModelIntoGUI$HEAD(ItemStack stack, int x, int y, IBakedModel bakedmodel, CallbackInfo ci) {
        if (ItemMergeRenderer.getInstance().renderSwell()) {
            GlStateManager.pushMatrix();
            float p = (float) Math.sin(ItemMergeRenderer.getInstance().getTimePercentage() * Math.PI) * 0.2f;
            GlStateManager.scale(1 + p, 1 + p, 1);
        }
    }
    
    @Inject(at = @At("RETURN"), method = "renderItemModelIntoGUI")
    public void autoequipmerge$renderItemModelIntoGUI$TAIL(ItemStack stack, int x, int y, IBakedModel bakedmodel, CallbackInfo ci) {
        if (ItemMergeRenderer.getInstance().renderSwell()) GlStateManager.popMatrix();
    }
    
}

package net.smileycorp.autoequipmerge.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.smileycorp.autoequipmerge.client.ItemMergeRenderer;

public class EquipmentMergeMessage implements IMessage {
	
	private byte slot = 0;
	private int amount = 0;
	
	public EquipmentMergeMessage() {}
	
	public EquipmentMergeMessage(byte slot, int amount) {
		this.slot = slot;
		this.amount = amount;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		slot = buf.readByte();
		amount = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeByte(slot);
		buf.writeInt(amount);
	}
    
    public IMessage process(MessageContext ctx) {
		if (ctx.side == Side.CLIENT) Minecraft.getMinecraft().addScheduledTask(() -> ItemMergeRenderer.getInstance().setupRenderer(slot, amount));
		return null;
    }
	
}

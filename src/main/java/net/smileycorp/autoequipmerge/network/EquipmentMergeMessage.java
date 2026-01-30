package net.smileycorp.autoequipmerge.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.smileycorp.autoequipmerge.client.ItemMergeRenderer;

public class EquipmentMergeMessage implements IMessage {
	
	private byte slot;
	private int amount;
	private int damage;
	
	public EquipmentMergeMessage() {}
	
	public EquipmentMergeMessage(byte slot, int amount, int damage) {
		this.slot = slot;
		this.amount = amount;
		this.damage = damage;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		slot = buf.readByte();
		amount = buf.readInt();
		damage = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeByte(slot);
		buf.writeInt(amount);
		buf.writeInt(damage);
	}
    
    public IMessage process(MessageContext ctx) {
		if (ctx.side == Side.CLIENT) Minecraft.getMinecraft().addScheduledTask(() -> ItemMergeRenderer.getInstance().setupRenderer(slot, amount, damage));
		return null;
    }
	
}

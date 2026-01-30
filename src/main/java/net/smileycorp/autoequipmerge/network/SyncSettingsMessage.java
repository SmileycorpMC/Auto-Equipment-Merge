package net.smileycorp.autoequipmerge.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.smileycorp.autoequipmerge.ConfigHandler;

public class SyncSettingsMessage implements IMessage {

	private ByteBuf buf;

	public SyncSettingsMessage() {}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.buf = buf.copy();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ConfigHandler.instance.writeToBuf(buf);
	}
    
    public IMessage process(MessageContext ctx) {
		if (ctx.side == Side.CLIENT) Minecraft.getMinecraft().addScheduledTask(() -> ConfigHandler.clientInstance.readFromBuf(buf));
		return null;
    }
	
}

package thebetweenlands.common.network.message.clientbound;

import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.network.message.BLMessage;

public class MessageSyncStaticAspects extends BLMessage {
	private NBTTagCompound nbt;

	public MessageSyncStaticAspects() {}

	public MessageSyncStaticAspects(AspectManager manager) {
		manager.saveStaticAspects(this.nbt = new NBTTagCompound());
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		try {
			this.nbt = buf.readNBTTagCompoundFromBuffer();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeNBTTagCompoundToBuffer(this.nbt);
	}

	@Override
	public IMessage process(MessageContext ctx) {
		if(ctx.side == Side.CLIENT) {
			this.loadAspects();
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	private void loadAspects() {
		World world = TheBetweenlands.proxy.getClientWorld();
		if(world != null) {
			AspectManager.get(world).loadStaticAspects(this.nbt);
		}
	}
}
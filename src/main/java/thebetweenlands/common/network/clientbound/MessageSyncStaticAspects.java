package thebetweenlands.common.network.clientbound;

import java.io.IOException;

import javax.xml.ws.handler.MessageContext;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thebetweenlands.common.DistUtils;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.network.MessageBase;

public class MessageSyncStaticAspects extends MessageBase {
	private NBTTagCompound nbt;

	public MessageSyncStaticAspects() {}

	public MessageSyncStaticAspects(AspectManager manager) {
		manager.saveStaticAspects(this.nbt = new NBTTagCompound());
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		try {
			this.nbt = buf.readCompoundTag();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeCompoundTag(this.nbt);
	}

	@Override
	public IMessage process(MessageContext ctx) {
		if(ctx.side == Dist.CLIENT) {
			this.loadAspects();
		}
		return null;
	}

	@OnlyIn(Dist.CLIENT)
	private void loadAspects() {
		World world = DistUtils.getClientWorld();
		if(world != null) {
			AspectManager.get(world).loadStaticAspects(this.nbt);
		}
	}
}
package thebetweenlands.common.network.serverbound;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thebetweenlands.api.capability.IPuppeteerCapability;
import thebetweenlands.common.network.MessageBase;
import thebetweenlands.common.registries.CapabilityRegistry;

public class MessageUpdatePuppeteerState extends MessageBase {
	private boolean active;

	public MessageUpdatePuppeteerState() { }

	public MessageUpdatePuppeteerState(boolean active) {
		this.active = active;
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeBoolean(this.active);
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		this.active = buf.readBoolean();
	}

	@Override
	public IMessage process(MessageContext ctx) {
		if(ctx.getServerHandler() != null) {
			if(!this.active) {
				EntityPlayer player = ctx.getServerHandler().player;
				IPuppeteerCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_PUPPETEER, null);
				if (cap != null) {
					cap.setActivatingEntity(null);
					cap.setActivatingTicks(0);
				}
			}
		}
		return null;
	}

}

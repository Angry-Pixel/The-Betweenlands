package thebetweenlands.common.network.serverbound;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.IRuneContainer;
import thebetweenlands.common.inventory.container.runechainaltar.ContainerRuneChainAltar;
import thebetweenlands.common.network.MessageBase;

public class MessageSetRuneChainAltarConfiguration extends MessageBase {
	private int runeIndex;
	private int configurationId;

	public MessageSetRuneChainAltarConfiguration() { }

	public MessageSetRuneChainAltarConfiguration(int runeIndex, int configurationId) {
		this.runeIndex = runeIndex;
		this.configurationId = configurationId;
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeInt(this.runeIndex);
		buf.writeInt(this.configurationId);
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		this.runeIndex = buf.readInt();
		this.configurationId = buf.readInt();
	}

	@Override
	public IMessage process(MessageContext ctx) {
		if(this.runeIndex >= 0 && ctx.getServerHandler() != null) {
			EntityPlayer player = ctx.getServerHandler().player;
			if(player.openContainer instanceof ContainerRuneChainAltar) {
				ContainerRuneChainAltar container = (ContainerRuneChainAltar) player.openContainer;
				IRuneContainer runeContainer = container.getRuneContainer(this.runeIndex);
				if(runeContainer != null) {
					for(INodeConfiguration configuration : runeContainer.getBlueprint().getConfigurations()) {
						if(configuration.getId() == this.configurationId) {
							runeContainer.getContext().setConfiguration(configuration);
							break;
						}
					}
				}
			}
		}
		return null;
	}
}

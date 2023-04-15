package thebetweenlands.common.network.serverbound;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thebetweenlands.api.capability.IInfectionCapability;
import thebetweenlands.api.entity.IInfectionBehavior;
import thebetweenlands.common.entity.infection.PlantingInfectionBehavior;
import thebetweenlands.common.network.MessageBase;
import thebetweenlands.common.registries.CapabilityRegistry;

public class MessageInfectionPlantBlock extends MessageBase {
	private BlockPos pos;
	
	public MessageInfectionPlantBlock() { }
	
	public MessageInfectionPlantBlock(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeBlockPos(this.pos);
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		this.pos = buf.readBlockPos();
	}

	@Override
	public IMessage process(MessageContext ctx) {
		if(ctx.getServerHandler() != null && this.pos != null) {
			EntityPlayerMP player = ctx.getServerHandler().player;

			IInfectionCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_INFECTION, null);
			
			if(cap != null) {
				IInfectionBehavior behavior = cap.getCurrentInfectionBehavior();
				
				if(behavior instanceof PlantingInfectionBehavior) {
					((PlantingInfectionBehavior)behavior).handleClientPlanting(this.pos);
				}
			}
		}
		return null;
	}
}

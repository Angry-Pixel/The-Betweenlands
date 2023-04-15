package thebetweenlands.common.network.clientbound;

import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.capability.IInfectionCapability;
import thebetweenlands.api.entity.IInfectionBehavior;
import thebetweenlands.common.entity.infection.AbstractInfectionBehavior;
import thebetweenlands.common.network.MessageEntity;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.InfectionBehaviorRegistry;

public class MessageStopInfectionBehavior extends MessageEntity {
	private ResourceLocation id;

	public MessageStopInfectionBehavior() { }

	public MessageStopInfectionBehavior(AbstractInfectionBehavior behavior) {
		this.addEntity(behavior.getEntity());
		this.id = InfectionBehaviorRegistry.getId(behavior.getClass());
	}

	@Override
	public void serialize(PacketBuffer buf) throws IOException {
		super.serialize(buf);

		if(this.id != null) {
			buf.writeResourceLocation(this.id);
		}
	}

	@Override
	public void deserialize(PacketBuffer buf) throws IOException {
		super.deserialize(buf);

		this.id = buf.readResourceLocation();
	}

	@Override
	public IMessage process(MessageContext ctx) {
		super.process(ctx);

		if(ctx.side == Side.CLIENT && this.id != null) {
			Entity entity = this.getEntity(0);

			if(entity != null) {
				this.handle(entity, id);
			}
		}

		return null;
	}

	@SideOnly(Side.CLIENT)
	protected void handle(Entity entity, ResourceLocation id) {
		IInfectionCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_INFECTION, null);

		IInfectionBehavior behavior = cap.getCurrentInfectionBehavior();

		if(behavior instanceof AbstractInfectionBehavior) {
			AbstractInfectionBehavior aib = (AbstractInfectionBehavior) behavior;

			if(this.id.equals(InfectionBehaviorRegistry.getId(aib.getClass()))) {
				cap.stopInfectionBehavior();
				cap.removeInfectionBehavior();
			}
		}
	}
}

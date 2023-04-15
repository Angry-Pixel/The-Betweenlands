package thebetweenlands.common.network.clientbound;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.capability.IInfectionCapability;
import thebetweenlands.common.entity.infection.AbstractInfectionBehavior;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.InfectionBehaviorRegistry;

public class MessageStartInfectionBehavior extends MessageSyncInfectionBehavior {
	public MessageStartInfectionBehavior() { }

	public MessageStartInfectionBehavior(AbstractInfectionBehavior behavior) {
		super(behavior, true);
	}

	@SideOnly(Side.CLIENT)
	@Override
	protected void handle(Entity entity, ResourceLocation id) {
		if(entity instanceof EntityLivingBase) {
			IInfectionCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_INFECTION, null);

			if(cap != null) {
				InfectionBehaviorRegistry.Factory<?> factory = InfectionBehaviorRegistry.getFactory(id);

				if(factory != null) {
					AbstractInfectionBehavior behavior = factory.create((EntityLivingBase) entity);

					this.syncData(behavior);

					cap.triggerInfectionBehavior(behavior);
				}
			}
		}
	}
}

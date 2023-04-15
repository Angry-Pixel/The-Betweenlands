package thebetweenlands.common.network.clientbound;

import java.io.IOException;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.capability.IInfectionCapability;
import thebetweenlands.api.entity.IInfectionBehavior;
import thebetweenlands.api.network.IGenericDataManagerAccess;
import thebetweenlands.common.entity.infection.AbstractInfectionBehavior;
import thebetweenlands.common.network.MessageEntity;
import thebetweenlands.common.network.datamanager.GenericDataManager;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.InfectionBehaviorRegistry;

public class MessageSyncInfectionBehavior extends MessageEntity {
	private ResourceLocation id;
	private List<IGenericDataManagerAccess.IDataEntry<?>> dataManagerEntries;

	public MessageSyncInfectionBehavior() { }

	public MessageSyncInfectionBehavior(AbstractInfectionBehavior behavior, boolean sendAll) {
		this.addEntity(behavior.getEntity());

		this.id = InfectionBehaviorRegistry.getId(behavior.getClass());

		IGenericDataManagerAccess dataManager = behavior.getDataManager();
		if (sendAll) {
			this.dataManagerEntries = dataManager.getAll();
			dataManager.setClean();
		} else {
			this.dataManagerEntries = dataManager.getDirty();
		}
	}

	@Override
	public void serialize(PacketBuffer buf) throws IOException {
		super.serialize(buf);

		if(this.id != null) {
			buf.writeResourceLocation(this.id);
		}

		GenericDataManager.writeEntries(this.dataManagerEntries, buf);
	}

	@Override
	public void deserialize(PacketBuffer buf) throws IOException {
		super.deserialize(buf);

		this.id = buf.readResourceLocation();

		this.dataManagerEntries = GenericDataManager.readEntries(buf);
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
				this.syncData(aib);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	protected void syncData(AbstractInfectionBehavior behavior) {
		IGenericDataManagerAccess dataManager = behavior.getDataManager();
		if(dataManager != null) {
			dataManager.setValuesFromPacket(this.dataManagerEntries);
		}
	}
}

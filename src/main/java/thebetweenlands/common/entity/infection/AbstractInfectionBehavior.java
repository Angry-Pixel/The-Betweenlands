package thebetweenlands.common.entity.infection;

import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import thebetweenlands.api.capability.IInfectionCapability;
import thebetweenlands.api.entity.IInfectionBehavior;
import thebetweenlands.api.network.IGenericDataManagerAccess.IDataManagedObject;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.network.MessageBase;
import thebetweenlands.common.network.clientbound.MessageStartInfectionBehavior;
import thebetweenlands.common.network.clientbound.MessageStopInfectionBehavior;
import thebetweenlands.common.network.clientbound.MessageSyncInfectionBehavior;
import thebetweenlands.common.network.datamanager.GenericDataManager;
import thebetweenlands.common.registries.CapabilityRegistry;

public abstract class AbstractInfectionBehavior implements IInfectionBehavior, IDataManagedObject {

	protected final EntityLivingBase entity;
	protected final World world;

	protected final GenericDataManager dataManager = new GenericDataManager(this);

	public AbstractInfectionBehavior(EntityLivingBase entity) {
		this.entity = entity;
		this.world = entity.world;
	}

	public EntityLivingBase getEntity() {
		return this.entity;
	}

	public GenericDataManager getDataManager() {
		return this.dataManager;
	}

	@Override
	public void update() {
		if(!this.world.isRemote) {
			this.dataManager.update();

			if(this.dataManager.isDirty()) {
				this.sendMessage(new MessageSyncInfectionBehavior(this, false));
			}
		}
	}

	@Override
	public void start() {
		if(!this.world.isRemote) {
			this.sendMessage(new MessageStartInfectionBehavior(this));
		}
	}

	@Override
	public boolean stop() {
		return true;
	}

	@Override
	public void onRemove() {
		if(!this.world.isRemote) {
			this.sendMessage(new MessageStopInfectionBehavior(this));
		}
	}

	private void sendMessage(MessageBase message) {
		Entity entity = this.getEntity();

		TheBetweenlands.networkWrapper.sendToAllTracking(message, entity);

		if(entity instanceof EntityPlayerMP) {
			TheBetweenlands.networkWrapper.sendTo(message, (EntityPlayerMP) entity);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends IInfectionBehavior> Optional<T> getInfectionBehavior(@Nullable Entity entity, Class<T> cls) {
		if(entity != null) {
			IInfectionCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_INFECTION, null);

			if(cap != null) {
				IInfectionBehavior behavior = cap.getCurrentInfectionBehavior();

				if(cls.isInstance(behavior)) {
					return Optional.of((T) behavior);
				}
			}
		}
		return Optional.empty();
	}
}

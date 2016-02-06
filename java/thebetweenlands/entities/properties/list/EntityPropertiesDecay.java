package thebetweenlands.entities.properties.list;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import thebetweenlands.decay.DecayStats;
import thebetweenlands.entities.properties.EntityProperties;

public class EntityPropertiesDecay extends EntityProperties<EntityPlayer> {
	public DecayStats decayStats = new DecayStats();

	@Override
	public void saveNBTData(NBTTagCompound nbt) {
		this.decayStats.writeNBT(nbt);
	}

	@Override
	public void loadNBTData(NBTTagCompound nbt) {
		this.decayStats.readNBT(nbt);
	}

	@Override
	public String getID() {
		return "blPropertyDecay";
	}

	@Override
	public Class<EntityPlayer> getEntityClass() {
		return EntityPlayer.class;
	}

	@Override
	public int getTrackingTime() {
		return 0;
	}

	@Override
	public boolean saveTrackingSensitiveData(NBTTagCompound nbt) {
		nbt.setInteger("decayLevel", this.decayStats.getDecayLevel());
		return false;
	}

	@Override
	public void loadTrackingSensitiveData(NBTTagCompound nbt) {
		this.decayStats.setDecayLevel(nbt.getInteger("decayLevel"));
	}
}

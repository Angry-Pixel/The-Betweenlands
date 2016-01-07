package thebetweenlands.entities.property;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import thebetweenlands.decay.DecayStats;

public class EntityPropertiesDecay implements IBLExtendedEntityProperties {
	public int decaySyncTimer = 0;
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
	public void init(Entity entity, World world) {

	}

	@Override
	public String getID() {
		return "blPropertyDecay";
	}

	@Override
	public Class<? extends Entity> getEntityClass() {
		return EntityPlayer.class;
	}
}

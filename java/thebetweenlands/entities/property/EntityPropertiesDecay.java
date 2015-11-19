package thebetweenlands.entities.property;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityPropertiesDecay implements IBLExtendedEntityProperties {
	public int decayTimer = 2000;
	public int decayLevel = 20;
	public int syncTimer = 0;

	@Override
	public void saveNBTData(NBTTagCompound nbt) {
		nbt.setInteger("decayLevel", this.decayLevel);
		nbt.setInteger("decayTimer", this.decayTimer);
	}

	@Override
	public void loadNBTData(NBTTagCompound nbt) {
		this.decayLevel = nbt.getInteger("decayLevel");
		this.decayTimer = nbt.getInteger("decayTimer");
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

package thebetweenlands.common.capability.circlegem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.capability.IEntityCircleGemCapability;
import thebetweenlands.api.capability.ISerializableCapability;
import thebetweenlands.common.capability.base.EntityCapability;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;

public class CircleGemEntityCapability extends EntityCapability<CircleGemEntityCapability, IEntityCircleGemCapability, EntityLivingBase> implements IEntityCircleGemCapability, ISerializableCapability {
	@Override
	public ResourceLocation getID() {
		return new ResourceLocation(ModInfo.ID, "entity_gems");
	}

	@Override
	protected CircleGemEntityCapability getDefaultCapabilityImplementation() {
		return new CircleGemEntityCapability();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Capability<IEntityCircleGemCapability> getCapability() {
		return (Capability<IEntityCircleGemCapability>) (Capability<?>) CapabilityRegistry.CAPABILITY_ENTITY_CIRCLE_GEM;
	}

	@Override
	protected Class<IEntityCircleGemCapability> getCapabilityClass() {
		return IEntityCircleGemCapability.class;
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return CircleGemHelper.isApplicable(entity);
	}

	@Override
	public boolean isPersistent(EntityPlayer oldPlayer, EntityPlayer newPlayer, boolean wasDead) {
		return !wasDead || this.getEntity().getEntityWorld().getGameRules().getBoolean("keepInventory");
	}





	private List<CircleGem> gems = new ArrayList<CircleGem>();

	@Override
	public boolean canAdd(CircleGem gem) {
		return true;
	}

	@Override
	public void addGem(CircleGem gem) {
		if(this.canAdd(gem)) {
			this.gems.add(gem);
			this.markDirty();
		}
	}

	@Override
	public boolean removeGem(CircleGem gem) {
		Iterator<CircleGem> gemIT = this.gems.iterator();
		while(gemIT.hasNext()) {
			CircleGem currentGem = gemIT.next();
			if(currentGem.getGemType() == gem.getGemType() && currentGem.getCombatType() == gem.getCombatType()) {
				gemIT.remove();
				this.markDirty();
				return true;
			}
		}
		return false;
	}

	@Override
	public List<CircleGem> getGems() {
		return Collections.unmodifiableList(this.gems);
	}

	@Override
	public boolean removeAll() {
		boolean hadGems = !this.gems.isEmpty();
		this.gems.clear();
		return hadGems;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		NBTTagList gemList = new NBTTagList();
		for(CircleGem gem : this.gems) {
			NBTTagCompound gemCompound = new NBTTagCompound();
			gem.writeToNBT(gemCompound);
			gemList.appendTag(gemCompound);
		}
		nbt.setTag("gems", gemList);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.gems.clear();
		NBTTagList gemList = nbt.getTagList("gems", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < gemList.tagCount(); i++) {
			NBTTagCompound gemCompound = gemList.getCompoundTagAt(i);
			CircleGem gem = CircleGem.readFromNBT(gemCompound);
			if(gem != null) {
				this.gems.add(gem);
			}
		}
	}

	@Override
	public void writeTrackingDataToNBT(NBTTagCompound nbt) {
		this.writeToNBT(nbt);
	}

	@Override
	public void readTrackingDataFromNBT(NBTTagCompound nbt) {
		this.readFromNBT(nbt);
	}

	@Override
	public int getTrackingTime() {
		return 0;
	}
}

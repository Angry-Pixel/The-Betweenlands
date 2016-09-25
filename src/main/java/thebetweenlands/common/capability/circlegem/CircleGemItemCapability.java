package thebetweenlands.common.capability.circlegem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.common.capability.base.ItemCapability;
import thebetweenlands.common.capability.circlegem.CircleGem.CombatType;
import thebetweenlands.common.item.misc.ItemGem;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;

public class CircleGemItemCapability extends ItemCapability<CircleGemItemCapability, IItemCircleGemCapability> implements IItemCircleGemCapability {
	@Override
	public ResourceLocation getID() {
		return new ResourceLocation(ModInfo.ID, "item_gems");
	}

	@Override
	protected CircleGemItemCapability getDefaultCapabilityImplementation() {
		return new CircleGemItemCapability();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Capability<IItemCircleGemCapability> getCapability() {
		return (Capability<IItemCircleGemCapability>) (Capability<?>) CapabilityRegistry.CAPABILITY_ITEM_CIRCLE_GEM;
	}

	@Override
	protected Class<IItemCircleGemCapability> getCapabilityClass() {
		return IItemCircleGemCapability.class;
	}

	@Override
	public boolean isApplicable(ItemStack stack) {
		return CircleGemHelper.isApplicable(stack);
	}






	private List<CircleGem> gems = new ArrayList<CircleGem>();

	protected boolean isItemGem() {
		return this.getItemStack().getItem() instanceof ItemGem;
	}

	@Override
	protected void init() {
		if(this.isItemGem()) {
			this.gems.add(new CircleGem(((ItemGem)this.getItemStack().getItem()).type, CombatType.BOTH));
		}
	}

	@Override
	public boolean canAdd(CircleGem gem) {
		return this.gems.isEmpty();
	}

	@Override
	public void addGem(CircleGem gem) {
		if(this.canAdd(gem)) {
			this.gems.add(gem);
		}
	}

	@Override
	public boolean removeGem(CircleGem gem) {
		if(this.isItemGem()) {
			return false;
		}
		Iterator<CircleGem> gemIT = this.gems.iterator();
		while(gemIT.hasNext()) {
			CircleGem currentGem = gemIT.next();
			if(currentGem.getGemType() == gem.getGemType() && currentGem.getCombatType() == gem.getCombatType()) {
				gemIT.remove();
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
		if(this.isItemGem()) {
			return false;
		}
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
}

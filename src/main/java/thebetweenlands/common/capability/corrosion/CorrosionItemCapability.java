package thebetweenlands.common.capability.corrosion;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import thebetweenlands.common.capability.base.ISerializableCapability;
import thebetweenlands.common.capability.base.ItemCapability;
import thebetweenlands.common.item.corrosion.ICorrodible;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;

public class CorrosionItemCapability extends ItemCapability<CorrosionItemCapability, ICorrosionCapability> implements ICorrosionCapability, ISerializableCapability {
	@Override
	public ResourceLocation getID() {
		return new ResourceLocation(ModInfo.ID, "item_corrosion");
	}

	@Override
	protected CorrosionItemCapability getDefaultCapabilityImplementation() {
		return new CorrosionItemCapability();
	}

	@Override
	protected Capability<ICorrosionCapability> getCapability() {
		return CapabilityRegistry.CAPABILITY_CORROSION;
	}

	@Override
	protected Class<ICorrosionCapability> getCapabilityClass() {
		return ICorrosionCapability.class;
	}

	@Override
	public boolean isApplicable(ItemStack stack) {
		return stack.getItem() instanceof ICorrodible;
	}




	private int corrosion = 0;
	private int coating;

	@Override
	public int getCorrosion() {
		return this.corrosion;
	}

	@Override
	public void setCorrosion(int corrosion) {
		this.corrosion = corrosion;
	}

	@Override
	public int getCoating() {
		return this.coating;
	}

	@Override
	public void setCoating(int coating) {
		this.coating = coating;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("corrosion", this.corrosion);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.corrosion = nbt.getInteger("corrosion");
	}
}

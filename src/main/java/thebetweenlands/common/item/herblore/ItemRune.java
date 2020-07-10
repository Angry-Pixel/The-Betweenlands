package thebetweenlands.common.item.herblore;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.ITintedItem;
import thebetweenlands.common.registries.AspectRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.util.NBTHelper;

public class ItemRune extends Item implements ITintedItem, ItemRegistry.IMultipleItemModelDefinition {
	private static final String NBT_ASPECT_TYPE = "thebetweenlands.rune.aspect_type";

	public ItemRune() {
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setCreativeTab(BLCreativeTabs.HERBLORE);

		this.addPropertyOverride(new ResourceLocation("infused"), (stack, worldIn, entityIn) -> {
			return this.getInfusedAspect(stack) != null ? 1.0f : 0.0f;
		});
	}

	public ItemStack createInfused(ItemStack stack, IAspectType type) {
		ItemStack infused = stack.copy();
		NBTTagCompound nbt = NBTHelper.getStackNBTSafe(infused);
		nbt.setTag(NBT_ASPECT_TYPE, type.writeToNBT(new NBTTagCompound()));
		return infused;
	}

	@Nullable
	public IAspectType getInfusedAspect(ItemStack stack) {
		if(stack.hasTagCompound()) {
			return IAspectType.readFromNBT(stack.getTagCompound().getCompoundTag(NBT_ASPECT_TYPE));
		}
		return null;
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if(this.isInCreativeTab(tab)) {
			for(int i = 0; i < 4; i++) {
				items.add(new ItemStack(this, 1, i));
			}

			for(IAspectType type : AspectRegistry.ASPECT_TYPES) {
				for(int i = 0; i < 4; i++) {
					items.add(this.createInfused(new ItemStack(this, 1, i), type));
				}
			}
		}
	}

	@Override
	public Map<Integer, ResourceLocation> getModels() {
		ResourceLocation regName = this.getRegistryName();
		Map<Integer, ResourceLocation> models = new HashMap<>();
		models.put(0, new ResourceLocation(regName.getNamespace(), String.format("%s_%s", regName.getPath(), "initiate")));
		models.put(1, new ResourceLocation(regName.getNamespace(), String.format("%s_%s", regName.getPath(), "token")));
		models.put(2, new ResourceLocation(regName.getNamespace(), String.format("%s_%s", regName.getPath(), "gate")));
		models.put(3, new ResourceLocation(regName.getNamespace(), String.format("%s_%s", regName.getPath(), "conduct")));
		return models;
	}

	@Override
	public int getColorMultiplier(ItemStack stack, int tintIndex) {
		if(tintIndex == 1) {
			IAspectType aspect = this.getInfusedAspect(stack);
			return aspect != null ? aspect.getColor() : 0xFFFFFFFF;
		}
		return 0xFFFFFFFF;
	}
}

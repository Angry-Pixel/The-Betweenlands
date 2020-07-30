package thebetweenlands.common.item.herblore.rune;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.api.item.IRuneItem;
import thebetweenlands.api.rune.IRuneContainerFactory;
import thebetweenlands.api.rune.RuneCategory;
import thebetweenlands.api.rune.RuneTier;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.ITintedItem;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.util.NBTHelper;

public class ItemRune extends Item implements ITintedItem, ItemRegistry.IMultipleItemModelDefinition, IRuneItem {
	private static final Map<Triple<Integer, Integer, IAspectType>, IRuneContainerFactory> REGISTRY = new HashMap<>();

	private static final String NBT_ASPECT_TYPE = "thebetweenlands.rune.aspect_type";

	public final ResourceLocation material;

	public ItemRune(ResourceLocation material) {
		this.material = material;
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setCreativeTab(BLCreativeTabs.HERBLORE);

		this.addPropertyOverride(new ResourceLocation("infused"), (stack, worldIn, entityIn) -> {
			return this.getInfusedAspect(stack) != null ? 1.0f : 0.0f;
		});

		this.addPropertyOverride(new ResourceLocation("category"), (stack, worldIn, entityIn) -> {
			return this.getCategory(stack);
		});

		this.addPropertyOverride(new ResourceLocation("tier"), (stack, worldIn, entityIn) -> {
			return this.getTier(stack);
		});
	}

	public static void register(RuneCategory category, RuneTier tier, IAspectType type, IRuneContainerFactory factory) {
		REGISTRY.put(Triple.of(category.id, tier.id, type), factory);
	}

	@Nullable
	public static IRuneContainerFactory getFactory(RuneCategory category, RuneTier tier, IAspectType type) {
		return REGISTRY.get(Triple.of(category.id, tier.id, type));
	}

	@Nullable
	public static IRuneContainerFactory getFactory(int category, int tier, IAspectType type) {
		return REGISTRY.get(Triple.of(category, tier, type));
	}

	public IRuneContainerFactory getRuneContainerFactory(ItemStack stack) {
		return getFactory(this.getCategory(stack), this.getTier(stack), this.getInfusedAspect(stack));
	}

	@Override
	public ItemStack infuse(ItemStack stack, IAspectType type, RuneTier tier) {
		ItemStack infused = stack.copy();
		infused.setItemDamage(this.getCategory(stack) * RuneTier.COUNT + tier.id);
		NBTTagCompound nbt = NBTHelper.getStackNBTSafe(infused);
		nbt.setTag(NBT_ASPECT_TYPE, type.writeToNBT(new NBTTagCompound()));
		return infused;
	}

	public ItemStack carve(RuneCategory category) {
		return new ItemStack(this, 1, category.id * RuneTier.COUNT);
	}

	public int getCategory(ItemStack stack) {
		return stack.getItemDamage() / RuneTier.COUNT;
	}

	public int getTier(ItemStack stack) {
		return stack.getItemDamage() % RuneTier.COUNT;
	}

	@Override
	public IAspectType getInfusedAspect(ItemStack stack) {
		if(stack.hasTagCompound()) {
			NBTTagCompound nbt = stack.getTagCompound();
			if(nbt.hasKey(NBT_ASPECT_TYPE, Constants.NBT.TAG_COMPOUND)) {
				return IAspectType.readFromNBT(nbt.getCompoundTag(NBT_ASPECT_TYPE));
			}
		}
		return null;
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if(this.isInCreativeTab(tab)) {
			//Testing
			/*for(RuneCategory category : RuneCategory.VALUES) {
				items.add(this.carve(category));

				for(RuneTier tier : RuneTier.VALUES) {
					for(IAspectType aspect : AspectRegistry.ASPECT_TYPES) {
						items.add(this.infuse(this.carve(category), aspect, tier));
					}
				}
			}*/

			Set<RuneCategory> usedCategories = new HashSet<>();
			for(Triple<Integer, Integer, IAspectType> key : REGISTRY.keySet()) {
				usedCategories.add(RuneCategory.fromId(key.getLeft()));
			}
			for(RuneCategory category : usedCategories) {
				items.add(this.carve(category));
			}

			for(Triple<Integer, Integer, IAspectType> key : REGISTRY.keySet()) {
				items.add(this.infuse(this.carve(RuneCategory.fromId(key.getLeft())), key.getRight(), RuneTier.fromId(key.getMiddle())));
			}
		}
	}

	@Override
	public Map<Integer, ResourceLocation> getModels() {
		ResourceLocation regName = this.getRegistryName();
		Map<Integer, ResourceLocation> models = new HashMap<>();
		for(RuneCategory category : RuneCategory.values()) {
			for(RuneTier tier : RuneTier.values()) {
				models.put(category.id * RuneTier.COUNT + tier.id, new ResourceLocation(regName.getNamespace(), String.format("%s_%s", regName.getPath(), category.name)));
			}
		}
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

	@Override
	public String getTranslationKey() {
		return "item.thebetweenlands.unknown_rune";
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		return String.format("item.%s.%s_rune.%s", this.getRegistryName().getNamespace(), RuneCategory.fromId(this.getCategory(stack)).name, this.getInfusedAspect(stack) != null ? "infused" : "carved");
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		IAspectType aspect = this.getInfusedAspect(stack);

		if(aspect != null) {
			String runeName;

			IRuneContainerFactory factory = getFactory(this.getCategory(stack), this.getTier(stack), this.getInfusedAspect(stack));

			if(factory != null) {
				runeName = I18n.translateToLocal(String.format("rune.%s.%s.name", factory.getId().getNamespace(), factory.getId().getPath()));
			} else {
				runeName = I18n.translateToLocal("rune.thebetweenlands.no_effect.name");
			}

			return I18n.translateToLocalFormatted(this.getUnlocalizedNameInefficiently(stack) + ".name", runeName).trim();
		} else {
			return super.getItemStackDisplayName(stack);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		String runeMaterial = I18n.translateToLocal(String.format("rune_material.%s.%s.name", this.material.getNamespace(), this.material.getPath()));

		IAspectType aspect = this.getInfusedAspect(stack);

		if(aspect != null) {
			String runeName;

			IRuneContainerFactory factory = getFactory(this.getCategory(stack), this.getTier(stack), this.getInfusedAspect(stack));

			if(factory != null) {
				runeName = I18n.translateToLocal(String.format("rune.%s.%s.name", factory.getId().getNamespace(), factory.getId().getPath()));
			} else {
				runeName = I18n.translateToLocal("rune.thebetweenlands.no_effect.name");
			}

			String aspectName = this.getInfusedAspect(stack).getName();
			String tierName = I18n.translateToLocal(String.format("rune_tier.%s.name", RuneTier.fromId(this.getTier(stack)).name));

			tooltip.addAll(ItemTooltipHandler.splitTooltip(
					I18n.translateToLocalFormatted(
							String.format("tooltip.%s.%s_rune.infused", this.getRegistryName().getNamespace(), RuneCategory.fromId(this.getCategory(stack)).name),
							runeMaterial, aspectName, tierName, runeName
							).trim(), 0));
		} else {
			tooltip.addAll(ItemTooltipHandler.splitTooltip(
					I18n.translateToLocalFormatted(
							String.format("tooltip.%s.%s_rune.carved", this.getRegistryName().getNamespace(), RuneCategory.fromId(this.getCategory(stack)).name),
							runeMaterial
							).trim(), 0));
		}
	}

	@Override
	public RuneCategory getRuneCategory(ItemStack stack) {
		return RuneCategory.fromId(this.getCategory(stack));
	}
}

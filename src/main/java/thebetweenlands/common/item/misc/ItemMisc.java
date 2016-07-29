package thebetweenlands.common.item.misc;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.CaseFormat;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.item.IGenericItem;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.ItemRegistry;

public class ItemMisc extends Item implements ItemRegistry.ISubItemsItem {
	public ItemMisc() {
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
		Stream.of(EnumItemMisc.values()).forEach(t -> list.add(t.create(1)));
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		try {
			return "item.thebetweenlands." + IGenericItem.getFromStack(EnumItemMisc.class, stack).getUnlocalizedName();
		} catch (Exception e) {
			return "item.thebetweenlands.unknownGeneric";
		}
	}

	@Override
	public List<ResourceLocation> getModels() {
		return Stream.of(EnumItemMisc.values()).map(t -> new ResourceLocation(ModInfo.ID, t.getModelName())).collect(Collectors.toList());
	}

	public enum EnumItemMisc implements IGenericItem {
		BLOOD_SNAIL_SHELL,
		MIRE_SNAIL_SHELL,
		COMPOST,
		DRAGONFLY_WING,
		LURKER_SKIN,
		SWAMP_REED,
		DRIED_SWAMP_REED,
		SWAMP_REED_ROPE,
		TANGLED_ROOT,
		PLANT_TONIC,
		MUD_BRICK,
		SYRMORITE_INGOT,
		OCTINE_INGOT,
		ROTTEN_BARK,
		SLIMY_BONE,
		SLUDGE_BALL,
		SNAPPER_ROOT,
		STALKER_EYE,
		SULFUR,
		VALONITE_SHARD,
		WEEDWOOD_STICK,
		ANGLER_TOOTH,
		WEEDWOOD_BOWL,
		RUBBER_BALL,
		TAR_BEAST_HEART,
		TAR_BEAST_HEART_ANIMATED,
		TAR_DRIP,
		LIMESTONE_FLUX,
		SWAMP_KELP_ITEM,
		INANIMATE_TARMINION,
		POISON_GLAND,
		ASPECTRUS_FRUIT,
		PARCHMENT,
		SHOCKWAVE_SWORD_1,
		SHOCKWAVE_SWORD_2,
		SHOCKWAVE_SWORD_3,
		SHOCKWAVE_SWORD_4,
		PYRAD_FLAME,
		AMULET_SOCKET,
		SCABYST;

		private final String unlocalizedName;
		private final String modelName;

		EnumItemMisc() {
		    this.modelName = this.name().toLowerCase(Locale.ENGLISH);
			this.unlocalizedName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, this.modelName);
		}

		@Override
		public String getUnlocalizedName() {
			return this.unlocalizedName;
		}

        @Override
        public String getModelName() {
            return this.modelName;
        }

		@Override
		public int getID() {
			return this.ordinal();
		}

		@Override
		public Item getItem() {
			return ItemRegistry.ITEMS_MISC;
		}
	}
}

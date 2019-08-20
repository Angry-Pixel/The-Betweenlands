package thebetweenlands.common.item.misc;

import com.google.common.base.CaseFormat;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.item.IGenericItem;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

public class ItemMisc extends Item implements ItemRegistry.IMultipleItemModelDefinition {
	public ItemMisc() {
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
		if (this.isInCreativeTab(tab)) {
			Stream.of(EnumItemMisc.values()).forEach(t -> list.add(t.create(1)));
		}
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		try {
			return "item.thebetweenlands." + IGenericItem.getFromStack(EnumItemMisc.class, stack).getTranslationKey();
		} catch (Exception e) {
			return "item.thebetweenlands.unknown_generic";
		}
	}

	@Override
	public Map<Integer, ResourceLocation> getModels() {
		Map<Integer, ResourceLocation> models = new HashMap<>();
		for(EnumItemMisc type : EnumItemMisc.values()) {
			models.put(type.getID(), new ResourceLocation(ModInfo.ID, type.getModelName()));
		}
		return models;
	}
	
	@Override
	public EnumRarity getRarity(ItemStack stack) {
		if(EnumItemMisc.SCROLL.isItemOf(stack) || EnumItemMisc.TAR_BEAST_HEART.isItemOf(stack) || EnumItemMisc.TAR_BEAST_HEART_ANIMATED.isItemOf(stack)
				|| EnumItemMisc.INANIMATE_TARMINION.isItemOf(stack)) {
			return EnumRarity.UNCOMMON;
		} else if(EnumItemMisc.AMULET_SOCKET.isItemOf(stack)) {
			return EnumRarity.RARE;
		}
		return super.getRarity(stack);
	}

	public enum EnumItemMisc implements IGenericItem {
		BLOOD_SNAIL_SHELL(0),
		MIRE_SNAIL_SHELL(1),
		COMPOST(2),
		DRAGONFLY_WING(3),
		LURKER_SKIN(4),
		DRIED_SWAMP_REED(6),
		SWAMP_REED_ROPE(7),
		MUD_BRICK(10),
		SYRMORITE_INGOT(11),
		DRY_BARK(13),
		SLIMY_BONE(14),
		SNAPPER_ROOT(16),
		STALKER_EYE(17),
		SULFUR(18),
		VALONITE_SHARD(19),
		WEEDWOOD_STICK(20),
		ANGLER_TOOTH(21),
		WEEDWOOD_BOWL(22),
		RUBBER_BALL(23),
		TAR_BEAST_HEART(24),
		TAR_BEAST_HEART_ANIMATED(25),
		TAR_DRIP(26),
		LIMESTONE_FLUX(27),
		INANIMATE_TARMINION(29),
		POISON_GLAND(30),
		PARCHMENT(32),
		SHOCKWAVE_SWORD_1(33),
		SHOCKWAVE_SWORD_2(34),
		SHOCKWAVE_SWORD_3(35),
		SHOCKWAVE_SWORD_4(36),
		AMULET_SOCKET(38),
		SCABYST(39),
		SCROLL(40),
		SYRMORITE_NUGGET(41),
		OCTINE_NUGGET(42),
		VALONITE_SPLINTER(43),
		CREMAINS(44),
		UNDYING_EMBER(45);

		private final int id;
		private final String unlocalizedName;
		private final String modelName;

		EnumItemMisc(int id) {
			this.id = id;
			this.modelName = this.name().toLowerCase(Locale.ENGLISH);
			this.unlocalizedName = this.modelName;
		}

		@Override
		public String getTranslationKey() {
			return this.unlocalizedName;
		}

		@Override
		public String getModelName() {
			return this.modelName;
		}

		@Override
		public int getID() {
			return this.id;
		}

		@Override
		public Item getItem() {
			return ItemRegistry.ITEMS_MISC;
		}
	}
}

package thebetweenlands.common.item.misc;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.entity.mobs.EntityEmberling;
import thebetweenlands.common.entity.mobs.EntityEmberlingWild;
import thebetweenlands.common.item.IGenericItem;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.ItemRegistry;

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
		} else if(EnumItemMisc.AMULET_SOCKET.isItemOf(stack) || EnumItemMisc.LOOT_SCRAPS.isItemOf(stack) || EnumItemMisc.FABRICATED_SCROLL.isItemOf(stack)) {
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
		UNDYING_EMBER(45),
		INANIMATE_ANGRY_PEBBLE(46),
		ANCIENT_REMNANT(47),
		LOOT_SCRAPS(48),
		FABRICATED_SCROLL(49),
		BETWEENSTONE_PEBBLE(50);

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
	
	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
		if (target instanceof EntityEmberlingWild && EnumItemMisc.UNDYING_EMBER.isItemOf(stack)) {
			EntityEmberlingWild oldEmberling = (EntityEmberlingWild) target;
			EntityEmberling newEmberling = new EntityEmberling(player.getEntityWorld());
			if (!player.getEntityWorld().isRemote) {
				
				newEmberling.copyLocationAndAnglesFrom(oldEmberling);
				newEmberling.setTamedBy(player);
				player.getEntityWorld().removeEntity(oldEmberling);
				player.getEntityWorld().spawnEntity(newEmberling);

				if (!player.capabilities.isCreativeMode) {
					stack.shrink(1);
					if (stack.getCount() <= 0)
						player.setHeldItem(hand, ItemStack.EMPTY);
				}
				return true;
			} else {
				oldEmberling.playTameEffect(true);
				return true;
			}
		} else {
			return false;
		}
	}
}

package thebetweenlands.common.item.tools;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import thebetweenlands.api.item.IAnimatorRepairable;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.EntityTinyWormEggSac;
import thebetweenlands.common.entity.mobs.EntityChiromawHatchling;
import thebetweenlands.common.entity.mobs.EntityChiromawTame;
import thebetweenlands.common.entity.mobs.EntityDragonFly;
import thebetweenlands.common.entity.mobs.EntityFirefly;
import thebetweenlands.common.entity.mobs.EntityGecko;
import thebetweenlands.common.item.misc.ItemMob;
import thebetweenlands.common.registries.ItemRegistry;

public class ItemNet extends Item implements IAnimatorRepairable {
	public static final Multimap<Class<? extends Entity>, Pair<Supplier<? extends ItemMob>, Predicate<Entity>>> CATCHABLE_ENTITIES = MultimapBuilder.hashKeys().arrayListValues().build();

	@SuppressWarnings("unchecked")
	public static <T extends Entity> void register(Class<T> cls, Supplier<? extends ItemMob> item, Predicate<T> predicate) {
		CATCHABLE_ENTITIES.put(cls, Pair.of(item, (Predicate<Entity>) predicate));
	}

	static {
		register(EntityFirefly.class, () -> ItemRegistry.CRITTER, e -> true);
		register(EntityGecko.class, () -> ItemRegistry.CRITTER, e -> true);
		register(EntityDragonFly.class, () -> ItemRegistry.CRITTER, e -> true);
		register(EntityTinyWormEggSac.class, () -> ItemRegistry.SLUDGE_WORM_EGG_SAC, e -> true);
		register(EntityChiromawHatchling.class, () -> ItemRegistry.CHIROMAW_EGG, e -> (!e.getHasHatched() && !e.getElectricBoogaloo()));
		register(EntityChiromawHatchling.class, () -> ItemRegistry.CHIROMAW_EGG_LIGHTNING, e -> (!e.getHasHatched() && e.getElectricBoogaloo()));
		register(EntityChiromawTame.class, () -> ItemRegistry.CHIROMAW_TAME, e -> !e.getElectricBoogaloo());
		register(EntityChiromawTame.class, () -> ItemRegistry.CHIROMAW_TAME_LIGHTNING, e -> e.getElectricBoogaloo());
	}

	public ItemNet() {
		this.maxStackSize = 1;
		this.setMaxDamage(32);
		this.setCreativeTab(BLCreativeTabs.GEARS);
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
		Collection<Pair<Supplier<? extends ItemMob>, Predicate<Entity>>> entries = CATCHABLE_ENTITIES.get(target.getClass());

		if(entries != null) {
			if(!player.world.isRemote) {
				for(Pair<Supplier<? extends ItemMob>, Predicate<Entity>> entry : entries) {
					if(entry.getRight().test(target)) {
						ItemMob item = entry.getLeft().get();

						ItemStack mobItemStack = item.capture(target);

						if(!mobItemStack.isEmpty()) {
							target.setDropItemsWhenDead(false);
							target.setDead();

							player.world.spawnEntity(new EntityItem(player.world, player.posX, player.posY, player.posZ, mobItemStack));

							stack.damageItem(1, player);

							break;
						}
					}
				}
			}

			player.swingArm(hand);
			return true;
		}
		return false;
	}

	@Override
	public int getMinRepairFuelCost(ItemStack stack) {
		return 2;
	}

	@Override
	public int getFullRepairFuelCost(ItemStack stack) {
		return 8;
	}

	@Override
	public int getMinRepairLifeCost(ItemStack stack) {
		return 4;
	}

	@Override
	public int getFullRepairLifeCost(ItemStack stack) {
		return 12;
	}
}

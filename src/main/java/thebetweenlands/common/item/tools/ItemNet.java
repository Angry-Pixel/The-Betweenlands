package thebetweenlands.common.item.tools;

import java.util.Collection;
import java.util.function.BiPredicate;
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
import thebetweenlands.common.entity.mobs.EntityAnadia;
import thebetweenlands.common.entity.mobs.EntityCaveFish;
import thebetweenlands.common.entity.mobs.EntityChiromawHatchling;
import thebetweenlands.common.entity.mobs.EntityChiromawTame;
import thebetweenlands.common.entity.mobs.EntityDragonFly;
import thebetweenlands.common.entity.mobs.EntityFirefly;
import thebetweenlands.common.entity.mobs.EntityFreshwaterUrchin;
import thebetweenlands.common.entity.mobs.EntityGecko;
import thebetweenlands.common.entity.mobs.EntityJellyfish;
import thebetweenlands.common.entity.mobs.EntityMireSnail;
import thebetweenlands.common.entity.mobs.EntityOlm;
import thebetweenlands.common.entity.mobs.EntityTinySludgeWorm;
import thebetweenlands.common.entity.mobs.EntityTinySludgeWormHelper;
import thebetweenlands.common.item.misc.ItemMob;
import thebetweenlands.common.registries.ItemRegistry;

public class ItemNet extends Item implements IAnimatorRepairable {
	public static final Multimap<Class<? extends Entity>, Pair<Supplier<? extends ItemMob>, BiPredicate<EntityPlayer, Entity>>> CATCHABLE_ENTITIES = MultimapBuilder.hashKeys().arrayListValues().build();

	@SuppressWarnings("unchecked")
	public static <T extends Entity> void register(Class<T> cls, Supplier<? extends ItemMob> item, BiPredicate<EntityPlayer, T> predicate) {
		CATCHABLE_ENTITIES.put(cls, Pair.of(item, (BiPredicate<EntityPlayer, Entity>) predicate));
	}

	static {
		register(EntityFirefly.class, () -> ItemRegistry.CRITTER, (p, e) -> true);
		register(EntityGecko.class, () -> ItemRegistry.CRITTER, (p, e) -> true);
		register(EntityDragonFly.class, () -> ItemRegistry.CRITTER, (p, e) -> true);
		register(EntityTinyWormEggSac.class, () -> ItemRegistry.SLUDGE_WORM_EGG_SAC, (p, e) -> true);
		register(EntityChiromawHatchling.class, () -> ItemRegistry.CHIROMAW_EGG, (p, e) -> !e.getHasHatched() && !e.getElectricBoogaloo());
		register(EntityChiromawHatchling.class, () -> ItemRegistry.CHIROMAW_EGG_LIGHTNING, (p, e) -> !e.getHasHatched() && e.getElectricBoogaloo());
		register(EntityChiromawTame.class, () -> ItemRegistry.CHIROMAW_TAME, (p, e) -> e.getOwner() == p && !e.getElectricBoogaloo());
		register(EntityChiromawTame.class, () -> ItemRegistry.CHIROMAW_TAME_LIGHTNING, (p, e) -> e.getOwner() == p && e.getElectricBoogaloo());
		register(EntityTinySludgeWorm.class, () -> ItemRegistry.TINY_SLUDGE_WORM, (p, e) -> true);
		register(EntityTinySludgeWormHelper.class, () -> ItemRegistry.TINY_SLUDGE_WORM_HELPER, (p, e) -> true);
		register(EntityMireSnail.class, () -> ItemRegistry.CRITTER, (p, e) -> true);
		register(EntityAnadia.class, () -> ItemRegistry.ANADIA, (p, e) -> e.getNettableTimer() > 0);
		register(EntityFreshwaterUrchin.class, () -> ItemRegistry.FRESHWATER_URCHIN, (p, e) -> true);
		register(EntityCaveFish.class, () -> ItemRegistry.CRITTER, (p, e) -> true);
		register(EntityOlm.class, () -> ItemRegistry.CRITTER, (p, e) -> true);
		register(EntityJellyfish.class, () -> ItemRegistry.JELLYFISH, (p, e) -> true);
	}

	public ItemNet() {
		this.maxStackSize = 1;
		this.setMaxDamage(32);
		this.setCreativeTab(BLCreativeTabs.GEARS);
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
		Collection<Pair<Supplier<? extends ItemMob>, BiPredicate<EntityPlayer, Entity>>> entries = CATCHABLE_ENTITIES.get(target.getClass());

		if(entries != null) {
			for(Pair<Supplier<? extends ItemMob>, BiPredicate<EntityPlayer, Entity>> entry : entries) {
				if(entry.getRight().test(player, target)) {
					ItemMob item = entry.getLeft().get();

					player.swingArm(hand);

					if(!player.world.isRemote) {
						ItemStack mobItemStack = item.capture(target);

						if(!mobItemStack.isEmpty()) {
							target.setDropItemsWhenDead(false);
							target.setDead();

							player.world.spawnEntity(new EntityItem(player.world, player.posX, player.posY, player.posZ, mobItemStack));

							stack.damageItem(1, player);

							item.onCapturedByPlayer(player, hand, mobItemStack, target);

							return true;
						}
					} else {
						return true;
					}
				}
			}
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

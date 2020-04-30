package thebetweenlands.common.item.tools;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

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
	public static final Map<Class<? extends Entity>, Supplier<? extends ItemMob>> CATCHABLE_ENTITIES = new HashMap<>();

	static {
		CATCHABLE_ENTITIES.put(EntityFirefly.class, () -> ItemRegistry.CRITTER);
		CATCHABLE_ENTITIES.put(EntityGecko.class, () -> ItemRegistry.CRITTER);
		CATCHABLE_ENTITIES.put(EntityDragonFly.class, () -> ItemRegistry.CRITTER);
		CATCHABLE_ENTITIES.put(EntityTinyWormEggSac.class, () -> ItemRegistry.SLUDGE_WORM_EGG_SAC);
		CATCHABLE_ENTITIES.put(EntityChiromawHatchling.class, () -> ItemRegistry.CHIROMAW_EGG);
		CATCHABLE_ENTITIES.put(EntityChiromawTame.class, () -> ItemRegistry.CRITTER);
	}

	public ItemNet() {
		this.maxStackSize = 1;
		this.setMaxDamage(32);
		this.setCreativeTab(BLCreativeTabs.GEARS);
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
		Supplier<? extends ItemMob> mobItem = CATCHABLE_ENTITIES.get(target.getClass());

		if(mobItem != null) {
			if(!player.world.isRemote) {

				if(exludeThisMob(target))
					return false;

				ItemMob item = mobItem.get();

				ItemStack mobItemStack = item.capture(target);

				if(!mobItemStack.isEmpty()) {
					target.setDropItemsWhenDead(false);
					target.setDead();

					player.world.spawnEntity(new EntityItem(player.world, player.posX, player.posY, player.posZ, mobItemStack));

					stack.damageItem(1, player);
				}
			}

			player.swingArm(hand);
			return true;
		}
		return false;
	}

	private boolean exludeThisMob(EntityLivingBase target) {
		if(target instanceof EntityChiromawHatchling) {
			if(((EntityChiromawHatchling) target).getHasHatched())
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

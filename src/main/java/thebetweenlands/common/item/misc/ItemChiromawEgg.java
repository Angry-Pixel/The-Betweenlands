package thebetweenlands.common.item.misc;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityChiromawHatchling;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationChiromawMatriarchNest;

public class ItemChiromawEgg extends ItemMob {
	private final boolean electric;

	public ItemChiromawEgg(boolean electric) {
		super(1, EntityChiromawHatchling.class, entity -> entity.setElectricBoogaloo(electric));
		this.electric = electric;
	}

	@Override
	public void onCapturedByPlayer(EntityPlayer player, EnumHand hand, ItemStack captured) {
		if(player instanceof EntityPlayerMP) {
			AxisAlignedBB checkBox = player.getEntityBoundingBox().grow(8);

			if(player.world.getEntitiesWithinAABB(EntityChiromawHatchling.class, checkBox, entity -> entity.isEntityAlive()).isEmpty()) {
				List<LocationChiromawMatriarchNest> nests = BetweenlandsWorldStorage.forWorld(player.world).getLocalStorageHandler().getLocalStorages(LocationChiromawMatriarchNest.class, checkBox, location -> location.getBoundingBox().intersects(checkBox));
				
				if(nests.isEmpty()) {
					return;
				}
				
				for(LocationChiromawMatriarchNest nest : nests) {
					if(nest.getGuard() == null || nest.getGuard().isClear(player.world)) {
						return;
					}
				}
				
				AdvancementCriterionRegistry.CHIROMAW_MATRIARCH_NEST_RAIDED.trigger((EntityPlayerMP) player);
			}
		}
	}

	@Override
	protected void spawnCapturedEntity(EntityPlayer player, World world, Entity entity) {
		if (entity instanceof EntityChiromawHatchling) {
			((EntityChiromawHatchling) entity).setOwnerId(player.getUniqueID());
			((EntityChiromawHatchling) entity).setFoodCraved(((EntityChiromawHatchling) entity).chooseNewFoodFromLootTable());
		}

		super.spawnCapturedEntity(player, world, entity);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasEffect(ItemStack stack) {
		return this.electric;
	}
}

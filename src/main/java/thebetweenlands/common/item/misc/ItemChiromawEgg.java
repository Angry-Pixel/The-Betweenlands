package thebetweenlands.common.item.misc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityChiromawHatchling;

public class ItemChiromawEgg extends ItemMob {
	private final boolean electric;

	public ItemChiromawEgg(boolean electric) {
		super(1, EntityChiromawHatchling.class, entity -> entity.setElectricBoogaloo(electric));
		this.electric = electric;
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

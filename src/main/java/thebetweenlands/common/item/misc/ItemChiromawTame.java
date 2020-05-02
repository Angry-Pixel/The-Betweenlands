package thebetweenlands.common.item.misc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityChiromawHatchling;
import thebetweenlands.common.entity.mobs.EntityChiromawTame;

public class ItemChiromawTame extends ItemMob {
	private final boolean electric;

	public ItemChiromawTame(boolean electric) {
		super(1, EntityChiromawTame.class, entity -> entity.setElectricBoogaloo(electric));
		this.electric = electric;
	}

	@Override
	protected void spawnCapturedEntity(EntityPlayer player, World world, Entity entity) {
		if (entity instanceof EntityChiromawTame) {
			((EntityChiromawTame) entity).setOwnerId(player.getUniqueID());
		}

		super.spawnCapturedEntity(player, world, entity);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasEffect(ItemStack stack) {
		return this.electric;
	}
}

package thebetweenlands.common.item.misc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityChiromawTame;

public class ItemChiromawTame extends ItemMob {
	private final boolean electric;

	public ItemChiromawTame(boolean electric) {
		super(1, EntityChiromawTame.class, entity -> entity.setElectricBoogaloo(electric));
		this.electric = electric;
	}
	
	@Override
	protected EnumActionResult spawnCapturedEntity(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ, Entity entity, boolean isNewEntity) {
		if (!world.isRemote && entity instanceof EntityChiromawTame) {
			((EntityChiromawTame) entity).setOwnerId(player.getUniqueID());
		}
		return super.spawnCapturedEntity(player, world, pos, hand, facing, hitX, hitY, hitZ, entity, isNewEntity);
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		return getTranslationKey();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasEffect(ItemStack stack) {
		return this.electric;
	}
}

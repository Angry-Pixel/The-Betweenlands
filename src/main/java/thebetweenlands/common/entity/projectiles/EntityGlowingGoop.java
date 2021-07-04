package thebetweenlands.common.entity.projectiles;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import thebetweenlands.common.block.misc.BlockGlowingGoop;
import thebetweenlands.common.registries.BlockRegistry;

public class EntityGlowingGoop extends EntityThrowable {

	public EntityGlowingGoop(World world) {
		super(world);
	}

	public EntityGlowingGoop(World world, EntityLivingBase entity) {
		super(world, entity);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if(this.ticksExisted > 400) {
			setDead();
		}
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		if (result.typeOfHit != null) {
			if (this.world.isRemote) {
				double particleX = MathHelper.floor(this.posX) + this.rand.nextFloat();
				double particleY = MathHelper.floor(this.posY) + this.rand.nextFloat();
				double particleZ = MathHelper.floor(this.posZ) + this.rand.nextFloat();
				for (int count = 0; count < 10; count++) {
					world.spawnParticle(EnumParticleTypes.SLIME, particleX, particleY, particleZ, 0, 0, 0);
				}
			} else {
				if (result.typeOfHit == result.typeOfHit.BLOCK) {
					EnumFacing facing = result.sideHit;
					BlockPos pos = result.getBlockPos();
					if (BlockRegistry.GLOWING_GOOP.canPlaceBlockAt(world, pos.offset(facing))){
						world.setBlockState(pos.offset(facing), BlockRegistry.GLOWING_GOOP.getDefaultState().withProperty(BlockGlowingGoop.FACING, facing));
						world.playSound(null, pos, SoundEvents.ENTITY_SLIME_SQUISH, SoundCategory.NEUTRAL, 1F, 1F);	
					}
					else {
						InventoryHelper.spawnItemStack(world, posX, posY, posZ, new ItemStack(BlockRegistry.GLOWING_GOOP));
					}
					setDead();
				}
			}
		}
	}
}
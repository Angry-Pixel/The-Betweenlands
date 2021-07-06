package thebetweenlands.common.entity.projectiles;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
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
				if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
					EnumFacing facing = result.sideHit;
					BlockPos pos = result.getBlockPos();
					pos = pos.offset(facing);
					IBlockState state = this.world.getBlockState(pos);
					
					if (state.getBlock().isReplaceable(this.world, pos) && BlockRegistry.GLOWING_GOOP.canPlaceBlockAt(this.world, pos)){
						if(state.getMaterial() == Material.WATER) {
							world.setBlockState(pos, BlockRegistry.GLOWING_GOOP_UNDERWATER.getDefaultState().withProperty(BlockGlowingGoop.FACING, facing));
						} else {
							world.setBlockState(pos, BlockRegistry.GLOWING_GOOP.getDefaultState().withProperty(BlockGlowingGoop.FACING, facing));
						}
						
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
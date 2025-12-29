package thebetweenlands.common.entity.projectile;


import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import thebetweenlands.common.block.misc.GlowingGoopBlock;
import thebetweenlands.common.block.waterlog.SwampWaterLoggable.WaterType;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class GlowingGoop extends ThrowableItemProjectile implements ItemSupplier {

	public GlowingGoop(EntityType<? extends GlowingGoop> type, Level level) {
		super(type, level);
	}

	public GlowingGoop(Level level, LivingEntity entity) {
		super(EntityRegistry.GLOWING_GOOP_ENTITY.get(), entity, level);
	}

	@Override
	public void tick() {
		super.tick();
		if(tickCount  > 400) {
			discard();
		}
	}
/*
	@Override
    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender() {
    	return 15728880;
    }
*/
	@Override
	protected void onHit(HitResult result) {
		HitResult.Type typeOfHit = result.getType();
		if (typeOfHit != null) {
			if (level().isClientSide()) {
				double particleX = Mth.floor(getX()) + random.nextFloat();
				double particleY = Mth.floor(getY()) + random.nextFloat();
				double particleZ = Mth.floor(getZ()) + random.nextFloat();
				for (int count = 0; count < 10; count++) {
					level().addParticle(ParticleTypes.ITEM_SLIME, false, particleX, particleY, particleZ, 0, 0, 0);
				}
			} else {
				if (typeOfHit == HitResult.Type.BLOCK) {
					BlockHitResult blockhitresult = (BlockHitResult)result;
					net.minecraft.core.Direction facing = blockhitresult.getDirection();
					BlockPos pos = blockhitresult.getBlockPos();
					pos = pos.relative(facing);
					BlockState state = level().getBlockState(pos);
					
					if (state.is(BlockTags.REPLACEABLE)) {
						if(level().getFluidState(pos).is(FluidTags.WATER))
							level().setBlockAndUpdate(pos, BlockRegistry.GLOWING_GOOP.get().defaultBlockState().setValue(GlowingGoopBlock.FACING, facing).setValue(GlowingGoopBlock.WATER_TYPE, WaterType.getFromFluid(level().getFluidState(pos).getType())));
						else
							level().setBlockAndUpdate(pos, BlockRegistry.GLOWING_GOOP.get().defaultBlockState().setValue(GlowingGoopBlock.FACING, facing));
						level().playSound(null, pos, SoundEvents.SLIME_SQUISH, SoundSource.NEUTRAL, 1F, 1F);	
					}
					else
						spawnAtLocation(getDefaultItem(), 1);
					discard();
				}
			}
		}
	}
	
	@Override
	public ItemStack getItem() {
		return new ItemStack(ItemRegistry.GLOWING_GOOP.get());
	}

	@Override
	protected Item getDefaultItem() {
		return ItemRegistry.GLOWING_GOOP.get();
	}
}
package thebetweenlands.common.block.structure;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class BlockEnergyBarrier extends Block {
	private static final AxisAlignedBB BOUNDS = new AxisAlignedBB(0.125, 0.0, 0.125, 0.875, 1.0, 0.875);

	public BlockEnergyBarrier() {
		super(Material.GLASS);
		this.setSoundType(SoundType.GLASS);
		this.setUnlocalizedName("thebetweenlands.energy_barrier");
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setBlockUnbreakable();
		this.setResistance(6000000.0F);
		this.setLightLevel(0.8F);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return blockAccess.getBlockState(pos.offset(side)).getBlock() != this && super.shouldSideBeRendered(blockState, blockAccess, pos, side);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
		double particleX = pos.getX() + rand.nextFloat();
		double particleY = pos.getY() + rand.nextFloat();
		double particleZ = pos.getZ() + rand.nextFloat();
		double motionX = (rand.nextFloat() - 0.5D) * 0.5D;
		double motionY = (rand.nextFloat() - 0.5D) * 0.5D;
		double motionZ = (rand.nextFloat() - 0.5D) * 0.5D;
		int multiplier = rand.nextInt(2) * 2 - 1;
		if (world.getBlockState(pos.add(-1, 0, 0)).getBlock() != this && world.getBlockState(pos.add(1, 0, 0)).getBlock() != this) {
			particleX = pos.getX() + 0.5D + 0.25D * multiplier;
			motionX = rand.nextFloat() * 2.0F * multiplier;
		} else {
			particleZ = pos.getZ() + 0.5D + 0.25D * multiplier;
			motionZ = rand.nextFloat() * 2.0F * multiplier;
		}
		BLParticles.PORTAL.spawn(world, particleX, particleY, particleZ, ParticleFactory.ParticleArgs.get().withMotion(motionX * 0.2F, motionY, motionZ * 0.2F));
	}

	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return BOUNDS;
	}


	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
		return FULL_BLOCK_AABB.offset(pos);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			EnumHand swordHand = null;
			for (EnumHand hand : EnumHand.values()) {
				ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
				if (!stack.isEmpty() && stack.getItem() == ItemRegistry.SHOCKWAVE_SWORD) {
					swordHand = hand;
					break;
				}
			}
			if (swordHand != null) {
				int data = Block.getIdFromBlock(world.getBlockState(pos).getBlock());
				if (!world.isRemote)
					world.playEvent(null, 2001, pos, data);
				int range = 7;
				for (int x = -range; x < range; x++) {
					for (int y = -range; y < range; y++) {
						for (int z = -range; z < range; z++) {
							BlockPos offset = pos.add(x, y, z);
							IBlockState blockState = world.getBlockState(offset);
							if (blockState.getBlock() == this) {
								if (blockState.getRenderType() != EnumBlockRenderType.INVISIBLE) {
									for(int i = 0; i < 8; i++) {
										world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, offset.getX() + (double)world.rand.nextFloat(), offset.getY() + (double)world.rand.nextFloat(), offset.getZ() + (double)world.rand.nextFloat(), (double)world.rand.nextFloat() - 0.5D, (double)world.rand.nextFloat() - 0.5D, (double)world.rand.nextFloat() - 0.5D, new int[] {Block.getStateId(blockState)});
									}
								}

								if (!world.isRemote)
									world.setBlockToAir(offset);
							}
						}
					}
				}
			} else if (!player.isSpectator()) {
				entity.attackEntityFrom(DamageSource.MAGIC, 1);
				double dx = (entity.posX - (pos.getX()))*2-1;
				double dz = (entity.posZ - (pos.getZ()))*2-1;
				if(Math.abs(dx) > Math.abs(dz))
					dz = 0;
				else
					dx = 0;
				dx = (int)dx;
				dz = (int)dz;
				entity.addVelocity(dx*0.85D, 0.08D, dz*0.85D);
				entity.playSound(SoundRegistry.REJECTED, 0.5F, 1F);
			}
		}
	}
	
	@Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    	return BlockFaceShape.UNDEFINED;
    }
}
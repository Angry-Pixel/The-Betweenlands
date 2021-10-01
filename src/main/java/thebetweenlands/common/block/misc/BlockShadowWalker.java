package thebetweenlands.common.block.misc;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.tab.BLCreativeTabs;

public class BlockShadowWalker extends Block  {
	

	public BlockShadowWalker(Material materialIn) {
		super(materialIn);
		setCreativeTab(BLCreativeTabs.BLOCKS);
		setSoundType(SoundType.STONE);
		setHardness(1.2F);
		setResistance(8.0F);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		return NULL_AABB;
	}

	@Override
    public boolean causesSuffocation(IBlockState state) {
        return false;
    }

	@Override
    public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid) {
        return false;
    }
	
    public boolean isReplaceable(IBlockAccess world, BlockPos pos) {
        return false;
    }

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return blockAccess.getBlockState(pos.offset(side)).getBlock() != this && super.shouldSideBeRendered(blockState, blockAccess, pos, side);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Items.AIR;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
		if (world.isAirBlock(pos.up())) {
			for(int i = 0; i < 3 + rand.nextInt(5); i++) {
				BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_NEAREST_NEIGHBOR, BLParticles.SMOOTH_SMOKE.create(world, pos.getX() + 0.5F, pos.getY() + 1F, pos.getZ() + 0.5F, 
						ParticleArgs.get()
						.withMotion((rand.nextFloat() - 0.5f) * 0.08f, rand.nextFloat() * 0.01F + 0.01F, (rand.nextFloat() - 0.5f) * 0.08f)
						.withScale(1f + rand.nextFloat() * 8.0F)
						.withColor(0F, 0F, 0F, 0.05f)
						.withData(80, true, 0.01F, true)));
			}
		}
	}
}

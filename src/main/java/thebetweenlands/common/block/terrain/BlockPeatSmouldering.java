package thebetweenlands.common.block.terrain;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.block.container.BlockSmokingRack;
import thebetweenlands.common.block.structure.BlockChipPath;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntitySmokingRack;

public class BlockPeatSmouldering extends BlockPeat {

	public BlockPeatSmouldering() {
		super();
	}

	@Override
	public void onBlockClicked(World world, BlockPos pos, EntityPlayer playerIn) {
		if (!world.isRemote) {
			if (world.isAirBlock(pos.up()))
				world.setBlockState(pos, BlockRegistry.PEAT.getDefaultState());
		}
	}

	@Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if (!world.isRemote) {
			if (world.getBlockState(pos.up()).getMaterial() == Material.FIRE)
				world.setBlockState(pos, BlockRegistry.PEAT.getDefaultState());
		}
    }

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (rand.nextInt(24) == 0)
			worldIn.playSound((double) ((float) pos.getX() + 0.5F), (double) ((float) pos.getY() + 0.5F), (double) ((float) pos.getZ() + 0.5F), SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
		
		if (worldIn.isAirBlock(pos.up())) {
			for(int i = 0; i < 3 + rand.nextInt(5); i++) {
				BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.SMOOTH_SMOKE.create(worldIn, pos.getX() + 0.5F, pos.getY() + 1F, pos.getZ() + 0.5F, 
						ParticleArgs.get()
						.withMotion((rand.nextFloat() - 0.5f) * 0.08f, rand.nextFloat() * 0.01F + 0.01F, (rand.nextFloat() - 0.5f) * 0.08f)
						.withScale(1f + rand.nextFloat() * 8.0F)
						.withColor(1F, 1.0F, 1.0F, 0.05f)
						.withData(80, true, 0.01F, true)));
			}
		}

		if (worldIn.getBlockState(pos.up()).getBlock() instanceof BlockChipPath) {
			for(int i = 0; i < 3 + rand.nextInt(5); i++) {
				BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_NEAREST_NEIGHBOR, BLParticles.SMOOTH_SMOKE.create(worldIn, pos.getX() + 0.5F, pos.getY() + 1F, pos.getZ() + 0.5F, 
						ParticleArgs.get()
						.withMotion((rand.nextFloat() - 0.5f) * 0.04f, rand.nextFloat() * 0.1F + 0.05F, (rand.nextFloat() - 0.5f) * 0.04f)
						.withScale(2f + rand.nextFloat() * 2.0F)
						.withColor(0F, 0.0F, 0.0F, 0.5f)
						.withData(80, true, 0.01F, true)));
			}

				switch(rand.nextInt(3)) {
				default:
				case 0:
					BLParticles.EMBER_1.spawn(worldIn, pos.getX() + 0.5D, pos.getY() + 1D, pos.getZ() + 0.5D);
					break;
				case 1:
					BLParticles.EMBER_2.spawn(worldIn, pos.getX() + 0.5D, pos.getY() + 1D, pos.getZ() + 0.5D);
					break;
				case 2:
					BLParticles.EMBER_3.spawn(worldIn, pos.getX() + 0.5D, pos.getY() + 1D, pos.getZ() + 0.5D);
					break;
				}
		}
		
		if (worldIn.getBlockState(pos.up()).getBlock() instanceof BlockSmokingRack) {
			TileEntitySmokingRack tile = (TileEntitySmokingRack) worldIn.getTileEntity(pos.up());
			if (tile != null && tile.updateFuelState()) {
				for(int i = 0; i < 3 + rand.nextInt(5); i++) {
					BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_NEAREST_NEIGHBOR, BLParticles.SMOOTH_SMOKE.create(worldIn, pos.getX() + 0.5F, pos.getY() + 1F, pos.getZ() + 0.5F, 
							ParticleArgs.get()
							.withMotion((rand.nextFloat() - 0.5f) * 0.04f, rand.nextFloat() * 0.2F + 0.01F, (rand.nextFloat() - 0.5f) * 0.04f)
							.withScale(1f + rand.nextFloat() * 2.0F)
							.withColor(1F, 1F, 1F, 0.5f)
							.withData(80, true, 0.01F, true)));
					
					BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_NEAREST_NEIGHBOR, BLParticles.SMOOTH_SMOKE.create(worldIn, pos.getX() + 0.5F, pos.getY() + 2.125F, pos.getZ() + 0.5F, 
							ParticleArgs.get()
							.withMotion((rand.nextFloat() - 0.5f) * 0.04f, rand.nextFloat() * 0.02F + 0.01F, (rand.nextFloat() - 0.5f) * 0.04f)
							.withScale(1f + rand.nextFloat() * 2.0F)
							.withColor(1F, 1F, 1F, 0.5f)
							.withData(80, true, 0.01F, true)));
				}
			}
		}
	}
}

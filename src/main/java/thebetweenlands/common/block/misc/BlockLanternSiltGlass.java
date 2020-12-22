package thebetweenlands.common.block.misc;

import java.util.Random;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;

public class BlockLanternSiltGlass extends BlockLantern {
	public BlockLanternSiltGlass() {
		super(Material.WOOD, SoundType.GLASS);
	}

	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.CUTOUT || layer == BlockRenderLayer.TRANSLUCENT;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		BatchedParticleRenderer.INSTANCE.addParticle(
				DefaultParticleBatches.TRANSLUCENT_GLOWING,
				BLParticles.WISP.create(worldIn, pos.getX() + 0.5f, pos.getY() + 0.325f, pos.getZ() + 0.5f,
						ParticleArgs.get().withMotion(0, -0.0015f, 0).withScale(0.3F + rand.nextFloat() * 0.4f).withColor(1.0f, 0.5f + rand.nextFloat() * 0.2f, 0.1f + rand.nextFloat() * 0.2f, 0.6f).withData(255, false)));
	}
}

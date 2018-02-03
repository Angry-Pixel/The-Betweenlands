package thebetweenlands.common.block.terrain;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.BLParticles;

public class BlockHearthgroveLog extends BlockLogBetweenlands {
	@Override
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		for(EnumFacing offset : EnumFacing.VALUES) {
			IBlockState offsetState = worldIn.getBlockState(pos.offset(offset));
			IBlockState offsetStateDown = worldIn.getBlockState(pos.down().offset(offset));
			
			if(offsetStateDown.getMaterial() == Material.WATER && offsetState.getMaterial() != Material.WATER) {
				if(rand.nextInt(8) == 0) {
					for(int i = 0; i < 5; i++) {
						float x = pos.getX() + (offset.getFrontOffsetX() > 0 ? 1.05F : offset.getFrontOffsetX() == 0 ? rand.nextFloat() : -0.05F);
						float y = pos.getY() - 0.1F;
						float z = pos.getZ() + (offset.getFrontOffsetZ() > 0 ? 1.05F : offset.getFrontOffsetZ() == 0 ? rand.nextFloat() : -0.05F);
	
						BLParticles.PURIFIER_STEAM.spawn(worldIn, x, y, z);
					}
				}
			}
			
			if(offsetState.getMaterial() == Material.WATER) {
				if(rand.nextInt(8) == 0) {
					for(int i = 0; i < 5; i++) {
						float x = pos.getX() + (offset.getFrontOffsetX() > 0 ? 1.1F : offset.getFrontOffsetX() == 0 ? rand.nextFloat() : -0.1F);
						float y = pos.getY() + rand.nextFloat();
						float z = pos.getZ() + (offset.getFrontOffsetZ() > 0 ? 1.1F : offset.getFrontOffsetZ() == 0 ? rand.nextFloat() : -0.1F);
	
						worldIn.spawnParticle(EnumParticleTypes.WATER_BUBBLE, x, y, z, 0, 0, 0);
					}
				}
			}
		}
	}
}

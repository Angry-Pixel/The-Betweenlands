package thebetweenlands.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockTorch;
import net.minecraft.world.World;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.creativetabs.BLCreativeTabs;

import java.util.Random;

public class BlockSulfurTorch extends BlockTorch {
    public BlockSulfurTorch() {
        super();
        setHardness(0.0F);
        setLightLevel(0.9375F);
        setStepSound(soundTypeWood);
        setBlockName("thebetweenlands.sulfurTorch");
        setBlockTextureName("thebetweenlands:sulfurTorch");
        setCreativeTab(BLCreativeTabs.blocks);
        setTickRandomly(true);
    }

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        int meta = world.getBlockMetadata(x, y, z);
        double posX = (double)((float)x + 0.5F);
        double posY = (double)((float)y + 0.8F);
        double posZ = (double)((float)z + 0.5F);
        double offSetY = 0.3199999988079071D;
        double offSetXZ = rand.nextDouble() * 0.5D - rand.nextDouble() * 0.5D;

        if (meta == 1)
    		BLParticle.SULFUR_TORCH.spawn(world, posX - offSetXZ, posY + offSetY, posZ, 0.0D, 0.0D, 0.0D, 0);

        else if (meta == 2)
        	BLParticle.SULFUR_TORCH.spawn(world, posX + offSetXZ, posY + offSetY, posZ, 0.0D, 0.0D, 0.0D, 0);

        else if (meta == 3)
        	BLParticle.SULFUR_TORCH.spawn(world, posX, posY + offSetY, posZ - offSetXZ, 0.0D, 0.0D, 0.0D, 0);

        else if (meta == 4)
        	BLParticle.SULFUR_TORCH.spawn(world, posX, posY + offSetY, posZ + offSetXZ, 0.0D, 0.0D, 0.0D, 0);
        else
        	BLParticle.SULFUR_TORCH.spawn(world, posX, posY, posZ, 0.0D, 0.0D, 0.0D, 0);
   
        if(world.rand.nextInt(20) == 0 && world.canBlockSeeTheSky(x, y, z)) {
        	int particle = rand.nextInt(3);
        	if(particle == 0) {
        		BLParticle.MOSQUITO.spawn(world, posX, posY, posZ, 0.0D, 0.0D, 0.0D, 0);
        	} else if(particle == 1) {
        		BLParticle.FLY.spawn(world, posX, posY, posZ, 0.0D, 0.0D, 0.0D, 0);
        	} else {
        		BLParticle.MOTH.spawn(world, posX, posY, posZ, 0.0D, 0.0D, 0.0D, 0);
        	}
        }
    }
 
}

package thebetweenlands.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockTorch;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.creativetabs.ModCreativeTabs;

import java.util.Random;

public class BlockSulfurTorch extends BlockTorch {
    public BlockSulfurTorch() {
        super();
        setHardness(0.0F);
        setLightLevel(0.9375F);
        setStepSound(soundTypeWood);
        setBlockName("thebetweenlands.sulfurTorch");
        setBlockTextureName("thebetweenlands:sulfurTorch");
        setCreativeTab(ModCreativeTabs.blocks);
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
    		TheBetweenlands.proxy.spawnCustomParticle("sulfurTorch", world, posX - offSetXZ, posY + offSetY, posZ, 0.0D, 0.0D, 0.0D, 0);

        else if (meta == 2)
    		TheBetweenlands.proxy.spawnCustomParticle("sulfurTorch", world, posX + offSetXZ, posY + offSetY, posZ, 0.0D, 0.0D, 0.0D, 0);

        else if (meta == 3)
    		TheBetweenlands.proxy.spawnCustomParticle("sulfurTorch", world, posX, posY + offSetY, posZ - offSetXZ, 0.0D, 0.0D, 0.0D, 0);

        else if (meta == 4)
    		TheBetweenlands.proxy.spawnCustomParticle("sulfurTorch", world, posX, posY + offSetY, posZ + offSetXZ, 0.0D, 0.0D, 0.0D, 0);
        else
    		TheBetweenlands.proxy.spawnCustomParticle("sulfurTorch", world, posX, posY, posZ, 0.0D, 0.0D, 0.0D, 0);
   
        if(world.rand.nextInt(20) == 0 && world.canBlockSeeTheSky(x, y, z)) {
        	int particle = rand.nextInt(3);
        	if(particle == 0) {
        		TheBetweenlands.proxy.spawnCustomParticle("mosquito", world, posX, posY, posZ, 0.0D, 0.0D, 0.0D, 0);
        	} else if(particle == 1) {
        		TheBetweenlands.proxy.spawnCustomParticle("fly", world, posX, posY, posZ, 0.0D, 0.0D, 0.0D, 0);
        	} else {
        		TheBetweenlands.proxy.spawnCustomParticle("moth", world, posX, posY, posZ, 0.0D, 0.0D, 0.0D, 0);
        	}
        }
    }
 
}

package thebetweenlands.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockTorch;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.creativetabs.ModCreativeTabs;

import java.util.Random;

public class BlockDampTorch extends BlockTorch {
	
    public BlockDampTorch() {
        super();
        setHardness(0.0F);
        setLightLevel(0);
        setStepSound(soundTypeWood);
        setBlockName("thebetweenlands.dampTorch");
        setBlockTextureName("thebetweenlands:dampTorch");
        setCreativeTab(ModCreativeTabs.blocks);
        setTickRandomly(true);
    }

    @SideOnly(Side.CLIENT)
	@Override
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        int meta = world.getBlockMetadata(x, y, z);
        double posX = (double)((float)x + 0.5F);
        double posY = (double)((float)y + 0.8F);
        double posZ = (double)((float)z + 0.5F);
        double offSetY = 0.3199999988079071D;
        double offSetXZ = rand.nextDouble() * 0.5D - rand.nextDouble() * 0.5D;

        if (meta == 1)
    		BLParticle.SMOKE.spawn(world, posX - offSetXZ, posY + offSetY, posZ);
        else if (meta == 2)
        	BLParticle.SMOKE.spawn(world, posX + offSetXZ, posY + offSetY, posZ);
        else if (meta == 3)
        	BLParticle.SMOKE.spawn(world, posX, posY + offSetY, posZ - offSetXZ);
        else if (meta == 4)
        	BLParticle.SMOKE.spawn(world, posX, posY + offSetY, posZ + offSetXZ);
        else
        	BLParticle.SMOKE.spawn(world, posX, posY, posZ);
    }

}

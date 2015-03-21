package thebetweenlands.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.creativetabs.ModCreativeTabs;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSludge extends Block {
    public BlockSludge() {
    	super(Material.ground);
    	setHardness(0.1F);
		setStepSound(soundTypeGravel);
		setCreativeTab(ModCreativeTabs.blocks);
		setBlockName("thebetweenlands.sludge");
		setBlockTextureName("thebetweenlands:sludge");
        setBlockBounds(0, 0.0F, 0, 1.0F, 0.125F, 1.0F);
    }
    
    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
    	entity.motionX *= 0.8D;
        entity.motionZ *= 0.8D;
    }

    @Override
    public boolean canBlockStay(World world, int x, int y, int z) {
        return world.getBlock(x, y - 1, z).isSideSolid(world, x, y - 1, z, ForgeDirection.UP);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderType() {
        return 0;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderBlockPass() {
        return 1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean renderAsNormalBlock() {
        return false;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int x, int y, int z, int side) {
    	if(blockAccess.getBlock(x, y, z) == this) {
    		return false;
    	}
        return super.shouldSideBeRendered(blockAccess, x, y, z, side);
    }
    
    @Override
    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
        return null;
    }
}
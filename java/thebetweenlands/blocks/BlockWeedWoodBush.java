package thebetweenlands.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.tileentities.TileEntityBush;

public class BlockWeedWoodBush extends BlockContainer{

	protected BlockWeedWoodBush() 
	{
		super(Material.leaves);
	}
    
    @Override
    public TileEntity createNewTileEntity(World world, int var2)
    {
        return new TileEntityBush();
    }
    
    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l)
    {
        return false;
    }
    
    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }
    
    @Override
    public int getRenderType()
    {
        return -1;
    }

}

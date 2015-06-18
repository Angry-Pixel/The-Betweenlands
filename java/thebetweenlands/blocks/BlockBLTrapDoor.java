package thebetweenlands.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.creativetabs.ModCreativeTabs;

/**
 * Created by Bart on 16-6-2015.
 */
public class BlockBLTrapDoor extends BlockTrapDoor {
    private IIcon sideIcon;

    public BlockBLTrapDoor(String name, Material material) {
        super(material);
        this.setBlockName("thebetweenlands." + name + "TrapDoor");
        textureName = "thebetweenlands:" + name + "Trapdoor";
        setHardness(3.0F);
        setStepSound(soundTypeWood);
        setCreativeTab(ModCreativeTabs.blocks);
    }


    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess access, int x, int y, int z, int side) {
        return blockIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (side == 0 || side == 1)
            return blockIcon;
        else
            return sideIcon;
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        this.blockIcon = register.registerIcon(textureName);
        this.sideIcon = register.registerIcon(textureName + "Side");
    }

    @Override
    public boolean onBlockActivated(World wor, int x, int y, int z, EntityPlayer entityPlayer, int meta, float hitX, float hitY, float hitZ) {
        if (this.blockMaterial == Material.rock) {
            return true;
        } else {
            int i1 = wor.getBlockMetadata(x, y, z);
            wor.setBlockMetadataWithNotify(x, y, z, i1 ^ 4, 2);
            wor.playAuxSFXAtEntity(entityPlayer, 1003, x, y, z, 0);
            return true;
        }
    }
}

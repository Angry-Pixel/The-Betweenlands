package thebetweenlands.blocks.plants;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.creativetabs.ModCreativeTabs;

import java.util.Random;


public class BlockBlubCappedMushroomStalk extends Block {
    public IIcon sides;
    public IIcon bot;

    public BlockBlubCappedMushroomStalk() {
        super(Material.wood);
        setBlockName("thebetweenlands.hugeMushroomStalk");
        setCreativeTab(ModCreativeTabs.blocks);
        setLightLevel(1.0F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.sides = iconRegister.registerIcon("thebetweenlands:bulbCappedShroomStalk1");
        this.bot = iconRegister.registerIcon("thebetweenlands:bulbCappedShroomStalk2");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess access, int x, int y, int z, int side){
        if(side == 0 || side == 1)
            return this.bot;
        else
            return sides;
    }
    @Override
    public IIcon getIcon(int side, int meta){
        if(side == 0 || side == 1)
            return this.bot;
        else
            return sides;
    }

    @Override
    public Item getItemDropped(int meta, Random rand, int fortune) {
        return Item.getItemFromBlock(BLBlockRegistry.bulbCappedMushroom);
    }

    @Override
    public void dropBlockAsItemWithChance (World world, int x, int y, int z, int meta, float chance, int fortune) {
        if (!world.isRemote) {
            int dropChance = 2;

            if (fortune > 0){
                dropChance -= 2*fortune;
            }
            if(world.rand.nextInt(dropChance) <= 0) {
                this.dropBlockAsItem(world, x, y, z, new ItemStack(this.getItemDropped(meta, world.rand, fortune), 1));
            }
        }
    }

}

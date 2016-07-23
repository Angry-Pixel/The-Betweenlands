package thebetweenlands.common.item.misc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.world.feature.tree.WorldGenWeedWoodTree;

//MINE!!
public class TestItem extends Item {
    public TestItem() {
        this.setMaxStackSize(1);
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
     /*   WorldGenDruidCircle worldGenDruidCircle = new WorldGenDruidCircle();
        worldGenDruidCircle.generateStructure(worldIn, itemRand, pos.getX(), pos.getY() + 1, pos.getZ());*/
    	if(!world.isRemote) {
    		WorldGenWeedWoodTree tree = new WorldGenWeedWoodTree();
    		tree.generate(world, itemRand, pos.up());
    		return EnumActionResult.SUCCESS;
    	}
        return super.onItemUse(stack, playerIn, world, pos, hand, facing, hitX, hitY, hitZ);
    }
}

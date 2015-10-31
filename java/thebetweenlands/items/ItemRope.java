package thebetweenlands.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thebetweenlands.blocks.BLBlockRegistry;

/**
 * Created by Bart on 23-10-2015.
 */
public class ItemRope extends Item {
    public ItemRope() {
        this.setUnlocalizedName("thebetweenlands.itemRope");
        setTextureName("thebetweenlands:rope");
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (world.getBlock(x, y, z) == BLBlockRegistry.rope) {
            while (world.getBlock(x, y, z) == BLBlockRegistry.rope) {
                y--;
            }
            if (world.getBlock(x, y, z) == Blocks.air && player.canPlayerEdit(x, y, z, side, stack)) {
                world.setBlock(x, y, z, BLBlockRegistry.rope);
                stack.stackSize--;
                return true;
            }
        } else if (world.getBlock(x, y, z).isSideSolid(world, x, y, z, ForgeDirection.DOWN) && world.getBlock(x, y - 1, z) == Blocks.air) {
            world.setBlock(x, y - 1, z, BLBlockRegistry.rope);
            stack.stackSize--;
            return true;
        }
        return false;
    }
}

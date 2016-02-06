package thebetweenlands.items.misc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSign;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.blocks.container.BlockBLSign;
import thebetweenlands.creativetabs.BLCreativeTabs;

/**
 * Created by Bart on 16/01/2016.
 */
public class ItemBLSign extends ItemSign {
    public BlockBLSign wall;
    public BlockBLSign standing;
    public String material;

    public ItemBLSign(BlockBLSign wall, BlockBLSign standing, String material){
        this.wall = wall;
        this.standing = standing;
        this.material = material;
        setCreativeTab(BLCreativeTabs.items);
        setUnlocalizedName("thebetweenlands." + material + "Sign");
        setTextureName("thebetweenlands:" + material + "Sign");
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (side == 0) {
            return false;
        } else if (!world.getBlock(x, y, z).getMaterial().isSolid()) {
            return false;
        } else {
            if (side == 1) {
                ++y;
            }

            if (side == 2) {
                --z;
            }

            if (side == 3) {
                ++z;
            }

            if (side == 4) {
                --x;
            }

            if (side == 5) {
                ++x;
            }

            if (!player.canPlayerEdit(x, y, z, side, itemStack)) {
                return false;
            } else if (!standing.canPlaceBlockAt(world, x, y, z)) {
                return false;
            } else if (world.isRemote) {
                return true;
            } else {
                if (side == 1) {
                    int i1 = MathHelper.floor_double((double) ((player.rotationYaw + 180.0F) * 16.0F / 360.0F) + 0.5D) & 15;
                    world.setBlock(x, y, z, standing, i1, 3);
                } else {
                    world.setBlock(x, y, z, wall, side, 3);
                }

                --itemStack.stackSize;
                TileEntitySign tileentitysign = (TileEntitySign) world.getTileEntity(x, y, z);

                if (tileentitysign != null) {
                    player.func_146100_a(tileentitysign);
                }

                return true;
            }
        }
    }
}

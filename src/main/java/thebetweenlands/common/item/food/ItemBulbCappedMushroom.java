package thebetweenlands.common.item.food;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemBulbCappedMushroom extends ItemBLFood {
    public ItemBulbCappedMushroom() {
        super(3, 0.6F, false, "bulbCappedMushroomItem");
    }
        /*@Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        Block block = worldIn.getBlockState(pos).getBlock();
        if (block.isReplaceable(worldIn, pos) || (facing == EnumFacing.UP && worldIn.isAirBlock(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ())))) {
            BlockPos pos1 = new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ());
            block = worldIn.getBlockState(pos1).getBlock();
            if (block != BlockRegistry.bulbCappedMushroom && BlockRegistry.bulbCappedMushroom.canPlaceBlockAt(worldIn, new BlockPos(pos1))) {
                if (!worldIn.isRemote) {
                    worldIn.setBlockState(pos1, BlockRegistry.bulbCappedMushroom.getDefaultState());
                    worldIn.playSoundEffect((double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), BLBlockRegistry.bulbCappedMushroom.stepSound.func_150496_b(), (BLBlockRegistry.bulbCappedMushroom.stepSound.getVolume() + 1.0F) / 2.0F, BLBlockRegistry.bulbCappedMushroom.stepSound.getPitch() * 0.8F);
                    --stack.stackSize;
                }
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.FAIL;
    }*/

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        super.onFoodEaten(stack, world, player);
        if (player != null) {
            player.addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation("nausea"), 200, 1));
            player.addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation("night_vision"), 200, 1));
        }
    }

}

package thebetweenlands.common.item.food;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import thebetweenlands.client.tab.BLCreativeTabs;

public class ItemCrabClaw extends ItemBLFood {

	public ItemCrabClaw() {
		super(2, 0.6F, false);
		this.setCreativeTab(BLCreativeTabs.ITEMS);
	}

    @Override
	public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
        if (!worldIn.isRemote)
            stack.shrink(1);
        Block block = state.getBlock();
        if (block instanceof IShearable)
        	return true;
        return state.getMaterial() != Material.LEAVES && block != Blocks.WEB && block != Blocks.TALLGRASS && block != Blocks.VINE && block != Blocks.TRIPWIRE && block != Blocks.WOOL ? super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving) : true;
    }

    @Override
    public boolean canHarvestBlock(IBlockState blockIn) {
        Block block = blockIn.getBlock();
        return block == Blocks.WEB || block == Blocks.REDSTONE_WIRE || block == Blocks.TRIPWIRE;
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack itemstack, EntityPlayer player, EntityLivingBase entity, EnumHand hand) {
        if (entity.world.isRemote)
            return false;
        
        if (entity instanceof IShearable){
            IShearable target = (IShearable)entity;
            BlockPos pos = new BlockPos(entity.posX, entity.posY, entity.posZ);
            if (target.isShearable(itemstack, entity.world, pos)) {
                List<ItemStack> drops = target.onSheared(itemstack, entity.world, pos, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, itemstack));
                Random rand = new Random();
                for(ItemStack stack : drops) {
                    EntityItem ent = entity.entityDropItem(stack, 1.0F);
                    ent.motionY += rand.nextFloat() * 0.05F;
                    ent.motionX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
                    ent.motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
                }
                itemstack.shrink(1);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
        if (player.world.isRemote || player.capabilities.isCreativeMode)
            return false;
        Block block = player.world.getBlockState(pos).getBlock();
        if (block instanceof IShearable) {
            IShearable target = (IShearable)block;
            if (target.isShearable(itemstack, player.world, pos)) {
                List<ItemStack> drops = target.onSheared(itemstack, player.world, pos, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, itemstack));
                Random rand = new Random();
                for (ItemStack stack : drops) {
                    float f = 0.7F;
                    double d  = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                    double d1 = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                    double d2 = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                    EntityItem entityitem = new EntityItem(player.world, (double)pos.getX() + d, (double)pos.getY() + d1, (double)pos.getZ() + d2, stack);
                    entityitem.setDefaultPickupDelay();
                    player.world.spawnEntity(entityitem);
                }
                itemstack.shrink(1);
                player.addStat(StatList.getBlockStats(block));
                player.world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
                return true;
            }
        }
        return false;
    }

    @Override
	public float getDestroySpeed(ItemStack stack, IBlockState state) {
		Block block = state.getBlock();
		if (block != Blocks.WEB && state.getMaterial() != Material.LEAVES)
			return block == Blocks.WOOL ? 5.0F : super.getDestroySpeed(stack, state);
		else
			return 15.0F;
	}
}

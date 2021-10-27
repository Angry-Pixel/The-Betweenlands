package thebetweenlands.common.item.misc;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.block.BlockJukebox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.container.BlockWeedwoodJukebox;
import thebetweenlands.common.registries.ItemRegistry;

public class ItemFreshwaterUrchin extends ItemMob {
	public <T extends Entity> ItemFreshwaterUrchin(int maxStackSize, @Nullable Class<T> defaultMob, @Nullable Consumer<T> defaultMobSetter) {
		super(1, defaultMob, defaultMobSetter);
		this.setCreativeTab(BLCreativeTabs.ITEMS);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		
		ItemStack stack = player.getHeldItem(hand);
		IBlockState iblockstate = world.getBlockState(pos);
		
		if (iblockstate.getBlock() instanceof BlockWeedwoodJukebox && !iblockstate.getValue(BlockJukebox.HAS_RECORD)) {
			if(!world.isRemote) {
				((BlockJukebox) iblockstate.getBlock()).insertRecord(world, pos, iblockstate, new ItemStack(ItemRegistry.DEEP_WATER_THEME));
				world.playEvent(null, 1010, pos, Item.getIdFromItem(ItemRegistry.DEEP_WATER_THEME));
				stack.shrink(stack.getCount());
				player.addStat(StatList.RECORD_PLAYED);
			}
			return EnumActionResult.SUCCESS;
		}
		
		return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
	}
}

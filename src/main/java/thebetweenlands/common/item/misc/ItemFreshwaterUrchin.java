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
	@SuppressWarnings("unchecked")
	public <T extends Entity> ItemFreshwaterUrchin(int maxStackSize, @Nullable Class<T> defaultMob, @Nullable Consumer<T> defaultMobSetter) {
		super(1, defaultMob, defaultMobSetter);
		this.setCreativeTab(BLCreativeTabs.ITEMS);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		IBlockState iblockstate = world.getBlockState(pos);
		if(!world.isRemote) {
			if (iblockstate.getBlock() instanceof BlockWeedwoodJukebox && !iblockstate.getValue(BlockJukebox.HAS_RECORD)) {
				((BlockJukebox) iblockstate.getBlock()).insertRecord(world, pos, iblockstate, stack);
				world.playEvent(null, 1010, pos, Item.getIdFromItem(ItemRegistry.DJ_WIGHTS_MIXTAPE)); //TODO add other music
				stack.shrink(stack.getCount());
				player.addStat(StatList.RECORD_PLAYED);
				return EnumActionResult.SUCCESS;
			}
			
			Entity entity = this.createCapturedEntity(world, pos.getX() + hitX, pos.getY() + hitY, pos.getZ() + hitZ, stack);
			if(entity != null) {
				
				if(facing.getXOffset() != 0) {
					entity.setPosition(entity.posX + facing.getXOffset() * entity.width * 0.5f, entity.posY, entity.posZ);
				}
				if(facing.getYOffset() < 0) {
					entity.setPosition(entity.posX, entity.posY - entity.height, entity.posZ);
				}
				if(facing.getZOffset() != 0) {
					entity.setPosition(entity.posX, entity.posY, entity.posZ + facing.getZOffset() * entity.width * 0.5f);
				}

				if(world.getCollisionBoxes(entity, entity.getEntityBoundingBox()).isEmpty()) {
					this.spawnCapturedEntity(player, world, entity);
					stack.shrink(1);
					return EnumActionResult.SUCCESS;
				}
			}
		}
		return EnumActionResult.SUCCESS;
	}
}

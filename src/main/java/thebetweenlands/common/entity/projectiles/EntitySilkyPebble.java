package thebetweenlands.common.entity.projectiles;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EntitySilkyPebble extends EntityAngryPebble {
	public EntitySilkyPebble(World world) {
		super(world);
	}

	public EntitySilkyPebble(World world, EntityLivingBase entity) {
		super(world, entity);
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void boomEvent(ExplosionEvent.Detonate event) {
		if (event.getWorld().isRemote)
			return;

		if (event.getExplosion().getExplosivePlacedBy() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getExplosion().getExplosivePlacedBy();
			List<Entity> entityList = event.getAffectedEntities();
			List<BlockPos> blockPosList = event.getAffectedBlocks();
			for (Entity entity : entityList)
				if (entity instanceof EntitySilkyPebble)
					for (BlockPos pos : blockPosList) {
						IBlockState state = event.getWorld().getBlockState(pos);
						Block block = state.getBlock();
						if (block.canSilkHarvest(event.getWorld(), pos, state, player)) {
							java.util.List<ItemStack> items = new java.util.ArrayList<ItemStack>();
							ItemStack itemstack = getSilkTouchDrop(block, state);
							if (!itemstack.isEmpty())
								items.add(itemstack);
							// not really needed but I guess it is nice if someone else needs it
							net.minecraftforge.event.ForgeEventFactory.fireBlockHarvesting(items, event.getWorld(), pos, state, 0, 1.0f, true, player);
							for (ItemStack item : items) {
								block.spawnAsEntity(event.getWorld(), pos, item);
							}
						} else
							block.dropBlockAsItem(event.getWorld(), pos, state, 0);
					}
		}
	}

	private static ItemStack getSilkTouchDrop(Block block, IBlockState state) {
        Item item = Item.getItemFromBlock(block);
        int i = 0;
        if (item.getHasSubtypes())
            i = block.getMetaFromState(state);
        return new ItemStack(item, 1, i);
    }
}
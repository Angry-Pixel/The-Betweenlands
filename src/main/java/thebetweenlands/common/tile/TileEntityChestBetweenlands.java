package thebetweenlands.common.tile;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import thebetweenlands.api.loot.ISharedLootPool;
import thebetweenlands.common.loot.shared.SharedLootPool;
import thebetweenlands.common.registries.LootTableRegistry;

public class TileEntityChestBetweenlands extends TileEntityChest {
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		//Use vanilla behaviour to prevent inventory from resetting when creating double chest
		return oldState.getBlock() != newSate.getBlock();
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos.add(-1, 0, -1), pos.add(2, 2, 2));
	}
	
	private boolean filled = false;
	
	private ISharedLootPool sharedPool;
	
	//TODO Test, remove
	@Override
	public void fillWithLoot(@Nullable EntityPlayer player) {
		if(this.filled || this.sharedPool == null) {
			return;
		}
		
		this.getItems().clear();
		
		this.filled = true;
		
        LootTable lootTable = this.sharedPool.getLootTableView(3, Integer.MAX_VALUE, Integer.MAX_VALUE);
        
        Random random;

        if (this.lootTableSeed == 0L)
        {
            random = new Random();
        }
        else
        {
            random = new Random(this.lootTableSeed);
        }

        LootContext.Builder lootcontext$builder = new LootContext.Builder((WorldServer)this.world);

        if (player != null)
        {
            lootcontext$builder.withLuck(player.getLuck()).withPlayer(player); // Forge: add player to LootContext
        }

        lootTable.fillInventory(this, random, lootcontext$builder.build());
    }
	
	@Override
	public void openInventory(EntityPlayer player) {
		super.openInventory(player);
		
		if(player.isSneaking() || this.sharedPool == null) {
			ISharedLootPool sharedLootPool1 = new SharedLootPool(LootTableRegistry.DUNGEON_CHEST_LOOT);
			ISharedLootPool sharedLootPool2 = new SharedLootPool(LootTableRegistry.MIRE_SNAIL);
			
			this.sharedPool = sharedLootPool1.combine(sharedLootPool2);
		}
		
		this.filled = false;
	}
}

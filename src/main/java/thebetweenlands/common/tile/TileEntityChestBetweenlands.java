package thebetweenlands.common.tile;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
	
	private SharedLootPool sharedPool1;
	private SharedLootPool sharedPool2;
	private SharedLootPool sharedPool3;
	
	private ISharedLootPool sharedPool;
	
	//TODO Test, remove
	@Override
	public void fillWithLoot(@Nullable EntityPlayer player) {
		if(this.filled || this.sharedPool == null || this.sharedPool1 == null || this.sharedPool2 == null || this.sharedPool3 == null) {
			return;
		}
		
		this.getItems().clear();
		
		this.filled = true;
		
        LootTable lootTable = this.sharedPool.getLootTableView(Integer.MAX_VALUE, Integer.MAX_VALUE);
        
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

        List<ItemStack> loot = lootTable.generateLootForPools(random, lootcontext$builder.build());
        
        TileEntityLootInventory.fillInventoryRandomly(random, loot, this);
    }
	
	@Override
	public void openInventory(EntityPlayer player) {
		super.openInventory(player);
		
		if(player.isSneaking() || this.sharedPool == null) {
			this.sharedPool1 = new SharedLootPool(LootTableRegistry.DUNGEON_CHEST_LOOT);
			this.sharedPool2 = new SharedLootPool(LootTableRegistry.MIRE_SNAIL);
			this.sharedPool3 = new SharedLootPool(LootTableRegistry.CHIROMAW);
			
			this.sharedPool = sharedPool1.combine(sharedPool2).combine(sharedPool3);
		}
		
		this.filled = false;
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		if(this.sharedPool1 != null) compound.setTag("sharedPool1", this.sharedPool1.writeToNBT(new NBTTagCompound()));
		if(this.sharedPool2 != null) compound.setTag("sharedPool2", this.sharedPool2.writeToNBT(new NBTTagCompound()));
		if(this.sharedPool3 != null) compound.setTag("sharedPool3", this.sharedPool3.writeToNBT(new NBTTagCompound()));
		return super.writeToNBT(compound);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		
		this.sharedPool1 = new SharedLootPool(compound.getCompoundTag("sharedPool1"));
		this.sharedPool2 = new SharedLootPool(compound.getCompoundTag("sharedPool2"));
		this.sharedPool3 = new SharedLootPool(compound.getCompoundTag("sharedPool3"));
		
		this.sharedPool = sharedPool1.combine(sharedPool2).combine(sharedPool3);
	}
}

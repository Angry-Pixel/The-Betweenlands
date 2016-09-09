package thebetweenlands.common.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.ILootContainer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;

import javax.annotation.Nullable;
import java.util.Random;

public class TileEntityLootInventory extends TileEntityBasicInventory implements ILootContainer {
    public ResourceLocation lootTable;
    public long lootTableSeed;

    public TileEntityLootInventory(int invtSize, String name) {
        super(invtSize, name);
    }

    public void fillWithLoot(@Nullable EntityPlayer player) {
        if (this.lootTable != null) {
            LootTable loottable = this.worldObj.getLootTableManager().getLootTableFromLocation(this.lootTable);
            this.lootTable = null;
            Random random;

            if (this.lootTableSeed == 0L) {
                random = new Random();
            } else {
                random = new Random(this.lootTableSeed);
            }

            LootContext.Builder lootBuilder = new LootContext.Builder((WorldServer) this.worldObj);

            if (player != null) {
                lootBuilder.withLuck(player.getLuck());
            }

            loottable.fillInventory(this, random, lootBuilder.build());
        }
    }

    public void setLootTable(ResourceLocation lootTable, long lootTableSeed) {
        this.lootTable = lootTable;
        this.lootTableSeed = lootTableSeed;
    }

    @Override
    public ResourceLocation getLootTable() {
        return lootTable;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if (nbt.hasKey("LootTable", 8)) {
            this.lootTable = new ResourceLocation(nbt.getString("LootTable"));
            this.lootTableSeed = nbt.getLong("LootTableSeed");
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        if (this.lootTable != null) {
            nbt.setString("LootTable", this.lootTable.toString());
            if (this.lootTableSeed != 0L) {
                nbt.setLong("LootTableSeed", this.lootTableSeed);
            }

        }
        return super.writeToNBT(nbt);
    }
}

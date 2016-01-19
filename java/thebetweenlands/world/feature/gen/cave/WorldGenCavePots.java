package thebetweenlands.world.feature.gen.cave;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.misc.ItemGeneric;
import thebetweenlands.tileentities.TileEntityLootPot1;
import thebetweenlands.world.loot.LootItemStack;
import thebetweenlands.world.loot.LootUtil;
import thebetweenlands.world.loot.WeightedLootList;

import java.util.Random;

/**
 * Created by Bart on 17/01/2016.
 */
public class WorldGenCavePots extends WorldGenCave {
    public static final WeightedLootList loot = new WeightedLootList(new LootItemStack[]{
            new LootItemStack(BLItemRegistry.anglerToothArrow).setAmount(32, 48).setWeight(20),
            new LootItemStack(BLItemRegistry.sapBall).setAmount(8, 32).setWeight(20),
            new LootItemStack(BLItemRegistry.poisonedAnglerToothArrow).setAmount(8, 16).setWeight(10),
            new LootItemStack(BLItemRegistry.weepingBluePetal).setAmount(2, 8).setWeight(10),
            new LootItemStack(BLItemRegistry.octineArrow).setAmount(8, 16).setWeight(8),
            new LootItemStack(BLItemRegistry.wightsHeart).setAmount(1, 3).setWeight(8),
            new LootItemStack(BLItemRegistry.basiliskArrow).setAmount(8).setWeight(4),
            new LootItemStack(BLItemRegistry.scroll).setAmount(1).setWeight(4)
    });

    public WorldGenCavePots() {
        super(false);
    }

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        if (world.getBlock(x, y, z) == Blocks.air && world.getBlock(x, y - 1, z).isBlockSolid(world, x, y-1, z, ForgeDirection.UP.ordinal())) {
            int randDirection = random.nextInt(4) + 2;
            world.setBlock(x, y, z , getRandomBlock(random), randDirection, 3);
            TileEntityLootPot1 lootPot = (TileEntityLootPot1) world.getTileEntity(x , y, z);
            if (lootPot != null)
                LootUtil.generateLoot(lootPot, random, loot, 1, 2);
            return true;
        }
        return false;
    }

    private Block getRandomBlock(Random rand) {
        switch(rand.nextInt(3)) {
            case 0: return BLBlockRegistry.lootPot1;
            case 1: return BLBlockRegistry.lootPot2;
            case 2: return BLBlockRegistry.lootPot3;
            default: return BLBlockRegistry.lootPot1;
        }
    }
}

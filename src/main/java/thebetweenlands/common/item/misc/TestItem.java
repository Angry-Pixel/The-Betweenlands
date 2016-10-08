package thebetweenlands.common.item.misc;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.world.storage.chunk.ChunkDataBase;
import thebetweenlands.common.world.storage.world.global.BetweenlandsWorldData;
import thebetweenlands.common.world.storage.world.shared.LocationStorageNew;
import thebetweenlands.common.world.storage.world.shared.SharedStorage;

//MINE!!
public class TestItem extends Item {
	public TestItem() {
		this.setMaxStackSize(1);
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		/*   WorldGenDruidCircle worldGenDruidCircle = new WorldGenDruidCircle();
        worldGenDruidCircle.generateStructure(worldIn, itemRand, pos.getX(), pos.getY() + 1, pos.getZ());*/
		/*if (!world.isRemote) {
            WorldGenIdolHeads head = new WorldGenIdolHeads();
            head.generate(world, itemRand, pos.up());
        }*/
		/*if (!world.isRemote) {
            WorldGenSpawnerStructure smallRuins = new WorldGenSpawnerStructure();
            smallRuins.generate(world, itemRand, pos.up());
        }*/

		/*WorldGenWightFortress fortress = new WorldGenWightFortress();
    		fortress.generate(world, itemRand, pos.up());*/
		BetweenlandsWorldData worldData = BetweenlandsWorldData.forWorld(world);
		if(playerIn.isSneaking()) {
			if(!world.isRemote) {
				LocationStorageNew locationStorage = new LocationStorageNew(worldData, UUID.randomUUID(), new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ()).expand(30, 20, 30));
				locationStorage.linkChunks();
				if(playerIn instanceof EntityPlayerMP) {
					locationStorage.onWatched(ChunkDataBase.forChunk(worldData, world.getChunkFromBlockCoords(pos)), (EntityPlayerMP) playerIn);
				}
				locationStorage.setDirty(true); //Save to file
				worldData.addSharedStorage(locationStorage);
				System.out.println("Created shared storage: " + locationStorage + " with UUID " + locationStorage.getUUIDString());
			}
		} else {
			System.out.println("Shared storages (client: " + world.isRemote + "): ");
			for(SharedStorage storage : worldData.getSharedStorage()) {
				System.out.println(storage);
			}
			/*if(!world.isRemote) {
				for(SharedStorage storage : worldData.getSharedStorage()) {
					storage.unlinkAllChunks();
				}
				System.out.println("Unlinked all references");
			}*/
		}

		return EnumActionResult.SUCCESS;
	}
}

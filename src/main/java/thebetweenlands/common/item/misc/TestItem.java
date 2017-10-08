package thebetweenlands.common.item.misc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.api.aspect.ItemAspectContainer;
import thebetweenlands.common.registries.AspectRegistry;
import thebetweenlands.common.world.gen.feature.structure.*;
import thebetweenlands.common.world.storage.world.global.BetweenlandsWorldData;
import thebetweenlands.common.world.storage.world.shared.SharedStorage;
import thebetweenlands.common.world.storage.world.shared.location.LocationStorage;

import java.util.List;

//MINE!!
public class TestItem extends Item {
	public TestItem() {
		this.setMaxStackSize(1);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
		/*
			WorldGenTarPoolDungeon gen = new WorldGenTarPoolDungeon();
			gen.generate(worldIn, itemRand, pos.up());
		*/
		/*
			WorldGenDruidCircle worldGenDruidCircle = new WorldGenDruidCircle();
			worldGenDruidCircle.generateStructure(worldIn, itemRand, pos.up());
		*/
		/*
            WorldGenIdolHeads head = new WorldGenIdolHeads();
            head.generate(world, itemRand, pos.up());
        */
		/*
            WorldGenSpawnerStructure smallRuins = new WorldGenSpawnerStructure();
            smallRuins.generate(worldIn, itemRand, pos.up());
		*/

		/*
			WorldGenWightFortress fortress = new WorldGenWightFortress();
			fortress.generate(worldIn, itemRand, pos.up());
		*/
		/*
			if(player.isSneaking()) {
				BetweenlandsWorldData worldStorage = BetweenlandsWorldData.forWorld(worldIn);
				List<SharedStorage> storages = worldStorage.getSharedStorageAt(SharedStorage.class, (storage) -> {
					if(storage instanceof LocationStorage) {
						return ((LocationStorage)storage).isInside(pos);
					}
					return true;
				}, pos.getX(), pos.getZ());
				for(SharedStorage storage : storages) {
					worldStorage.removeSharedStorage(storage);
				}
			} else {
				WorldGenWightFortress fortress = new WorldGenWightFortress();
				fortress.generate(worldIn, itemRand, pos.up());
			}
		*/
		/*
			ItemAspectContainer container = ItemAspectContainer.fromItem(player.getHeldItem(hand));
			if(!player.isSneaking()) {
				container.add(AspectRegistry.AZUWYNN, 10);
				System.out.println("Added: 10");
			} else {
				System.out.println("Drained: " + container.drain(AspectRegistry.AZUWYNN, 8));
			}
		*/
		/*
			WorldGenSpawner spawner = new WorldGenSpawner();
			if(spawner.generate(worldIn, itemRand, pos)) {
				//playerIn.setHeldItem(hand, null);
			}
		*/
		/*
			WorldGenCragrockTower tower = new WorldGenCragrockTower();
			if(tower.generate(worldIn, itemRand, pos.up(8).add(8, 0, 0))) {
				//playerIn.setHeldItem(hand, null);
			}
		*/
		}

		return EnumActionResult.SUCCESS;
	}
}

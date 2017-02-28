package thebetweenlands.common.item.misc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.world.gen.feature.WorldGenDeadWeedwoodTree;
import thebetweenlands.common.world.gen.feature.structure.WorldGenDruidCircle;

//MINE!!
public class TestItem extends Item {
	public TestItem() {
		this.setMaxStackSize(1);
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {  
			WorldGenDeadWeedwoodTree worldGenDruidCircle = new WorldGenDeadWeedwoodTree();
			worldGenDruidCircle.generate(world, itemRand, pos.up());
		}
		/*if (!world.isRemote) {  
			WorldGenDruidCircle worldGenDruidCircle = new WorldGenDruidCircle();
			worldGenDruidCircle.generateStructure(world, itemRand, pos.up());
		}*/
		/*if (!world.isRemote) {
            WorldGenIdolHeads head = new WorldGenIdolHeads();
            head.generate(world, itemRand, pos.up());
        }*/
		/*if (!world.isRemote) {
            WorldGenSpawnerStructure smallRuins = new WorldGenSpawnerStructure();
            smallRuins.generate(world, itemRand, pos.up());
        }*/

		/*if (!world.isRemote) {
			WorldGenWightFortress fortress = new WorldGenWightFortress();
			fortress.generate(world, itemRand, pos.up());
		}*/

		//		if(!world.isRemote) {
		//			/*if(playerIn.isSneaking()) {
		//				BetweenlandsWorldData worldStorage = BetweenlandsWorldData.forWorld(world);
		//				List<SharedStorage> storages = worldStorage.getSharedStorageAt(SharedStorage.class, (storage) -> {
		//					if(storage instanceof LocationStorage) {
		//						return ((LocationStorage)storage).isInside(pos);
		//					}
		//					return true;
		//				}, pos.getX(), pos.getZ());
		//				for(SharedStorage storage : storages) {
		//					worldStorage.removeSharedStorage(storage);
		//				}
		//			} else {
		//				WorldGenWightFortress fortress = new WorldGenWightFortress();
		//				fortress.generate(world, itemRand, pos.up());
		//			}*/
		//			ItemAspectContainer container = ItemAspectContainer.fromItem(stack);
		//			if(!playerIn.isSneaking()) {
		//				container.add(AspectRegistry.AZUWYNN, 10);
		//				System.out.println("Added: 10");
		//			} else {
		//				System.out.println("Drained: " + container.drain(AspectRegistry.AZUWYNN, 8));
		//			}
		//		}

		//		if (!world.isRemote) {
		//			/*WorldGenSpawner spawner = new WorldGenSpawner();
		//					if(spawner.generate(world, itemRand, pos)) {
		//						//playerIn.setHeldItem(hand, null);
		//					}*/
		//			WorldGenCragrockTower tower = new WorldGenCragrockTower();
		//			if(tower.generate(world, itemRand, pos.up(8).add(8, 0, 0))) {
		//				//playerIn.setHeldItem(hand, null);
		//			}
		//		}

		return EnumActionResult.SUCCESS;
	}
}

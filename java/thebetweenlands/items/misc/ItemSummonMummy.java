package thebetweenlands.items.misc;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.creativetabs.BLCreativeTabs;
import thebetweenlands.entities.mobs.EntityDreadfulMummy;
import thebetweenlands.world.biomes.base.BLBiomeRegistry;

public class ItemSummonMummy extends Item {
	public ItemSummonMummy() {
		this.setUnlocalizedName("thebetweenlands.mummyBait");
		this.setTextureName("thebetweenlands:mummyBait");
		this.setCreativeTab(BLCreativeTabs.specials);
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		if(entityItem.ticksExisted % 10 == 0) {
			if(entityItem.onGround) {
				int bx = MathHelper.floor_double(entityItem.posX);
				int by = MathHelper.floor_double(entityItem.posY);
				int bz = MathHelper.floor_double(entityItem.posZ);
				BiomeGenBase biome = entityItem.worldObj.getBiomeGenForCoords(bx, bz);
				if(biome == BLBiomeRegistry.sludgePlains || biome == BLBiomeRegistry.marsh1 || biome == BLBiomeRegistry.marsh2) {
					boolean canSpawn = true;
					for(int yo = -3; yo <= -1; yo++) {
						for(int xo = -1; xo <= 1 && canSpawn; xo++) {
							for(int zo = -1; zo <= 1 && canSpawn; zo++) {
								Block block = entityItem.worldObj.getBlock(bx+xo, by+yo, bz+zo);
								if(!block.isNormalCube() && !block.isSideSolid(entityItem.worldObj, bx+xo, by+yo, bz+zo, ForgeDirection.UP))
									canSpawn = false;
							}
						}
					}
					if(canSpawn) {
						if(entityItem.worldObj.isRemote) {
							for(int xo = -1; xo <= 1 && canSpawn; xo++) {
								for(int zo = -1; zo <= 1 && canSpawn; zo++) {
									Block block = entityItem.worldObj.getBlock(bx+xo, by-1, bz+zo);
									int metadata = entityItem.worldObj.getBlockMetadata(bx+xo, by-1, bz+zo);
									String particle = "blockdust_" + Block.getIdFromBlock(block) + "_" + metadata;
									for (int i = 0, amount = 12 + entityItem.worldObj.rand.nextInt(20); i < amount; i++) {
										double ox = entityItem.worldObj.rand.nextDouble();
										double oy = entityItem.worldObj.rand.nextDouble() * 3;
										double oz = entityItem.worldObj.rand.nextDouble();
										double motionX = entityItem.worldObj.rand.nextDouble() * 0.2 - 0.1;
										double motionY = entityItem.worldObj.rand.nextDouble() * 0.1 + 0.1;
										double motionZ = entityItem.worldObj.rand.nextDouble() * 0.2 - 0.1;
										entityItem.worldObj.spawnParticle(particle, bx+xo + ox, by, bz+zo + oz, motionX, motionY, motionZ);
										BLParticle.SMOKE.spawn(entityItem.worldObj, bx+xo + ox, by + oy, bz+zo + oz, 0, 0.25F, 0, 1);
									}
								}
							}
						} else {
							EntityDreadfulMummy boss = new EntityDreadfulMummy(entityItem.worldObj);
							boss.setLocationAndAngles(entityItem.posX, entityItem.posY, entityItem.posZ, 0, 0);
							if(boss.getCanSpawnHere()) {
								entityItem.worldObj.spawnEntityInWorld(boss);
								entityItem.setDead();
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
}

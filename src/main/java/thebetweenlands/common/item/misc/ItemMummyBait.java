package thebetweenlands.common.item.misc;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.mobs.EntityDreadfulMummy;
import thebetweenlands.common.registries.BiomeRegistry;

public class ItemMummyBait extends Item {

    public ItemMummyBait() {
        this.setCreativeTab(BLCreativeTabs.SPECIALS);
    }

    @Override
    public boolean onEntityItemUpdate(EntityItem entityItem) {
        if(entityItem.ticksExisted % 10 == 0) {
            if(entityItem.onGround) {
                int bx = MathHelper.floor(entityItem.posX);
                int by = MathHelper.floor(entityItem.posY);
                int bz = MathHelper.floor(entityItem.posZ);
                BlockPos.PooledMutableBlockPos pos = BlockPos.PooledMutableBlockPos.retain();
                Biome biome = entityItem.world.getBiome(pos.setPos(bx, by, bz));
                if(biome == BiomeRegistry.SLUDGE_PLAINS || biome == BiomeRegistry.MARSH_0 || biome == BiomeRegistry.MARSH_1) {
                    boolean canSpawn = true;
                    for(int yo = -3; yo <= -1; yo++) {
                        for(int xo = -1; xo <= 1 && canSpawn; xo++) {
                            for(int zo = -1; zo <= 1 && canSpawn; zo++) {
                                IBlockState state = entityItem.world.getBlockState(pos.setPos(bx+xo, by+yo, bz+zo));
                                if(!state.isNormalCube() && !state.isSideSolid(entityItem.world, pos, EnumFacing.UP))
                                    canSpawn = false;
                            }
                        }
                    }
                    if(canSpawn) {
                        if(entityItem.world.isRemote) {
                            for(int xo = -1; xo <= 1 && canSpawn; xo++) {
                                for(int zo = -1; zo <= 1 && canSpawn; zo++) {
                                    IBlockState state = entityItem.world.getBlockState(pos.setPos(bx+xo, by-1, bz+zo));
                                    int stateId = Block.getStateId(state);
                                    for (int i = 0, amount = 12 + entityItem.world.rand.nextInt(20); i < amount; i++) {
                                        double ox = entityItem.world.rand.nextDouble();
                                        double oy = entityItem.world.rand.nextDouble() * 3;
                                        double oz = entityItem.world.rand.nextDouble();
                                        double motionX = entityItem.world.rand.nextDouble() * 0.2 - 0.1;
                                        double motionY = entityItem.world.rand.nextDouble() * 0.1 + 0.1;
                                        double motionZ = entityItem.world.rand.nextDouble() * 0.2 - 0.1;
                                        entityItem.world.spawnParticle(EnumParticleTypes.BLOCK_DUST, bx+xo + ox, by, bz+zo + oz, motionX, motionY, motionZ, stateId);
                                        BLParticles.SMOKE.spawn(entityItem.world, bx+xo + ox, by + oy, bz+zo + oz, ParticleFactory.ParticleArgs.get().withColor(-1, 0xDEAD, 0xC0DE, 1).withMotion(0, 0.25F, 0).withScale(1));
                                    }
                                }
                            }
                        } else {
                            EntityDreadfulMummy boss = new EntityDreadfulMummy(entityItem.world);
                            boss.setLocationAndAngles(entityItem.posX, entityItem.posY, entityItem.posZ, 0, 0);
                            if(boss.getCanSpawnHere()) {
                                entityItem.world.spawnEntity(boss);
                                entityItem.setDead();
                                pos.release();
                                return true;
                            }
                        }
                    }
                }
                pos.release();
            }
        }
        return false;
    }

    @Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.UNCOMMON;
	}
}

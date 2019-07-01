package thebetweenlands.common.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.api.capability.IEntityCustomCollisionsCapability;
import thebetweenlands.api.capability.IEntityCustomCollisionsCapability.BlockCollisionPredicate;
import thebetweenlands.api.capability.IEntityCustomCollisionsCapability.CollisionBoxHelper;
import thebetweenlands.api.capability.IEntityCustomCollisionsCapability.EntityCollisionPredicate;
import thebetweenlands.api.entity.IEntityCustomBlockCollisions;
import thebetweenlands.api.entity.ProcessedEntityCollisionBox;
import thebetweenlands.common.registries.CapabilityRegistry;

public final class CustomEntityCollisionsHandler {
	public static final class Helper implements CollisionBoxHelper {
		private Helper() { }

		@Override
		public void getCollisionBoxes(Entity entity, AxisAlignedBB aabb, EntityCollisionPredicate entityPredicate,
				BlockCollisionPredicate blockPredicate, List<AxisAlignedBB> collisionBoxes) {
			CustomEntityCollisionsHandler.getCollisionBoxes(entity, aabb, entityPredicate, blockPredicate, collisionBoxes);
		}
	}

	public static final Helper HELPER = new Helper();

	private CustomEntityCollisionsHandler() {}

	private static boolean gathering = false;

	@SubscribeEvent
	public static void onGatherCollisionBoxes(GetCollisionBoxesEvent event) {
		if(!gathering) {
			Entity entity = event.getEntity();
			gathering = true;
			try {
				if(entity != null) {
					IEntityCustomCollisionsCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_ENTITY_CUSTOM_BLOCK_COLLISIONS, null);

					if(cap != null || entity instanceof IEntityCustomBlockCollisions) {
						if(cap != null) {
							cap.getCustomCollisionBoxes(HELPER, event.getAabb(), event.getCollisionBoxesList());
						} else {
							((IEntityCustomBlockCollisions) event.getEntity()).getCustomCollisionBoxes(event.getAabb(), event.getCollisionBoxesList());
						}
					}
				}

				/*List<AxisAlignedBB> processedAabbList = null;

				Iterator<AxisAlignedBB> it = event.getCollisionBoxesList().iterator();
				while(it.hasNext()) {
					AxisAlignedBB aabb = it.next();
					if(aabb instanceof ProcessedEntityCollisionBox) {
						it.remove();

						AxisAlignedBB processedAabb = ((ProcessedEntityCollisionBox<?>) aabb).process(event.getEntity(), event.getAabb());
						if(processedAabb != null) {
							if(processedAabbList == null) {
								processedAabbList = new ArrayList<>();
							}

							processedAabbList.add(processedAabb);
						}
					}
				}

				if(processedAabbList != null) {
					event.getCollisionBoxesList().addAll(processedAabbList);
				}*/
			} finally {
				gathering = false;
			}
		}
	}

	private static void getCollisionBoxes(Entity entity, AxisAlignedBB aabb, EntityCollisionPredicate entityPredicate, BlockCollisionPredicate blockPredicate, List<AxisAlignedBB> collisionBoxes) {
		getBlockCollisionBoxes(entity, aabb, blockPredicate, collisionBoxes);
		getEntityCollisionBoxes(entity, aabb, entityPredicate, collisionBoxes);
		MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.GetCollisionBoxesEvent(entity.world, entity, aabb, collisionBoxes));
	}

	private static void getEntityCollisionBoxes(Entity entity, AxisAlignedBB aabb, EntityCollisionPredicate entityPredicate, List<AxisAlignedBB> collisionBoxes) {
		if (entity != null) {
			List<Entity> otherEntities = entity.world.getEntitiesWithinAABBExcludingEntity(entity, aabb.grow(0.25D));

			for (Entity otherEntity : otherEntities) {
				if (!entity.isRidingSameEntity(otherEntity)) {
					AxisAlignedBB otherAABB = otherEntity.getCollisionBoundingBox();

					if (otherAABB != null && otherAABB.intersects(aabb) && entityPredicate.isColliding(entity, aabb, otherEntity, otherAABB)) {
						collisionBoxes.add(otherAABB);
					}

					otherAABB = entity.getCollisionBox(otherEntity);

					if (otherAABB != null && otherAABB.intersects(aabb) && entityPredicate.isColliding(entity, aabb, otherEntity, otherAABB)) {
						collisionBoxes.add(otherAABB);
					}
				}
			}
		}
	}

	private static void getBlockCollisionBoxes(Entity entity, AxisAlignedBB aabb, BlockCollisionPredicate blockPredicate, List<AxisAlignedBB> collisionBoxes) {
		World world = entity.world;
		int xs = MathHelper.floor(aabb.minX) - 1;
		int xe = MathHelper.ceil(aabb.maxX) + 1;
		int ys = MathHelper.floor(aabb.minY) - 1;
		int ye = MathHelper.ceil(aabb.maxY) + 1;
		int zs = MathHelper.floor(aabb.minZ) - 1;
		int ze = MathHelper.ceil(aabb.maxZ) + 1;
		WorldBorder worldBorder = world.getWorldBorder();
		boolean isOutsideWorldBorder = entity.isOutsideBorder();
		boolean isInsideWorldBorder = world.isInsideWorldBorder(entity);
		IBlockState defaultBlockState = Blocks.STONE.getDefaultState();
		BlockPos.PooledMutableBlockPos checkPos = BlockPos.PooledMutableBlockPos.retain();

		try {
			for(int x = xs; x < xe; ++x) {
				for(int z = zs; z < ze; ++z) {
					boolean borderX = x == xs || x == xe - 1;
					boolean borderZ = z == zs || z == ze - 1;

					if((!borderX || !borderZ) && world.isBlockLoaded(checkPos.setPos(x, 64, z))) {
						for(int y = ys; y < ye; ++y) {
							if(!borderX && !borderZ || y != ye - 1) {
								if(entity != null && isOutsideWorldBorder == isInsideWorldBorder) {
									entity.setOutsideBorder(!isInsideWorldBorder);
								}

								checkPos.setPos(x, y, z);
								IBlockState state;

								if (!worldBorder.contains(checkPos) && isInsideWorldBorder) {
									state = defaultBlockState;
								} else {
									state = world.getBlockState(checkPos);
								}

								if(blockPredicate.isColliding(entity, aabb, checkPos, state, null)) {
									List<AxisAlignedBB> blockBoxes = new ArrayList<>();
									state.addCollisionBoxToList(world, checkPos, aabb, blockBoxes, entity, false);
									for(AxisAlignedBB blockAabb : blockBoxes) {
										if(blockPredicate.isColliding(entity, aabb, checkPos, state, blockAabb)) {
											collisionBoxes.add(blockAabb);
										}
									}
								}
							}
						}
					}
				}
			}
		} finally {
			checkPos.release();
		}
	}
}

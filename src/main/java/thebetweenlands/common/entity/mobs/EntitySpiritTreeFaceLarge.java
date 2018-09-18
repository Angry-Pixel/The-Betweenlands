package thebetweenlands.common.entity.mobs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationSpiritTree;

public class EntitySpiritTreeFaceLarge extends EntitySpiritTreeFace {
	public EntitySpiritTreeFaceLarge(World world) {
		super(world);
		this.setSize(1.8F, 1.8F);
	}

	@Override
	protected void initEntityAI() {
		super.initEntityAI();

		this.tasks.addTask(3, new AIRespawnSmallFaces(this));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();

		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(8.0D);
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.SPIRIT_TREE_FACE_LARGE;
	}

	@Override
	public List<BlockPos> findNearbyWoodBlocks() {
		List<LocationSpiritTree> locations = BetweenlandsWorldStorage.forWorld(this.world).getLocalStorageHandler().getLocalStorages(LocationSpiritTree.class, this.getEntityBoundingBox(), loc -> loc.isInside(this));
		if(!locations.isEmpty()) {
			List<BlockPos> positions = locations.get(0).getLargeFacePositions();
			if(!positions.isEmpty()) {
				return positions;
			}
		}
		return super.findNearbyWoodBlocks();
	}

	protected List<BlockPos> findSmallFacesBlocks() {
		List<LocationSpiritTree> locations = BetweenlandsWorldStorage.forWorld(this.world).getLocalStorageHandler().getLocalStorages(LocationSpiritTree.class, this.getEntityBoundingBox(), loc -> loc.isInside(this));
		if(!locations.isEmpty()) {
			List<BlockPos> positions = locations.get(0).getSmallFacePositions();
			if(!positions.isEmpty()) {
				return positions;
			}
		}
		return super.findNearbyWoodBlocks();
	}

	public static class AIRespawnSmallFaces extends EntityAIBase {
		protected final EntitySpiritTreeFaceLarge entity;

		protected int executeCheckCooldown = 0;

		protected boolean shouldContinue = true;

		protected int spawnCooldown = 0;

		public AIRespawnSmallFaces(EntitySpiritTreeFaceLarge entity) {
			this.entity = entity;
		}

		protected int countSmallFaces() {
			return this.entity.world.getEntitiesWithinAABB(EntitySpiritTreeFaceSmall.class, this.entity.getEntityBoundingBox().grow(40.0D)).size();
		}

		protected boolean hasEnoughSmallFaces() {
			return this.countSmallFaces() >= 8;
		}

		@Override
		public boolean shouldExecute() {
			if(this.executeCheckCooldown <= 0) {
				this.executeCheckCooldown = 20 + this.entity.rand.nextInt(20);
				return !this.hasEnoughSmallFaces();
			}
			this.executeCheckCooldown--;
			return false;
		}

		@Override
		public void startExecuting() {
			this.shouldContinue = true;
		}

		@Override
		public void updateTask() {
			if(this.spawnCooldown <= 0) {
				this.spawnCooldown = 180 + this.entity.rand.nextInt(100);
				List<BlockPos> blocks = this.entity.findSmallFacesBlocks();

				EntitySpiritTreeFaceSmall face = new EntitySpiritTreeFaceSmall(this.entity.world);

				spawnLoop: for(int i = 0; i < 16; i++) {
					BlockPos anchor = blocks.get(this.entity.rand.nextInt(blocks.size()));

					List<EnumFacing> facings = new ArrayList<>();
					facings.addAll(Arrays.asList(EnumFacing.VALUES));
					Collections.shuffle(facings, this.entity.rand);

					for(EnumFacing facing : facings) {
						EnumFacing facingUp = facing.getAxis().isVertical() ? EnumFacing.HORIZONTALS[this.entity.rand.nextInt(EnumFacing.HORIZONTALS.length)] : EnumFacing.UP;
						if(face.canAnchorAt(anchor, facing, facingUp)) {
							face.onInitialSpawn(this.entity.world.getDifficultyForLocation(anchor), null);
							face.setPositionToAnchor(anchor, facing, facingUp);
							this.entity.world.spawnEntity(face);
							break spawnLoop;
						}
					}
				}

				if(this.hasEnoughSmallFaces()) {
					this.shouldContinue = false;
				}
			}
			this.spawnCooldown--;
		}

		@Override
		public boolean shouldContinueExecuting() {
			return this.shouldContinue;
		}
	}
}

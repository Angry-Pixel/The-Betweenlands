package thebetweenlands.common.entity.mobs;

import java.util.List;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationSpiritTree;

public class EntitySpiritTreeFaceSmall extends EntitySpiritTreeFace {
	public EntitySpiritTreeFaceSmall(World world) {
		super(world);
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.SPIRIT_TREE_FACE_SMALL;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(2.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
	}

	@Override
	public List<BlockPos> findNearbyWoodBlocks() {
		List<LocationSpiritTree> locations = BetweenlandsWorldStorage.forWorld(this.world).getLocalStorageHandler().getLocalStorages(LocationSpiritTree.class, this.getEntityBoundingBox(), loc -> loc.isInside(this));
		if(!locations.isEmpty()) {
			List<BlockPos> positions = locations.get(0).getSmallFacePositions();
			if(!positions.isEmpty()) {
				return positions;
			}
		}
		return super.findNearbyWoodBlocks();
	}
}

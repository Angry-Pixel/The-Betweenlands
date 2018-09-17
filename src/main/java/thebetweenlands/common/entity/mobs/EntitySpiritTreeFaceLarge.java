package thebetweenlands.common.entity.mobs;

import java.util.List;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(8.0D);
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
}

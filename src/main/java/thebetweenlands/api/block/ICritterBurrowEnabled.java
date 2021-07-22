package thebetweenlands.api.block;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.world.World;
import thebetweenlands.common.entity.mobs.EntityGecko;
import thebetweenlands.common.entity.mobs.EntitySiltCrab;
import thebetweenlands.common.registries.BlockRegistry;

public interface ICritterBurrowEnabled {

	public static boolean isSuitableBurrowBlock(Block blockIn) {
		for(EnumBurrowParts blocks : EnumBurrowParts.values())
			if (blocks.getBurrowBlock() == blockIn)
				return true;
		return false;
	}

	@Nullable
	public static Entity getEntityForBlockType(World world, Block blockIn) {
		for(EnumBurrowParts blocks : EnumBurrowParts.values())
			if (blocks.getBurrowBlock() == blockIn)
				if (blocks.getBurrowEntityClass() != null)
					return EntityList.newEntity(blocks.getBurrowEntityClass(), world);
		return null;
	};

	public enum EnumBurrowParts {
		//CRAGROCK(BlockRegistry.CRAGROCK, EntityPuffin.class),
		SILT(BlockRegistry.SILT, EntitySiltCrab.class),
		MUD(BlockRegistry.MUD, EntityGecko.class);

		Block block; // block of burrow
		Class entityClass; // entity class for above

		EnumBurrowParts(Block block, Class entityClass) {
			this.block = block;
			this.entityClass = entityClass;
		}

		public Block getBurrowBlock() {
			return block;
		}

		public Class getBurrowEntityClass() {
			return entityClass;
		}
	}
}
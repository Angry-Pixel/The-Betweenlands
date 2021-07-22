package thebetweenlands.api.block;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.world.World;
import thebetweenlands.common.entity.mobs.EntityGecko;
import thebetweenlands.common.entity.mobs.EntitySiltCrab;
import thebetweenlands.common.registries.BlockRegistry;

public interface ICritterBurrowEnabled {

	public static boolean isSuitableBurrowBlock(Block blockIn) {
		for(EnumBurrowParts blocks : EnumBurrowParts.values())
			if (blocks.getOriginalBlock() == blockIn)
				return true;
		return false;
	}

	@Nullable
	public static Class getEntityForBlockType(World world, Block blockIn) {
		for(EnumBurrowParts blocks : EnumBurrowParts.values())
			if (blocks.getOriginalBlock() == blockIn)
				if (blocks.getBurrowEntityClass() != null)
					return blocks.getBurrowEntityClass();
					//return EntityList.newEntity(blocks.getBurrowEntityClass(), world);
		return null;
	};

	public static IBlockState getBurrowBlock(Block blockIn) {
		for(EnumBurrowParts blocks : EnumBurrowParts.values())
			if (blocks.getOriginalBlock() == blockIn)
				return blocks.getBurrowBlock();
		return null;
	};

	public enum EnumBurrowParts {
		//CRAGROCK(BlockRegistry.CRAGROCK, EntityPuffin.class),
		SILT(BlockRegistry.SILT, EntitySiltCrab.class, BlockRegistry.ANIMAL_BURROW_SILT.getDefaultState()),
		MUD(BlockRegistry.MUD, EntityGecko.class, BlockRegistry.ANIMAL_BURROW_MUD.getDefaultState());

		Block blockOrigin; // Original block
		Class entityClass; // entity class for above
		IBlockState blockBurrow; // New Block to place

		EnumBurrowParts(Block blockOrigin, Class entityClass, IBlockState blockBurrow) {
			this.blockOrigin = blockOrigin;
			this.entityClass = entityClass;
			this.blockBurrow = blockBurrow;
		}

		public Block getOriginalBlock() {
			return blockOrigin;
		}
		
		public IBlockState getBurrowBlock() {
			return blockBurrow;
		}

		public Class getBurrowEntityClass() {
			return entityClass;
		}
	}
}
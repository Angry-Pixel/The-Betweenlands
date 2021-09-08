package thebetweenlands.api.block;

public interface ICritterBurrowEnabled {
/*
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
		return null;
	};

	public static IBlockState getBurrowBlock(Block blockIn) {
		for(EnumBurrowParts blocks : EnumBurrowParts.values())
			if (blocks.getOriginalBlock() == blockIn)
				return blocks.getBurrowBlock();
		return null;
	};

	public enum EnumBurrowParts {
		CRAGROCK(BlockRegistry.CRAGROCK, EntityPuffin.class,BlockRegistry.ANIMAL_BURROW_CRAGROCK.getDefaultState()),
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
*/
}
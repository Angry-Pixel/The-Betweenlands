package thebetweenlands.common.block.terrain;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.block.BLBlockMaterial;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.item.armor.ItemRubberBoots;
import thebetweenlands.common.registries.ItemRegistry;


public class BlockMud extends Block {
	protected static final VoxelShape MUD_SHAPE = VoxelShapes.create(0.0D, 0.0D, 0.0D, 1.0D, 0.875D, 1.0D);

	public BlockMud() {
		super(Properties.create(BLBlockMaterial.MUD)
				.hardnessAndResistance(0.5F, 0.0F)
				.sound(SoundType.GROUND));
	}

	@Override
	public ToolType getHarvestTool(IBlockState state) {
		return ToolType.SHOVEL;
	}

	public boolean canEntityWalkOnMud(Entity entity) {
		//TODO 1.13 Removed elixir check for walking on mud
		/*if (entity instanceof EntityLivingBase && ElixirEffectRegistry.EFFECT_HEAVYWEIGHT.isActive((EntityLivingBase) entity))
			return false;*/
		boolean canWalk = entity instanceof EntityPlayer /*&& ItemRubberBoots.canEntityWalkOnMud(entity)*/;
		boolean hasLurkerArmor = entity instanceof EntityPlayer && entity.isInWater() && !((EntityPlayer) entity).inventory.armorInventory.isEmpty() && ((EntityPlayer) entity).inventory.armorInventory.get(0).getItem() == ItemRegistry.LURKER_SKIN_BOOTS;
		return entity instanceof IEntityBL || entity instanceof EntityItem || canWalk || hasLurkerArmor || (entity instanceof EntityPlayer && ((EntityPlayer) entity).abilities.isCreativeMode && ((EntityPlayer) entity).abilities.isFlying);
	}

	@Override
	public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
		return VoxelShapes.fullCube();
	}

	@Override
	public VoxelShape getCollisionShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
		return MUD_SHAPE;
	}

	//TODO 1.13 Mud block used to return full AABB when wearing boots, can't be done anymore atm

	public static Material getMaterialAtEyes(Entity entity, float partialTicks) {
		Vec3d eyePos = entity.getEyePosition(partialTicks);
		BlockPos pos = new BlockPos(eyePos);
		IBlockState state = entity.world.getBlockState(pos);
		if(state.getCollisionShape(entity.world, pos).func_212433_a(eyePos, eyePos.add(entity.getLookVec().normalize().scale(0.001D)), pos) != null) {
			return state.getMaterial();
		}
		return Material.AIR;
	}

	@Override
	public void onEntityCollision(IBlockState state, World world, BlockPos pos, Entity entity){
		if (!canEntityWalkOnMud(entity)) {
			entity.motionX *= 0.08D;
			if(!entity.isInWater() && entity.motionY < 0 && entity.onGround) entity.motionY = -0.1D;
			entity.motionZ *= 0.08D;
			if(!entity.isInWater()) {
				entity.setInWeb();
			} else {
				entity.motionY *= 0.02D;
			}
			entity.onGround = true;
			if(entity instanceof EntityLivingBase && getMaterialAtEyes(entity, 1) == BLBlockMaterial.MUD) {
				entity.attackEntityFrom(DamageSource.IN_WALL, 2.0F);
			}
		}
	}

	@Override
	public boolean isNormalCube(IBlockState state, IBlockReader world, BlockPos pos) {
		return false;
	}
}
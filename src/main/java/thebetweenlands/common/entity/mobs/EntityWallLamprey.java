package thebetweenlands.common.entity.mobs;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.entity.ai.EntityAIHurtByTargetImproved;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.LootTableRegistry;

//TODO Don't extent spirit tree face
public class EntityWallLamprey extends EntitySpiritTreeFace {
	public EntityWallLamprey(World world) {
		super(world);
	}

	@Override
	protected void initEntityAI() {
		super.initEntityAI();

		this.targetTasks.addTask(0, new EntityAIHurtByTargetImproved(this, true));
		this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, false));

		this.tasks.addTask(0, new AITrackTarget(this, true, 28.0D));
		this.tasks.addTask(1, new AIAttackMelee(this, 1, true));
		this.tasks.addTask(2, new AISpit(this, 3.0F));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(2.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.SPIRIT_TREE_FACE_SMALL;
	}

	@Override
	public boolean canResideInBlock(BlockPos pos) {
		IBlockState state = this.world.getBlockState(pos);
		return state.getBlock() == BlockRegistry.MUD_BRICKS ||
				state.getBlock() == BlockRegistry.MUD_BRICKS_CARVED ||
				state.getBlock() == BlockRegistry.MUD_TILES;
	}

	@Override
	public List<BlockPos> findNearbyWoodBlocks() {
		final int radius = 8;
		BlockPos center = new BlockPos(this);
		List<BlockPos> blocks = new ArrayList<>();
		MutableBlockPos pos = new MutableBlockPos();

		for (int dx = -radius; dx <= radius; dx++) {
			for (int dy = -radius; dy <= radius; dy++) {
				for (int dz = -radius; dz <= radius; dz++) {
					pos.setPos(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
					IBlockState state = this.world.getBlockState(pos);

					if (state.getBlock() == BlockRegistry.MUD_BRICKS ||
							state.getBlock() == BlockRegistry.MUD_BRICKS_CARVED ||
							state.getBlock() == BlockRegistry.MUD_TILES) {
						blocks.add(pos.toImmutable());
					}
				}
			}
		}

		return blocks;
	}
}

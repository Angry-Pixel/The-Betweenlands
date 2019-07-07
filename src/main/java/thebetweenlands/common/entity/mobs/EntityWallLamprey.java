package thebetweenlands.common.entity.mobs;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.common.entity.ai.EntityAIHurtByTargetImproved;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.LootTableRegistry;

//TODO Loot tables
//TODO Don't extent spirit tree face
public class EntityWallLamprey extends EntitySpiritTreeFace {
	private static final DataParameter<Boolean> HIDDEN = EntityDataManager.createKey(EntityWallLamprey.class, DataSerializers.BOOLEAN);

	private float prevHiddenPercent = 1.0F;
	private float hiddenPercent = 1.0F;

	public EntityWallLamprey(World world) {
		super(world);
	}

	@Override
	protected void entityInit() {
		super.entityInit();

		this.dataManager.register(HIDDEN, true);
	}

	@Override
	protected void initEntityAI() {
		super.initEntityAI();

		this.targetTasks.addTask(0, new EntityAIHurtByTargetImproved(this, true));
		this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, false));

		this.tasks.addTask(0, new AITrackTarget(this, true, 28.0D));
		this.tasks.addTask(1, new AIAttackMelee(this, 1, true));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.15D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if(!this.world.isRemote) {
			this.dataManager.set(HIDDEN, this.getAttackTarget() == null);
		} else {
			this.prevHiddenPercent = this.hiddenPercent;

			if(this.dataManager.get(HIDDEN)) {
				if(this.hiddenPercent < 1.0F) {
					this.hiddenPercent += 0.01F;
					if(this.hiddenPercent > 1.0F) {
						this.hiddenPercent = 1.0F;
					}
				}
			} else {
				if(this.hiddenPercent > 0.0F) {
					this.hiddenPercent -= 0.04F;
					if(this.hiddenPercent < 0.0F) {
						this.hiddenPercent = 0.0F;
					}
				}
			}
		}
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.WALL_LAMPREY;
	}

	@Override
	public boolean canResideInBlock(BlockPos pos, EnumFacing facing, EnumFacing facingUp) {
		return this.canResideInState(this.world.getBlockState(pos)) && this.canResideInState(this.world.getBlockState(pos.offset(facingUp.getOpposite())));
	}

	protected boolean canResideInState(IBlockState state) {
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

	@Override
	public Vec3d getOffset(float movementProgress) {
		return super.getOffset(1.0F);
	}

	public float getHoleDepthPercent(float partialTicks) {
		return this.getHalfMovementProgress(partialTicks);
	}

	public float getLampreyHiddenPercent(float partialTicks) {
		return 1 - (1 - this.easeInOut(this.prevHiddenPercent + (this.hiddenPercent - this.prevHiddenPercent) * partialTicks)) * this.getHoleDepthPercent(partialTicks);
	}

	private float easeInOut(float percent) {
		float sq = percent * percent;
		return sq / (2.0f * (sq - percent) + 1.0f);
	}
}

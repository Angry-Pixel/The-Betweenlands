package thebetweenlands.common.entity.mobs;

import java.util.Random;

import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.math.BlockPos.PooledMutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.common.entity.ai.EntityAIFlyRandomly;
import thebetweenlands.common.entity.movement.FlightMoveHelper;

public class EntityFirefly extends EntityFlying implements IMob, IEntityBL {
	protected double aboveLayer = 6.0D;

	public EntityFirefly(World world) {
		super(world);
		this.setSize(0.6F, 0.6F);
		this.ignoreFrustumCheck = true;
		this.moveHelper = new FlightMoveHelper(this);
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(5, new EntityAIFlyRandomly(this) {
			@Override
			protected double getTargetY(Random rand, double distanceMultiplier) {
				int worldHeight = MathHelper.floor_double(EntityFirefly.this.aboveLayer);

				PooledMutableBlockPos checkPos = PooledMutableBlockPos.retain();

				for(int yo = 0; yo < MathHelper.ceiling_double_int(EntityFirefly.this.aboveLayer); yo++) {
					checkPos.setPos(this.entity.posX, this.entity.posY - yo, this.entity.posZ);

					if(!this.entity.getEntityWorld().isBlockLoaded(checkPos))
						return this.entity.posY;
					
					if(!this.entity.getEntityWorld().isAirBlock(checkPos)) {
						worldHeight = checkPos.getY();
						break;
					}
				}

				checkPos.release();

				if(this.entity.posY > worldHeight + EntityFirefly.this.aboveLayer) {
					return this.entity.posY + (-rand.nextFloat() * 2.0F) * 16.0F * distanceMultiplier;
				} else {
					float rndFloat = rand.nextFloat() * 2.0F - 1.0F;
					if(rndFloat > 0.0D) {
						double maxRange = worldHeight + EntityFirefly.this.aboveLayer - this.entity.posY;
						return this.entity.posY + (-rand.nextFloat() * 2.0F) * maxRange * distanceMultiplier;
					} else {
						return this.entity.posY + (rand.nextFloat() * 2.0F - 1.0F) * 16.0F * distanceMultiplier;
					}
				}
			}

			@Override
			protected double getFlightSpeed() {
				return 0.035D;
			}
		});
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(4.0D);
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 1;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (this.isInWater()) {
			this.moveHelper.setMoveTo(this.posX, this.posY + 1.0D, this.posZ, 0.1D);
		}
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}
}

package thebetweenlands.entities.mobs;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import thebetweenlands.client.model.ControlledAnimation;
import thebetweenlands.manual.ManualManager;

/**
 * Created by jnad325 on 7/14/15.
 */
public class EntityGiantToad extends EntityCreature implements IEntityBL {
	private int leapOffset;
	private boolean prevOnGround;
	private ControlledAnimation leapingAnim = new ControlledAnimation(4);

	public EntityGiantToad(World worldObj) {
		super(worldObj);
		getNavigator().setAvoidsWater(true);
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(5, new EntityAIWander(this, 0));
		leapOffset = rand.nextInt(29);
		this.setSize(2F, 1.5F);
	}

	@Override
	public boolean isAIEnabled() {
		return true;
	}

	@Override
	public void onUpdate() {
		prevOnGround = onGround;
		super.onUpdate();
		if (!worldObj.isRemote) {
			if (getAttackTarget() != null) {
				getNavigator().tryMoveToEntityLiving(getAttackTarget(), 0);
			}
			PathEntity path = getNavigator().getPath();
			if (path != null && !path.isFinished() && onGround) {
				if (ticksExisted % 30 == leapOffset) {
					int index = path.getCurrentPathIndex();
					if (index < path.getCurrentPathLength()) {
						PathPoint nextHopSpot = path.getPathPointFromIndex(index);
						float x = (float) (nextHopSpot.xCoord - posX);
						float z = (float) (nextHopSpot.zCoord - posZ);
						float angle = (float) (Math.atan2(z, x));
						float distance = (float) Math.sqrt(x * x + z * z);
						if (distance > 1) {
							motionY += 0.5;
							motionX += 0.3 * MathHelper.cos(angle);
							motionZ += 0.3 * MathHelper.sin(angle);
						} else {
							getNavigator().clearPathEntity();
						}
					}
				}
			}
		} else {
			leapingAnim.updateTimer();
			// allow 1 tick lag time to prevent single tick onGround == false
			if (onGround || prevOnGround) {
				leapingAnim.decreaseTimer();
			} else {
				leapingAnim.increaseTimer();
			}
		}
	}

	public float getLeapProgress(float partialRenderTicks) {
		return easeInOut(leapingAnim.getAnimationProgressSinSqrt(partialRenderTicks));
	}

	private static float easeInOut(float t) {
		return t;
//		final float d = 1, c = 1, b = 0;
//		float s = 1.70158f;
//		if ((t /= d / 2) < 1)
//			return c / 2 * (t * t * (((s *= (1.525f)) + 1) * t - s)) + b;
//		return c / 2 * ((t -= 2) * t * (((s *= (1.525f)) + 1) * t + s) + 2) + b;
	}

	@Override
	public String pageName() {
		return "giantToad";
	}
}

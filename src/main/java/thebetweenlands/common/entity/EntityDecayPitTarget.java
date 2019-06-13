package thebetweenlands.common.entity;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityDecayPitTarget extends Entity implements IEntityMultiPartPitTarget {
	public int animationTicks = 0;
	public int animationTicksPrev = 0;
	public EntityDecayPitTargetPart[] shield_array;
	public EntityDecayPitTargetPart shield_1;
	public EntityDecayPitTargetPart shield_2;
	public EntityDecayPitTargetPart shield_3;
	public EntityDecayPitTargetPart shield_4;
	public EntityDecayPitTargetPart shield_5;
	public EntityDecayPitTargetPart shield_6;
	public EntityDecayPitTargetPart shield_7;
	public EntityDecayPitTargetPart shield_8;

	public EntityDecayPitTarget(World world) {
		super(world);
		setSize(1F, 1F);
		shield_array = new EntityDecayPitTargetPart[] {
				shield_1 = new EntityDecayPitTargetPart(this, "part1", 0.625F, 1F),
				shield_2 = new EntityDecayPitTargetPart(this, "part2", 0.625F, 1F),
				shield_3 = new EntityDecayPitTargetPart(this, "part3", 0.625F, 1F),
				shield_4 = new EntityDecayPitTargetPart(this, "part4", 0.6255F, 1F),
				shield_5 = new EntityDecayPitTargetPart(this, "part5", 0.625F, 1F),
				shield_6 = new EntityDecayPitTargetPart(this, "part6", 0.625F, 1F),
				shield_7 = new EntityDecayPitTargetPart(this, "part7", 0.625F, 1F),
				shield_8 = new EntityDecayPitTargetPart(this, "part8", 0.625F, 1F)
				};
	}

	@Override
	protected void entityInit() {
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		animationTicksPrev = animationTicks;
		animationTicks += 3;
		if (animationTicks >= 357)
			animationTicks = animationTicksPrev = 0;
		setNewShieldHitboxPos(animationTicks, shield_1);
		setNewShieldHitboxPos(animationTicks + 45, shield_2);
		setNewShieldHitboxPos(animationTicks + 90, shield_3);
		setNewShieldHitboxPos(animationTicks + 135, shield_4);
		setNewShieldHitboxPos(animationTicks + 180, shield_5);
		setNewShieldHitboxPos(animationTicks + 225, shield_6);
		setNewShieldHitboxPos(animationTicks + 270, shield_7);
		setNewShieldHitboxPos(animationTicks + 315, shield_8);
	}

	protected void setNewShieldHitboxPos(int animationTicks, EntityDecayPitTargetPart shield) {
		double a = Math.toRadians(animationTicks);
		double offSetX = -Math.sin(a) * 1.5D;
		double offSetZ = Math.cos(a) * 1.5D;
		float wobble = MathHelper.cos((float) ((animationTicks) * 0.07F)) * 0.49F;
		shield.setPosition(posX + offSetX, posY + wobble , posZ + offSetZ);
		List<Entity> entities = getEntityWorld().getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(shield.posX - (double)shield.width / 2.0D, shield.posY + wobble, shield.posZ - (double)shield.width / 2.0D, shield.posX + (double)shield.width / 2.0D, shield.posY + wobble + (double)shield.height, shield.posZ + (double)shield.width / 2.0D));
		for (Entity entity : entities)
			if (entity != null)
				if (entity instanceof EntityArrow) {
					moveUp();
					entity.setDead();
				}
	}

	@Override
	public boolean canBePushed() {
		return true;
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public void addVelocity(double x, double y, double z) {
	//	motionX = 0;
	//	motionY = 0;
	//	motionZ = 0;
	}

	@Override
	public boolean getIsInvulnerable() {
		return false;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		moveDown();
		return true;
	}

	@Override
	public boolean attackEntityFromPart(EntityDecayPitTargetPart part, DamageSource source, float damage) {
	//	if(part == shield_1 || part == shield_2 || part == shield_3 || part == shield_4)
		//	moveDown();
		return false;
	}

	private void moveUp() {
		setPosition(posX, posY + 1, posZ);
	}

	private void moveDown() {
		setPosition(posX, posY - 1, posZ);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
	}

	@Override
	public World getWorld() {
		return getEntityWorld();
	}
}
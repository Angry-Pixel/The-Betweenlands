package thebetweenlands.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityDecayPitTargetPart extends Entity {
	public IEntityMultiPartPitTarget parent;
	public String partName;
	public final boolean isShield;

	public EntityDecayPitTargetPart(IEntityMultiPartPitTarget parent, String partName, float width, float height, boolean isShield) {
		super(parent.getWorld());
		setSize(width, height);
		this.parent = parent;
		this.partName = partName;
		this.isShield = isShield;
	}

	@Override
	protected void entityInit() {
	}

	@Override
	public boolean canBeCollidedWith() {
		return !this.isShield;
	}

	@Override
	public boolean canBePushed() {
		return true;
	}

	@Override
	public boolean getIsInvulnerable() {
		return false;
	}

	@Override
	public void addVelocity(double x, double y, double z) {
		motionX = 0;
		motionY = 0;
		motionZ = 0;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return getEntityBoundingBox();
	}

	@Override
	public AxisAlignedBB getCollisionBox(Entity entity) {
		return getEntityBoundingBox();
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox() {
		return getEntityBoundingBox();
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		return parent.attackEntityFromPart(this, source, amount);
	}

	@Override
	public boolean isEntityEqual(Entity entity) {
		return this == entity || parent == entity;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
	}
}

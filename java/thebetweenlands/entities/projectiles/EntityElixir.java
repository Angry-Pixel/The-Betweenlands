package thebetweenlands.entities.projectiles;

import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import thebetweenlands.items.BLItemRegistry;

public class EntityElixir extends EntityThrowable {
	private ItemStack elixirStack;

	public EntityElixir(World world) {
		super(world);
		this.getDataWatcher().addObject(20, new ItemStack(BLItemRegistry.elixir));
	}

	public EntityElixir(World world, EntityLivingBase thrower, ItemStack elixir, float strength) {
		super(world, thrower);
		this.elixirStack = elixir;
		this.getDataWatcher().addObject(20, elixir);
		this.motionX *= strength;
		this.motionY *= strength;
		this.motionZ *= strength;
	}

	public ItemStack getElixirStack() {
		return this.getDataWatcher().getWatchableObjectItemStack(20);
	}

	@Override
	protected float getGravityVelocity() {
		return 0.05F;
	}

	@Override
	protected float func_70182_d() {
		return 0.5F;
	}

	@Override
	protected float func_70183_g() {
		return -20.0F;
	}

	@Override
	protected void onImpact(MovingObjectPosition hitPosition) {
		if (!this.worldObj.isRemote) {
			AxisAlignedBB hitBB = this.boundingBox.expand(4.0D, 2.0D, 4.0D);
			List hitEntities = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, hitBB);
			if (hitEntities != null && !hitEntities.isEmpty()) {
				Iterator hitEntitiesIT = hitEntities.iterator();
				while (hitEntitiesIT.hasNext()) {
					EntityLivingBase affectedEntity = (EntityLivingBase)hitEntitiesIT.next();
					double entityDst = this.getDistanceSqToEntity(affectedEntity);
					if (entityDst < 16.0D) {
						double modifier = 1.0D - Math.sqrt(entityDst) / 4.0D;
						if (affectedEntity == hitPosition.entityHit) {
							modifier = 1.0D;
						}
						BLItemRegistry.elixir.applyEffect(this.elixirStack, affectedEntity, modifier);
					}
				}
			}
			this.worldObj.playAuxSFX(2002, (int)Math.round(this.posX), (int)Math.round(this.posY), (int)Math.round(this.posZ), 0);
			this.setDead();
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		if (nbt.hasKey("elixir")) {
			this.elixirStack = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("elixir"));
			this.getDataWatcher().updateObject(20, this.elixirStack);
		}
		if (this.elixirStack == null) {
			this.setDead();
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		if (this.elixirStack != null) {
			nbt.setTag("elixir", this.elixirStack.writeToNBT(new NBTTagCompound()));
		}
	}
}
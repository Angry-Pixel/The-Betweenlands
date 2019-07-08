package thebetweenlands.common.entity.projectiles;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.EntityRootGrabber;
import thebetweenlands.common.entity.mobs.EntitySpiritTreeFace;
import thebetweenlands.common.registries.ItemRegistry;

public class EntitySapSpit extends EntityThrowable {
	protected float damage;

	public EntitySapSpit(World worldIn) {
		super(worldIn);
	}

	public EntitySapSpit(World worldIn, EntityLivingBase throwerIn, float damage) {
		super(worldIn, throwerIn);
		this.damage = damage;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if(this.world.isRemote) {
			this.world.spawnParticle(EnumParticleTypes.ITEM_CRACK, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D, Item.getIdFromItem(ItemRegistry.SAP_SPIT));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleStatusUpdate(byte id) {
		if(id == 3) {
			for(int i = 0; i < 16; ++i) {
				this.world.spawnParticle(EnumParticleTypes.ITEM_CRACK, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D, Item.getIdFromItem(ItemRegistry.SAP_SPIT));
			}
		}
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		if(!this.world.isRemote && result.entityHit != this.thrower && result.entityHit instanceof EntitySpiritTreeFace == false && result.entityHit instanceof EntityRootGrabber == false) {
			if(result.entityHit != null) {
				result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), this.damage);
			}
			
			this.world.setEntityState(this, (byte)3);
			this.setDead();
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);

		nbt.setFloat("damage", this.damage);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);

		this.damage = nbt.getFloat("damage");
	}
}

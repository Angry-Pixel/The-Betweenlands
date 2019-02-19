package thebetweenlands.common.entity.projectiles;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Particles;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.entity.EntityRootGrabber;
import thebetweenlands.common.entity.mobs.EntitySpiritTreeFace;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class EntitySapSpit extends EntityThrowable {
	protected float damage;

	public EntitySapSpit(World worldIn) {
		super(EntityRegistry.SAP_SPIT, worldIn);
	}

	public EntitySapSpit(World worldIn, EntityLivingBase throwerIn, float damage) {
		super(EntityRegistry.SAP_SPIT, throwerIn, worldIn);
		this.damage = damage;
	}

	@Override
	public void tick() {
		super.tick();

		if(this.world.isRemote()) {
			this.world.spawnParticle(new ItemParticleData(Particles.ITEM, new ItemStack(ItemRegistry.SAP_SPIT)), this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void handleStatusUpdate(byte id) {
		if(id == 3) {
			for(int i = 0; i < 16; ++i) {
				this.world.spawnParticle(new ItemParticleData(Particles.ITEM, new ItemStack(ItemRegistry.SAP_BALL)), this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		if(!this.world.isRemote() && result.entity instanceof EntitySpiritTreeFace == false && result.entity instanceof EntityRootGrabber == false) {
			if(result.entity != null) {
				result.entity.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), this.damage);
			}
			
			this.world.setEntityState(this, (byte)3);
			this.remove();
		}
	}

	@Override
	public void writeAdditional(NBTTagCompound nbt) {
		super.writeAdditional(nbt);

		nbt.setFloat("damage", this.damage);
	}

	@Override
	public void readAdditional(NBTTagCompound nbt) {
		super.readAdditional(nbt);

		this.damage = nbt.getFloat("damage");
	}
}

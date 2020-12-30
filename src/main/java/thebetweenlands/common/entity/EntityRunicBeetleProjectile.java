package thebetweenlands.common.entity;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IThrowableEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IRuneEffectModifierEntity;
import thebetweenlands.api.runechain.io.types.IBlockTarget;
import thebetweenlands.api.runechain.io.types.StaticBlockTarget;
import thebetweenlands.api.runechain.modifier.RenderState;
import thebetweenlands.api.runechain.modifier.RuneEffectModifier;
import thebetweenlands.api.runechain.modifier.Subject;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;

public class EntityRunicBeetleProjectile extends EntityThrowable implements IThrowableEntity, IRuneEffectModifierEntity {
	private static final byte EVENT_IMPACT = 81;

	private Entity hitEntity;
	private BlockPos hitBlock;

	private RuneEffectModifier runeEffectModifier;
	private Subject runeEffectModifierSubject;

	private RenderState renderState = RenderState.none();

	private float yaw;
	
	private BlockPos lastTrailPos = null;
	private List<IBlockTarget> trail = new ArrayList<>();

	public EntityRunicBeetleProjectile(World worldIn) {
		super(worldIn);
	}

	public EntityRunicBeetleProjectile(World worldIn, EntityLivingBase throwerIn) {
		super(worldIn, throwerIn);
	}

	public EntityRunicBeetleProjectile(World worldIn, double x, double y, double z) {
		super(worldIn, x, y, z);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if(!this.world.isRemote) {
			BlockPos pos = this.getPosition();
			if(this.lastTrailPos == null || !this.lastTrailPos.equals(pos)) {
				this.lastTrailPos = pos;
				this.trail.add(new StaticBlockTarget(pos));
			}
		}
		
		if(this.motionX * this.motionX + this.motionZ * this.motionZ > 0.01f) {
			this.yaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * (180D / Math.PI));
		}

		this.rotationYaw = this.yaw;

		if(this.world.isRemote) {
			this.renderState.update();
		
			if(this.ticksExisted == 1) {
				this.spawnParticles();
			}
		}
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		if(result.entityHit != null) {
			this.hitEntity = result.entityHit;
		} else if(result.typeOfHit == RayTraceResult.Type.BLOCK) {
			this.hitBlock = result.getBlockPos();
		}

		if(!this.world.isRemote) {
			this.world.setEntityState(this, EVENT_IMPACT);
			this.setDead();
		} else {
			this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
			this.motionX = this.motionY = this.motionZ = 0;
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);

		if(id == EVENT_IMPACT) {
			this.motionX = this.motionY = this.motionZ = 0;
			this.spawnParticles();
		}
	}
	
	@SideOnly(Side.CLIENT)
	private void spawnParticles() {
		for(int i = 0; i < 10; i++) {
			ParticleArgs<?> args = ParticleArgs.get().withMotion((this.rand.nextFloat() - 0.5F) / 6.0F, (this.rand.nextFloat() - 0.5F) / 6.0F + 0.05f, (this.rand.nextFloat() - 0.5F) / 6.0F);
			args.withColor(1F, 0.25F + this.rand.nextFloat() * 0.5F, 0.05F + this.rand.nextFloat() * 0.25F, 1);
			BLParticles.WEEDWOOD_LEAF.spawn(this.world, this.posX, this.posY + this.height, this.posZ, args);
			args = ParticleArgs.get().withMotion((this.rand.nextFloat() - 0.5F) / 6.0F, (this.rand.nextFloat() - 0.5F) / 6.0F, (this.rand.nextFloat() - 0.5F) / 6.0F);
			BLParticles.SWAMP_SMOKE.spawn(this.world, this.posX, this.posY + this.height, this.posZ, args);
		}
	}

	@Override
	public void setThrower(Entity entity) {
		if(entity instanceof EntityLivingBase) {
			this.thrower = (EntityLivingBase) entity;
		}
	}

	@Nullable
	public Entity getHitEntity() {
		return this.hitEntity;
	}

	@Nullable
	public BlockPos getHitBlock() {
		return this.hitBlock;
	}

	public RenderState getRenderState() {
		return this.renderState;
	}

	@Override
	public void setRuneEffectModifier(RuneEffectModifier modifier, Subject subject) {
		this.runeEffectModifier = modifier;
		this.runeEffectModifierSubject = subject;
	}

	@Override
	public RuneEffectModifier getRuneEffectModifier() {
		return this.runeEffectModifier;
	}

	@Override
	public Subject getRuneEffectModifierSubject() {
		return this.runeEffectModifierSubject;
	}

	@Override
	public void clearRuneEffectModifier() {
		this.runeEffectModifier = null;
		this.runeEffectModifierSubject = null;
	}
	
	public List<IBlockTarget> getTrail() {
		return this.trail;
	}
}
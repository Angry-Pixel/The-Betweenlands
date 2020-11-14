package thebetweenlands.common.entity;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IThrowableEntity;
import thebetweenlands.api.rune.impl.RuneEffectModifier;
import thebetweenlands.api.rune.impl.RuneEffectModifier.RenderState;
import thebetweenlands.api.rune.impl.RuneEffectModifier.Subject;

public class EntityRunicBeetleProjectile extends EntityThrowable implements IThrowableEntity {
	private Entity hitEntity;
	private BlockPos hitBlock;

	private Pair<RuneEffectModifier, Subject> visualModifier;
	
	private RenderState renderState = RenderState.none();

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

		if(this.world.isRemote) {
			this.renderState.update();
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
			this.setDead();
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

	public void setVisualModifier(@Nullable Pair<RuneEffectModifier, Subject> visualModifier) {
		this.visualModifier = visualModifier;
	}

	@Nullable
	public Pair<RuneEffectModifier, Subject> getVisualModifier() {
		return this.visualModifier;
	}
	
	public RenderState getRenderState() {
		return this.renderState;
	}
}
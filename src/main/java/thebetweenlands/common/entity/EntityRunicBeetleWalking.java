package thebetweenlands.common.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IRuneEffectModifierEntity;
import thebetweenlands.api.rune.IBlockTarget;
import thebetweenlands.api.rune.IVectorTarget;
import thebetweenlands.api.rune.impl.RuneEffectModifier;
import thebetweenlands.api.rune.impl.RuneEffectModifier.RenderState;
import thebetweenlands.api.rune.impl.RuneEffectModifier.Subject;
import thebetweenlands.api.rune.impl.StaticBlockTarget;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.entity.ai.puppet.EntityAIGoTo;

public class EntityRunicBeetleWalking extends EntityCreature implements IRuneEffectModifierEntity {
	private static final byte EVENT_IMPACT = 81;

	private EntityAIGoTo aiGoTo;

	private Entity hitEntity;
	private BlockPos hitBlock;

	private RuneEffectModifier runeEffectModifier;
	private Subject runeEffectModifierSubject;

	private RenderState renderState = RenderState.none();

	private BlockPos lastTrailPos = null;
	private List<IBlockTarget> trail = new ArrayList<>();

	private IVectorTarget target;

	public EntityRunicBeetleWalking(World worldIn) {
		super(worldIn);
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(0, this.aiGoTo = new EntityAIGoTo(this, 1));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
	}
	
	public void setTarget(@Nullable IVectorTarget target) {
		this.target = target;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if(!this.world.isRemote) {
			BlockPos targetPos = this.target != null ? new BlockPos(this.target.x(), this.target.y(), this.target.z()) : null;
			if(!Objects.equals(targetPos, this.aiGoTo.getTarget())) {
				this.aiGoTo.setTarget(targetPos);
			}

			BlockPos pos = this.getPosition();

			if(this.lastTrailPos == null || !this.lastTrailPos.equals(pos)) {
				this.lastTrailPos = pos;
				this.trail.add(new StaticBlockTarget(pos));
			}
		}

		if(this.world.isRemote) {
			this.renderState.update();

			if(this.ticksExisted == 1) {
				this.spawnParticles();
			}
		} else {
			if(this.ticksExisted > 60) {
				this.setDead();
			}

			//if(result.entityHit != null) {
			//	this.hitEntity = result.entityHit;
			//} else if(result.typeOfHit == RayTraceResult.Type.BLOCK) {
			//	this.hitBlock = result.getBlockPos();
			//}
			//
			//if(!this.world.isRemote) {
			//	this.world.setEntityState(this, EVENT_IMPACT);
			//	this.setDead();
			//} else {
			//	this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
			//	this.motionX = this.motionY = this.motionZ = 0;
			//}
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
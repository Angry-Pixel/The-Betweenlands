package thebetweenlands.common.entity.mobs;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.entity.ai.EntityAIAttackOnCollide;

public class EntitySwarm extends EntityClimberBase implements IMob {

	public EntitySwarm(World world) {
		super(world);
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAIAttackMelee(this, 1.0D, false));
		this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, 1, false, false, null));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();

		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);

		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(0.0D /*TODO Debug*/);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.185D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40.0D);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(24.0D);
		this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		return EntityAIAttackOnCollide.useStandardAttack(this, entityIn);
	}

	@Override
	public boolean getCanSpawnHere() {
		return this.world.getDifficulty() != EnumDifficulty.PEACEFUL && super.getCanSpawnHere();
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		List<AxisAlignedBB> collisionBoxes = new ArrayList<>();

		for(BlockPos offsetPos : BlockPos.getAllInBoxMutable(
				new BlockPos(MathHelper.floor(this.posX - this.width - 0.1f), MathHelper.floor(this.posY - 0.1f), MathHelper.floor(this.posZ - this.width - 0.1f)),
				new BlockPos(MathHelper.floor(this.posX + this.width + 0.1f), MathHelper.floor(this.posY + this.height + 0.1f), MathHelper.floor(this.posZ + this.width + 0.1f)))) {
			IBlockState state = this.world.getBlockState(offsetPos);

			if(state.isFullCube()) {
				collisionBoxes.add(new AxisAlignedBB(offsetPos));
			}
		}

		for(int i = 0; i < 2; i++) {
			float rx = (this.world.rand.nextFloat() - 0.5f) * this.width;
			float ry = (this.world.rand.nextFloat() - 0.5f) * this.height;
			float rz = (this.world.rand.nextFloat() - 0.5f) * this.width;

			float len = MathHelper.sqrt(rx * rx + ry * ry + rz * rz);

			rx /= len;
			ry /= len;
			rz /= len;

			len = 0.333f + this.world.rand.nextFloat() * 0.666f;

			double x = this.posX + rx * len * (this.width + 0.2f) * 0.5f;
			double y = this.posY + this.height * 0.5f + ry * len * this.height;
			double z = this.posZ + rz * len * (this.width + 0.2f) * 0.5f;

			if(this.rand.nextInt(4) == 0) {
				BLParticles.FLY.spawn(world, x, y, z, ParticleArgs.get().withScale(0.15F * world.rand.nextFloat() + 0.25F).withData(40, 0.01F, 0.0025F, false));
			} else {
				AxisAlignedBB particle = new AxisAlignedBB(x - 0.01f, y - 0.01f, z - 0.01f, x + 0.01f, y + 0.01f, z + 0.01f);

				double closestDst = 1;
				double closestDX = 0;
				double closestDY = 0;
				double closestDZ = 0;

				for(AxisAlignedBB box : collisionBoxes) {
					double dx1 = box.calculateXOffset(particle, -1);
					double dy1 = box.calculateYOffset(particle, -1);
					double dz1 = box.calculateZOffset(particle, -1);
					double dx2 = box.calculateXOffset(particle, 1);
					double dy2 = box.calculateYOffset(particle, 1);
					double dz2 = box.calculateZOffset(particle, 1);

					if(Math.abs(dx1) < closestDst) {
						closestDst = Math.abs(dx1);
						closestDX = dx1;
						closestDY = 0;
						closestDZ = 0;
					}

					if(Math.abs(dy1) < closestDst) {
						closestDst = Math.abs(dy1);
						closestDX = 0;
						closestDY = dy1;
						closestDZ = 0;
					}

					if(Math.abs(dz1) < closestDst) {
						closestDst = Math.abs(dz1);
						closestDX = 0;
						closestDY = 0;
						closestDZ = dz1;
					}

					if(Math.abs(dx2) < closestDst) {
						closestDst = Math.abs(dx2);
						closestDX = dx2;
						closestDY = 0;
						closestDZ = 0;
					}

					if(Math.abs(dy2) < closestDst) {
						closestDst = Math.abs(dy2);
						closestDX = 0;
						closestDY = dy2;
						closestDZ = 0;
					}

					if(Math.abs(dz2) < closestDst) {
						closestDst = Math.abs(dz2);
						closestDX = 0;
						closestDY = 0;
						closestDZ = dz2;
					}
				}

				if(closestDst < 1) {
					x += closestDX - Math.signum(closestDX) * 0.01f;
					y += closestDY - Math.signum(closestDY) * 0.01f;
					z += closestDZ - Math.signum(closestDZ) * 0.01f;

					double ox = 1 - Math.abs(Math.signum(closestDX));
					double oy = 1 - Math.abs(Math.signum(closestDY));
					double oz = 1 - Math.abs(Math.signum(closestDZ));

					//TODO Debug
					BLParticles.SWARM.spawn(this.world, x, y, z, 
							ParticleArgs.get()
							.withMotion((this.world.rand.nextFloat() - 0.5f) * 0.05f * ox, (this.world.rand.nextFloat() - 0.5f) * 0.05f * oy, (this.world.rand.nextFloat() - 0.5f) * 0.05f * oz)
							.withScale(0.25f)
							.withData(EnumFacing.getFacingFromVector((float) -closestDX, (float) -closestDY, (float) -closestDZ), 40, this.getPositionVector(), (Supplier<Vec3d>) () -> this.getPositionVector()));
				}
			}
		}
	}

}

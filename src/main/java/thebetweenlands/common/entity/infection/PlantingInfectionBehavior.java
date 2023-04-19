package thebetweenlands.common.entity.infection;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IInfectionBehaviorOverlay;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.network.datamanager.GenericDataManager;
import thebetweenlands.common.network.serverbound.MessageInfectionPlantBlock;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;

public class PlantingInfectionBehavior extends AbstractInfectionBehavior implements IInfectionBehaviorOverlay {

	private static final DataParameter<BlockPos> TARGET_POS = GenericDataManager.createKey(PlantingInfectionBehavior.class, DataSerializers.BLOCK_POS);

	protected int lookingTicks;

	private Vec3d targetLookDir;
	private Vec3d prevLookDir;
	private Vec3d originalLookDir;

	private float rotationYaw;
	private float rotationPitch;

	private float prevRotationYaw;
	private float prevRotationPitch;

	private boolean firstApply = true;

	private boolean hasPlanted = false;

	private int plantingTimeout;

	private int obstructedTicks;

	private int maxLookingTicks = 200;

	public PlantingInfectionBehavior(EntityLivingBase entity) {
		super(entity);
		this.dataManager.register(TARGET_POS, BlockPos.ORIGIN);
	}

	public BlockPos getTargetPos() {
		return this.dataManager.get(TARGET_POS);
	}

	protected void setTargetPos(BlockPos pos) {
		this.dataManager.set(TARGET_POS, pos);
	}

	@Override
	public boolean onParameterChange(DataParameter<?> key, Object value, boolean fromPacket) {
		if(key == TARGET_POS && this.world.isRemote) {
			this.lookingTicks = 0;
		}
		return false;
	}

	@Override
	public float getOverlayPercentage() {
		return 1.0f;
	}

	@Override
	public void start() {
		if(!this.world.isRemote) {
			BlockPos newTarget = this.findTargetPos();
			this.setTargetPos(newTarget != null ? newTarget : BlockPos.ORIGIN);
		}

		super.start();
	}

	@Override
	public void update() {
		if(!this.world.isRemote) {
			this.checkTargetValid();

			if(this.isLooking()) {
				this.entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 1, 3, true, false));
			}
		}

		BlockPos target = this.getTargetPos();

		if(this.isLooking()) {
			this.applyLookDirection(target);
		}

		if(!this.world.isRemote || !this.firstApply) {
			this.lookingTicks++;

			if(this.lookingTicks >= this.maxLookingTicks - 10 && this.tryPlaceBlock(this.entity.getPosition())) {
				this.hasPlanted = true;
			}
		}

		if(this.plantingTimeout > 0) {
			--this.plantingTimeout;
		}

		if(this.world.isRemote && this.lookingTicks > 40 && this.isLooking() && this.plantingTimeout == 0) {
			Vec3d startVec = this.entity.getPositionEyes(1.0f);
			Vec3d endVec = startVec.add(this.entity.getLookVec().scale(5.0D));

			RayTraceResult result = this.world.rayTraceBlocks(startVec, endVec, true);

			if(result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
				BlockPos hitPos = result.getBlockPos();

				if(hitPos != null && this.getTargetPos().equals(hitPos.up()) && result.sideHit == EnumFacing.UP && result.hitVec != null && Math.abs(result.hitVec.x - hitPos.getX() - 0.5D) < 0.25D && Math.abs(result.hitVec.z - hitPos.getZ() - 0.5D) < 0.35D) {
					if(this.tryPlaceBlock(hitPos.up())) {
						this.spawnPlantingParticles(hitPos.up());
						TheBetweenlands.networkWrapper.sendToServer(new MessageInfectionPlantBlock(hitPos.up()));
						this.plantingTimeout = 20;
					}
				}
			}
		}

		super.update();
	}

	@SideOnly(Side.CLIENT)
	protected void spawnPlantingParticles(BlockPos pos) {
		for(int i = 0; i < 16; ++i) {
			BLParticles particle;
			switch(this.world.rand.nextInt(4)) {
			default:
			case 0:
				particle = BLParticles.MOULD_HORN_1;
				break;
			case 1:
				particle = BLParticles.MOULD_HORN_2;
				break;
			case 2:
				particle = BLParticles.MOULD_HORN_3;
				break;
			case 3:
				particle = BLParticles.MOULD_HORN_4;
				break;
			}
			float dx = this.world.rand.nextFloat() * 0.4f - 0.2f;
			float dz = this.world.rand.nextFloat() * 0.4f - 0.2f;
			particle.spawn(this.world,
					pos.getX() + 0.5D + dx, pos.getY() - 0.1f + this.world.rand.nextFloat() * 0.5f, pos.getZ() + 0.5D + dz,
					ParticleArgs.get().withMotion(dx * 0.25D, 0.025D, dz * 0.25D).withData(-1, false, 5));
		}

		for(int i = 0; i < 10; ++i) {
			Random rand = this.world.rand;
			float size = rand.nextFloat();
			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_NEAREST_NEIGHBOR, BLParticles.SMOOTH_SMOKE.create(this.world,
					pos.getX() + rand.nextFloat(), pos.getY(), pos.getZ() + rand.nextFloat(), 
					ParticleArgs.get()
					.withMotion((rand.nextFloat() - 0.5f) * 0.04f, rand.nextFloat() * 0.02f, (rand.nextFloat() - 0.5f) * 0.04f)
					.withScale(2f + size * 10.0F)
					.withColor(0.8F, 0.6F, 0.3F, (1 - size) * 0.25f + 0.25f)
					.withData(80, true, 0.01F, true)));
		}
	}

	public void handleClientPlanting(BlockPos pos) {
		if(this.getTargetPos().equals(pos) && this.tryPlaceBlock(pos)) {
			this.hasPlanted = true;
		}
	}

	public boolean tryPlaceBlock(BlockPos pos) {
		if(!this.hasPlanted) {
			IBlockState state = BlockRegistry.MOULD_HORN.getDefaultState();

			if(this.entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) this.entity;

				if(!player.canPlayerEdit(pos, EnumFacing.UP, new ItemStack(Item.getItemFromBlock(state.getBlock())))
						|| !this.world.mayPlace(state.getBlock(), pos, false, EnumFacing.UP, player)) {
					this.hasPlanted = true;
					return false;
				}
			} else if(!ForgeEventFactory.getMobGriefingEvent(this.world, this.entity)) {
				this.hasPlanted = true;
				return false;
			}

			if(this.placeBlockAt(this.entity, this.world, pos, EnumFacing.UP, state)) {
				state = this.world.getBlockState(pos);

				SoundType soundtype = state.getBlock().getSoundType(state, this.world, pos, this.entity);
				this.world.playSound(this.entity instanceof EntityPlayer ? (EntityPlayer) this.entity : null, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);

				this.entity.swingArm(EnumHand.MAIN_HAND);

				return true;
			}
		}

		return false;
	}

	public boolean placeBlockAt(EntityLivingBase placer, World world, BlockPos pos, EnumFacing side, IBlockState newState) {
		if(!world.setBlockState(pos, newState, 11)) {
			return false;
		}

		IBlockState state = world.getBlockState(pos);
		if(state.getBlock() == newState.getBlock()) {
			newState.getBlock().onBlockPlacedBy(world, pos, state, placer, ItemStack.EMPTY);
		}

		return true;
	}

	@Override
	public boolean isDone() {
		return !this.isLooking() || this.lookingTicks > this.maxLookingTicks || this.hasPlanted;
	}

	protected boolean isLooking() {
		return !BlockPos.ORIGIN.equals(this.getTargetPos()) && !this.hasPlanted;
	}

	protected void checkTargetValid() {
		BlockPos target = this.getTargetPos();

		if(!BlockPos.ORIGIN.equals(target)) {
			if(!this.isValidPlantingSpot(target)) {
				this.setTargetPos(BlockPos.ORIGIN);
			} else {
				boolean isValid = true;

				if(target.distanceSq(this.entity.getPosition()) > 36) {
					isValid = false;
				}

				if(this.world.rayTraceBlocks(new Vec3d(this.entity.posX, this.entity.posY + (double)this.entity.getEyeHeight(), this.entity.posZ), new Vec3d(target.getX() + 0.5D, target.getY() + 0.01D, target.getZ() + 0.5D), true) != null) {
					++this.obstructedTicks;

					if(this.obstructedTicks > 20) {
						isValid = false;
					}
				} else {
					this.obstructedTicks = 0;
				}

				if(!isValid) {
					BlockPos newTarget = this.findTargetPos();
					this.setTargetPos(newTarget != null ? newTarget : BlockPos.ORIGIN);
				}
			}
		}
	}

	@Nullable
	protected BlockPos findTargetPos() {
		BlockPos center = this.entity.getPosition();

		MutableBlockPos checkPos = new MutableBlockPos();

		Random rng = this.world.rand;

		for(int i = 0; i < 64; ++i) {
			checkPos.setPos(center.getX() + rng.nextInt(7) - 3, center.getY() + rng.nextInt(5) - 2, center.getZ() + rng.nextInt(7) - 3);

			if(!center.equals(checkPos)
					&& this.isValidPlantingSpot(checkPos)
					&& this.world.rayTraceBlocks(new Vec3d(this.entity.posX, this.entity.posY + (double)this.entity.getEyeHeight(), this.entity.posZ), new Vec3d(checkPos.getX() + 0.5D, checkPos.getY() + 0.01D, checkPos.getZ() + 0.5D), true) == null) {
				return checkPos.toImmutable();
			}
		}

		return null;
	}

	protected boolean isValidPlantingSpot(BlockPos pos) {
		return this.world.getBlockState(pos).getBlock().isReplaceable(this.world, pos) && SurfaceType.MIXED_GROUND.apply(this.world.getBlockState(pos.down()));
	}

	protected void applyLookDirection(BlockPos target) {
		if(!this.world.isRemote && this.firstApply) {
			this.originalLookDir = this.entity.getLookVec();

			this.firstApply = false;
		}

		if(this.originalLookDir != null) {
			this.prevRotationYaw = this.rotationYaw;
			this.prevRotationPitch = this.rotationPitch;

			double dx = target.getX() + 0.5D - this.entity.posX;
			double dy = target.getY() - (this.entity.posY + (double)this.entity.getEyeHeight());
			double dz = target.getZ() + 0.5D - this.entity.posZ;

			Vec3d newLookDir = this.targetLookDir = new Vec3d(dx, dy, dz);

			float p = MathHelper.clamp(this.lookingTicks / 40.0f, 0.0f, 1.0f);

			float s = MathHelper.clamp(this.lookingTicks / 100.0f, 0.0f, 1.0f);

			newLookDir = this.originalLookDir.add(newLookDir.subtract(this.originalLookDir).scale(p)).normalize();

			if(this.prevLookDir != null) {
				newLookDir = this.prevLookDir.add(newLookDir.subtract(this.prevLookDir).scale((0.5f + s * 4.5f) * 0.2f)).normalize();
			}

			double d = (double)MathHelper.sqrt(newLookDir.x * newLookDir.x + newLookDir.z * newLookDir.z);
			float yaw = (float)(MathHelper.atan2(newLookDir.z, newLookDir.x) * (180D / Math.PI)) - 90.0F;
			float pitch = (float)(-(MathHelper.atan2(newLookDir.y, d) * (180D / Math.PI)));

			this.rotationYaw = this.entity.rotationYawHead = this.updateRotation(this.rotationYaw, yaw, Math.abs(MathHelper.wrapDegrees(yaw - this.rotationYaw) / 180.0f) * (1.0f + s * 4.0f) * 20.0f);
			this.rotationPitch = this.entity.rotationPitch = this.updateRotation(this.rotationPitch, pitch, Math.abs(MathHelper.wrapDegrees(pitch - this.rotationPitch) / 180.0f) * (1.0f + s * 5.0f) * 20.0f);

			this.prevLookDir = newLookDir;
		}
	}

	private float updateRotation(float current, float target, float rate) {
		float offset = MathHelper.wrapDegrees(target - current);

		if (offset > rate) {
			offset = rate;
		}

		if (offset < -rate) {
			offset = -rate;
		}

		return current + offset;
	}

	protected void applyLookDirectionFirstPerson(BlockPos target, float partialTicks) {
		if(this.firstApply) {
			this.originalLookDir = this.entity.getLook(partialTicks);

			this.rotationYaw = this.entity.rotationYaw;
			this.rotationPitch = this.entity.rotationPitch;

			this.prevRotationYaw = this.entity.prevRotationYaw;
			this.prevRotationPitch = this.entity.prevRotationPitch;

			this.firstApply = false;
		} else {
			this.entity.rotationYaw = this.rotationYaw;
			this.entity.rotationPitch = this.rotationPitch;

			this.entity.prevRotationYaw = this.prevRotationYaw;
			this.entity.prevRotationPitch = this.prevRotationPitch;
		}
	}

	// Last event before rendering view
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onFogColors(FogColors event) {
		getInfectionBehavior(Minecraft.getMinecraft().player, PlantingInfectionBehavior.class).ifPresent(behavior -> {
			BlockPos target = behavior.getTargetPos();

			if(behavior.isLooking()) {
				behavior.applyLookDirectionFirstPerson(target, (float)event.getRenderPartialTicks());
			}
		});
	}

	@SubscribeEvent
	public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		getInfectionBehavior(entity, PlantingInfectionBehavior.class).ifPresent(behavior -> {
			if(behavior.isLooking() && entity.motionY > 0) {
				entity.motionY *= 0.5f;
			}
		});
	}

}

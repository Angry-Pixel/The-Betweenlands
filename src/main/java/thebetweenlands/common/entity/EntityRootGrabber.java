package thebetweenlands.common.entity;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.SpikeRenderer;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.render.particle.entity.ParticleRootSpike;
import thebetweenlands.client.render.tile.RenderDecayPitHangingChain;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityRootGrabber extends Entity implements IEntityAdditionalSpawnData {
	public static final DataParameter<Float> DAMAGE = EntityDataManager.createKey(EntityRootGrabber.class, DataSerializers.FLOAT);
	public static final DataParameter<Boolean> RETRACT = EntityDataManager.createKey(EntityRootGrabber.class, DataSerializers.BOOLEAN);

	public static final byte EVENT_BROKEN = 40;
	public static final byte EVENT_HIT = 41;

	protected BlockPos origin;
	protected int delay;

	protected int maxAge = 12 * 20;

	protected int prevAttackTicks = 0;
	protected int attackTicks = 0;

	protected int prevRetractTicks = 0;
	protected int retractTicks = 0;

	protected boolean emergeSound = true;
	protected boolean retractSound = true;

	@Nullable
	protected EntityLivingBase grabbedEntity = null;

	@Nullable
	@SideOnly(Side.CLIENT)
	public List<RootPart> modelParts;

	@SideOnly(Side.CLIENT)
	public static class RootPart {
		public float x, y, z;
		public float yaw, pitch;
		public float texWidth, texHeight;
		public float texU, texV;
		public ResourceLocation texture;
		
		public void render() {
			
		}
	}
	
	private boolean isChains = false;

	public EntityRootGrabber(World world, boolean isGears) {
		super(world);
		this.setSize(2, 2);
		this.noClip = true;
		this.isChains = isGears;
	}
	
	public EntityRootGrabber(World world) {
		this(world, false);
	}

	@Override
	protected void entityInit() {
		this.dataManager.register(DAMAGE, 0.0F);
		this.dataManager.register(RETRACT, false);
	}

	public float getDamage() {
		return this.dataManager.get(DAMAGE);
	}

	public void setDamage(float damage) {
		this.dataManager.set(DAMAGE, damage);

		if(damage >= 1.0F && !this.world.isRemote) {
			this.setDead();
			this.world.setEntityState(this, EVENT_BROKEN);
		}
	}

	public void setPosition(BlockPos pos, int delay) {
		this.origin = pos;
		this.setPosition(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
		this.lastTickPosX = this.prevPosX = this.posX;
		this.lastTickPosY = this.prevPosY = this.posY;
		this.lastTickPosZ = this.prevPosZ = this.posZ;

		this.delay = delay;
	}

	@SideOnly(Side.CLIENT)
	public void initRootModels() {
		if(this.modelParts == null) {
			this.modelParts = new ArrayList<>();

			int rings = 2 + this.world.rand.nextInt(2);

			for(int j = 0; j < rings; j++) {
				float radius = (this.width - 0.5F) / rings * j;
				int roots = this.isChains ? (2 + this.world.rand.nextInt(3)) : (5 + this.world.rand.nextInt(5));

				for(int i = 0; i < roots; i++) {
					float scale = 0.6F + this.rand.nextFloat() * 0.2F;
					double angle = i * Math.PI * 2 / roots;
					
					Vec3d offset = new Vec3d(Math.cos(angle) * radius, 0, Math.sin(angle) * radius);
					
					RootPart part;
					
					if(!this.isChains) {
						final SpikeRenderer renderer = new SpikeRenderer(3, scale * 0.5F, scale, 1, this.rand.nextLong(), -scale * 0.5F * 1.5F, 0, -scale * 0.5F * 1.5F).build(DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL, Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(ParticleRootSpike.SPRITE.toString()));
						
						part = new RootPart() {
							@Override
							public void render() {
								renderer.render();
							}
						};

						part.texture = TextureMap.LOCATION_BLOCKS_TEXTURE;
						
						part.texWidth = renderer.getSprite().getMaxU() - renderer.getSprite().getMinU();
						part.texU = renderer.getSprite().getMinU();
						part.texHeight = renderer.getSprite().getMaxV() - renderer.getSprite().getMinV();
						part.texV = renderer.getSprite().getMinV();
						
						part.pitch = 30.0F;
					} else {
						final float[] yaws = new float[5];
						final float[] pitches = new float[5];
						
						for(int k = 0; k < 5; k++) {
							yaws[k] = (this.rand.nextFloat() - 0.5F) * 360.0F;
							pitches[k] = (this.rand.nextFloat() - 0.5F) * 40.0F;
						}
						
						part = new RootPart() {
							@Override
							public void render() {
								final float scale = 0.3F;
								
								GlStateManager.pushMatrix();
								GlStateManager.scale(scale, scale, scale);
								
								for(int w = 0; w < 5; w++) {
									GlStateManager.translate(0, 1, 0);
									GlStateManager.rotate(yaws[w], 0, 1, 0);
									GlStateManager.rotate(pitches[w], 1, 0, 0);
									GlStateManager.translate(0, -1, 0);
									
									RenderDecayPitHangingChain.CHAIN_MODEL.render(0.0625F);
									
									GlStateManager.translate(0, 1, 0);
								}
								
								GlStateManager.popMatrix();
							}
						};
						
						part.texture = RenderDecayPitHangingChain.CHAIN_TEXTURE;
						
						part.texWidth = 1;
						part.texU = 0;
						part.texHeight = 1;
						part.texV = 0;
						
						part.pitch = 15.0F;
					}
					
					part.x = (float)offset.x;
					part.y = (float)offset.y;
					part.z = (float)offset.z;
					part.yaw = -(float)Math.toDegrees(angle);
					
					this.modelParts.add(part);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getBrightnessForRender() {
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(MathHelper.floor(this.posX), 0, MathHelper.floor(this.posZ));

		if (this.world.isBlockLoaded(pos)) {
			pos.setY(MathHelper.floor(this.posY + (double)this.getEyeHeight()));
			return this.world.getCombinedLight(pos, 0);
		} else {
			return 0;
		}
	}

	public float getRootYOffset(float partialTicks) {
		float attackTicks = this.prevAttackTicks + (this.attackTicks - this.prevAttackTicks) * partialTicks;
		float retractTicks = this.prevRetractTicks + (this.retractTicks - this.prevRetractTicks) * partialTicks;

		float y;
		if(attackTicks < 5) {
			y = -2.5F + attackTicks / 5.0F;
		} else if(attackTicks >= this.delay) {
			y = -1.5F + Math.min(1.25F, (attackTicks - this.delay) / 0.5F);
		} else {
			y = -1.5F;
		}
		y = Math.max(-2.5F, y - retractTicks / 5.0F * 2.5F);
		return y;
	}

	@Override
	public void onUpdate() {
		this.world.profiler.startSection("entityBaseTick");

		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.motionX = 0;
		this.motionZ = 0;
		this.lastTickPosX = this.posX;
		this.lastTickPosY = this.posY;
		this.lastTickPosZ = this.posZ;

		this.prevAttackTicks = this.attackTicks;
		this.prevRetractTicks = this.retractTicks;

		if(this.attackTicks >= this.delay) {
			if(this.attackTicks == this.delay) {
				if(!this.world.isRemote) {
					List<EntityLivingBase> targets = this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox(), e -> !e.getIsInvulnerable() && (e instanceof EntityPlayer == false || (!((EntityPlayer)e).isSpectator() && !((EntityPlayer)e).isCreative())));
					if(!targets.isEmpty()) {
						this.grabbedEntity = targets.get(this.rand.nextInt(targets.size()));
						this.grabbedEntity.setLocationAndAngles(this.posX, this.posY + 1, this.posZ, this.grabbedEntity.rotationYaw, this.grabbedEntity.rotationPitch);
						this.grabbedEntity.motionX = 0;
						this.grabbedEntity.motionZ = 0;
						this.grabbedEntity.velocityChanged = true;
						if(this.grabbedEntity instanceof EntityPlayerMP) {
							((EntityPlayerMP)this.grabbedEntity).connection.setPlayerLocation(this.grabbedEntity.posX, this.grabbedEntity.posY, this.grabbedEntity.posZ, this.grabbedEntity.rotationYaw, this.grabbedEntity.rotationPitch);
						}
					}
				} else {
					this.spawnExtendParticles();
					this.world.playSound(this.posX, this.posY, this.posZ, SoundRegistry.SPIRIT_TREE_SPIKE_TRAP, SoundCategory.HOSTILE, 1, 0.9F + this.rand.nextFloat() * 0.2F, false);
				}
			}

			if(!this.world.isRemote && this.grabbedEntity != null && !this.dataManager.get(RETRACT)) {
				if(this.getEntityBoundingBox().intersects(this.grabbedEntity.getEntityBoundingBox())) {
					this.grabbedEntity.addPotionEffect(new PotionEffect(ElixirEffectRegistry.ROOT_BOUND, 5, 0, true, false));
				} else {
					this.grabbedEntity = null;
				}
			}

			if(!this.world.isRemote) {
				if(this.grabbedEntity != null) {
					if(this.attackTicks >= this.delay + this.maxAge) {
						this.dataManager.set(RETRACT, true);
					}
				} else {
					if(this.attackTicks >= this.delay + 20) {
						this.dataManager.set(RETRACT, true);
					}
				}
			}

			if(this.dataManager.get(RETRACT)) {
				this.retractTicks++;

				if(!this.world.isRemote && this.getRootYOffset(1) <= -2.4F) {
					this.setDead();
				}
			}
		}

		boolean retracting = this.dataManager.get(RETRACT);

		if(this.world.isRemote && (this.attackTicks <= 5 || retracting)) {
			this.spawnBlockDust();
			if(this.emergeSound && !retracting) {
				this.emergeSound = false;
				this.world.playSound(this.posX, this.posY, this.posZ, SoundRegistry.SPIRIT_TREE_SPIKE_TRAP_EMERGE, SoundCategory.HOSTILE, 1, 0.9F + this.rand.nextFloat() * 0.2F, false);
			}
			if(this.retractSound && retracting) {
				this.retractSound = false;
				this.world.playSound(this.posX, this.posY, this.posZ, SoundRegistry.SPIRIT_TREE_SPIKE_TRAP_EMERGE, SoundCategory.HOSTILE, 1, 0.9F + this.rand.nextFloat() * 0.2F, false);
			}
		}

		this.attackTicks++;

		this.firstUpdate = false;
		this.world.profiler.endSection();
	}

	@SideOnly(Side.CLIENT)
	protected void spawnExtendParticles() {
		if(!this.isChains) {
			for(int i = 0; i < 64; i++) {
				double dx = (this.rand.nextDouble() * 2 - 1) * this.width / 2;
				double dy = this.height / 2.0D - 0.5D;
				double dz = (this.rand.nextDouble() * 2 - 1) * this.width / 2;
				double mx = (this.rand.nextDouble() - 0.5D) * 0.15D;
				double my = (this.rand.nextDouble() - 0.5D) * 0.15D + 0.3D;
				double mz = (this.rand.nextDouble() - 0.5D) * 0.15D;
				BlockPos pos = new BlockPos(this.posX + dx, MathHelper.floor(this.posY + dy), this.posZ + dz);
				IBlockState state = this.world.getBlockState(pos);
				if(!state.getBlock().isAir(state, this.world, pos)) {
					this.world.spawnParticle(EnumParticleTypes.BLOCK_DUST, this.posX + dx, MathHelper.floor(this.posY + dy) + 1 + this.rand.nextDouble() * 0.5D, this.posZ + dz, mx, my, mz, Block.getStateId(state));
				}
			}
	
			for(int i = 0; i < 8; i++) {
				double dx = (this.rand.nextDouble() * 2 - 1) * this.width / 2;
				double dy = this.height / 2.0D - 0.5D;
				double dz = (this.rand.nextDouble() * 2 - 1) * this.width / 2;
				double mx = (this.rand.nextDouble() - 0.5D) * 0.2D;
				double my = (this.rand.nextDouble() - 0.5D) * 0.2D + 0.4D;
				double mz = (this.rand.nextDouble() - 0.5D) * 0.2D;
				ParticleRootSpike particle = (ParticleRootSpike) BLParticles.ROOT_SPIKE.spawn(this.world, this.posX + dx, this.posY + dy, this.posZ + dz, ParticleArgs.get().withMotion(mx, my, mz).withScale(0.4F));
				particle.setUseSound(this.rand.nextInt(3) == 0);
			}
		} else {
			this.spawnBlockDust();
		}
	}

	@SideOnly(Side.CLIENT)
	protected void spawnBlockDust() {
		for(int i = 0; i < 8; i++) {
			double dx = (this.rand.nextDouble() * 2 - 1) * this.width / 2;
			double dy = this.height / 2.0D - 0.5D;
			double dz = (this.rand.nextDouble() * 2 - 1) * this.width / 2;
			double mx = (this.rand.nextDouble() - 0.5D) * 0.15D;
			double my = (this.rand.nextDouble() - 0.5D) * 0.15D;
			double mz = (this.rand.nextDouble() - 0.5D) * 0.15D;
			BlockPos pos = new BlockPos(this.posX + dx, MathHelper.floor(this.posY + dy), this.posZ + dz);
			IBlockState state = this.world.getBlockState(pos);
			if(!state.getBlock().isAir(state, this.world, pos)) {
				this.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, this.posX + dx, MathHelper.floor(this.posY + dy) + 1, this.posZ + dz, mx, my, mz, Block.getStateId(state));
			}
		}
	}
	
	public boolean isChains() {
		return this.isChains;
	}

	@Override
	public boolean handleWaterMovement() {
		return false;
	}

	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public boolean hitByEntity(Entity entity) {
		if(!this.world.isRemote) {
			if(entity instanceof EntityPlayer && ((EntityPlayer)entity).isCreative()) {
				this.setDamage(1.0F);
			} else {
				this.setDamage(this.getDamage() + 0.05F);
			}
			this.world.setEntityState(this, EVENT_HIT);
		}
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);

		if(id == EVENT_BROKEN) {
			for(int i = 0; i < 128; i++) {
				double dx = (this.rand.nextDouble() * 2 - 1) * this.width / 2.2F;
				double dy = (this.rand.nextDouble() * 2 - 1) * this.height / 1.2F + this.height / 2;
				double dz = (this.rand.nextDouble() * 2 - 1) * this.width / 2.2F;
				double mx = (this.rand.nextDouble() - 0.5D) * 0.15D;
				double my = (this.rand.nextDouble() - 0.5D) * 0.15D;
				double mz = (this.rand.nextDouble() - 0.5D) * 0.15D;
				this.world.spawnParticle(EnumParticleTypes.BLOCK_DUST, this.posX + dx, this.posY + dy, this.posZ + dz, mx, my, mz, Block.getStateId(BlockRegistry.LOG_SPIRIT_TREE.getDefaultState()));
			}

			SoundType soundType = SoundType.WOOD;
			this.world.playSound(this.posX, this.posY, this.posZ, soundType.getBreakSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F, false);
		} else if(id == EVENT_HIT) {
			for(int i = 0; i < 8; i++) {
				double dx = (this.rand.nextDouble() * 2 - 1) * this.width / 4;
				double dy = (this.rand.nextDouble() * 2 - 1) * this.height / 2 + this.height / 2;
				double dz = (this.rand.nextDouble() * 2 - 1) * this.width / 4;
				double mx = (this.rand.nextDouble() - 0.5D) * 0.15D;
				double my = (this.rand.nextDouble() - 0.5D) * 0.15D;
				double mz = (this.rand.nextDouble() - 0.5D) * 0.15D;
				this.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, this.posX + dx, this.posY + dy, this.posZ + dz, mx, my, mz, Block.getStateId(BlockRegistry.LOG_SPIRIT_TREE.getDefaultState()));
			}

			SoundType soundType = SoundType.WOOD;
			this.world.playSound(this.posX, this.posY, this.posZ, soundType.getHitSound(), SoundCategory.NEUTRAL, (soundType.getVolume() + 1.0F) / 8.0F, soundType.getPitch() * 0.5F, false);
		}
	}

	@Override
	public void writeSpawnData(ByteBuf data) {
		data.writeLong(this.origin.toLong());
		data.writeInt(this.delay);
		data.writeInt(this.attackTicks);
		data.writeBoolean(this.isChains);
	}

	@Override
	public void readSpawnData(ByteBuf data) {
		this.origin = BlockPos.fromLong(data.readLong());
		this.delay = data.readInt();
		this.attackTicks = data.readInt();
		this.isChains = data.readBoolean();
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		this.delay = nbt.getInteger("delay");
		this.origin = BlockPos.fromLong(nbt.getLong("origin"));
		this.attackTicks = nbt.getInteger("attackTicks");
		this.dataManager.set(DAMAGE, nbt.getFloat("damage"));
		this.isChains = nbt.getBoolean("isChains");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setInteger("delay", this.delay);
		nbt.setLong("origin", this.origin.toLong());
		nbt.setInteger("attackTicks", this.attackTicks);
		nbt.setFloat("damage", this.dataManager.get(DAMAGE));
		nbt.setBoolean("isChains", this.isChains);
	}
}
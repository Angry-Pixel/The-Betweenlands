package thebetweenlands.common.entity;

import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.render.particle.entity.ParticleLightningArc;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityBLLightningBolt extends EntityLightningBolt implements IEntityAdditionalSpawnData {
	private static final byte EVENT_STRIKE = 80;

	private BlockPos startPos = BlockPos.ORIGIN;
	private int delay = 60;
	private boolean isFloatingTarget;
	
	private boolean effectOnly;
	
	public EntityBLLightningBolt(World world) {
		super(world, 0, 0, 0, true);
		this.setSize(1, 1);
		this.isImmuneToFire = true;
	}

	public EntityBLLightningBolt(World world, double x, double y, double z, int delay, boolean isFloatingTarget, boolean effectOnly) {
		super(world, x, y, z, true);
		this.setSize(1, 1);
		this.isImmuneToFire = true;
		this.delay = Math.max(8, delay);
		this.startPos = new BlockPos(x, y, z).add(world.rand.nextInt(40) - 20, 80, world.rand.nextInt(40) - 20);
		this.isFloatingTarget = isFloatingTarget;
		this.effectOnly = effectOnly;
	}

	@Override
	public void setLocationAndAngles(double x, double y, double z, float yaw, float pitch) {
		super.setLocationAndAngles(x, y, z, yaw, pitch);
		
		if(BlockPos.ORIGIN.equals(this.startPos)) {
			this.startPos = new BlockPos(x, y, z).add(world.rand.nextInt(40) - 20, 80, world.rand.nextInt(40) - 20);
		}
	}
	
	@Override
	protected void entityInit() {

	}

	@Override
	public boolean writeToNBTOptional(NBTTagCompound compound) {
		//don't save
		return false;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {

	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {

	}

	@SideOnly(Side.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);

		if(id == EVENT_STRIKE) {
			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.BEAM, this.createParticle(new Vec3d(this.startPos.getX() + 0.5f, this.startPos.getY(), this.startPos.getZ() + 0.5f), this.getPositionVector()));

			if(this.isFloatingTarget) {
				BlockPos ground = this.world.getHeight(this.getPosition());
				BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.BEAM, this.createParticle(this.getPositionVector(), new Vec3d(ground.getX() + 0.5f, ground.getY(), ground.getZ() + 0.5f)));
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private ParticleLightningArc createParticle(Vec3d start, Vec3d end) {
		ParticleLightningArc particle = (ParticleLightningArc) BLParticles.LIGHTNING_ARC.create(this.world, start.x, start.y, start.z, 
				ParticleArgs.get().withColor(0.5f, 0.4f, 1.0f, 0.9f).withData(end));

		particle.setBaseSize(0.8f);
		particle.setSubdivs(15, 4);
		particle.setOffsets(4.0f, 0.8f);
		particle.setSplits(3);
		particle.setSplitSpeed(0.1f, 0.65f);
		particle.setLengthDecay(0.1f);
		particle.setSizeDecay(0.3f);
		particle.setMaxAge(20);

		return particle;
	}

	@Override
	public void onUpdate() {
		if(!this.world.isRemote) {
			this.setFlag(6, this.isGlowing());
		}

		this.onEntityUpdate();

		this.delay = Math.max(this.delay - 1, 0);

		if(this.delay == 6) {
			this.world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, SoundRegistry.THUNDER, SoundCategory.WEATHER, 10000.0F, 0.8F + this.rand.nextFloat() * 0.2F);
			this.world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, SoundRegistry.LIGHTNING, SoundCategory.WEATHER, 2.0F, 0.5F + this.rand.nextFloat() * 0.2F);

			this.world.setEntityState(this, EVENT_STRIKE);
		} else if(this.delay > 0 && this.delay <= 4) {
			if(this.world.isRemote) {
				this.world.setLastLightningBolt(2);
			} else if(!this.effectOnly) {
				if(this.delay == 4) {
					BlockPos blockpos = new BlockPos(this);

					if(!this.world.isRemote && this.world.getGameRules().getBoolean("doFireTick") && (this.world.getDifficulty() == EnumDifficulty.NORMAL || this.world.getDifficulty() == EnumDifficulty.HARD) && this.world.isAreaLoaded(blockpos, 10)) {
						if(this.world.getBlockState(blockpos).getMaterial() == Material.AIR && Blocks.FIRE.canPlaceBlockAt(this.world, blockpos)) {
							this.world.setBlockState(blockpos, Blocks.FIRE.getDefaultState());
						}

						for(int i = 0; i < 4; ++i) {
							BlockPos blockpos1 = blockpos.add(this.rand.nextInt(3) - 1, this.rand.nextInt(3) - 1, this.rand.nextInt(3) - 1);

							if(this.world.getBlockState(blockpos1).getMaterial() == Material.AIR && Blocks.FIRE.canPlaceBlockAt(this.world, blockpos1)) {
								this.world.setBlockState(blockpos1, Blocks.FIRE.getDefaultState());
							}
						}
					}
				}


				Vec3d start = new Vec3d(this.startPos.getX() + 0.5f, this.startPos.getY(), this.startPos.getZ());
				Vec3d end = this.getPositionVector();

				Vec3d diff = end.subtract(start);
				Vec3d dir = diff.normalize();

				double length = diff.length();

				double range = 5.0D;

				int steps = MathHelper.ceil(length / range / 2);
				for(int i = 0; i < steps;i++) {
					Vec3d checkPos = start.add(diff.scale(1 / (float)steps * (i + 1)));

					List<Entity> nearbyEntities = this.world.getEntitiesWithinAABBExcludingEntity(this, new AxisAlignedBB(checkPos.x - range, checkPos.y - range, checkPos.z - range, checkPos.x + range, checkPos.y + range, checkPos.z + range));

					for(Entity entity : nearbyEntities) {
						if(entity instanceof EntityLightningBolt == false) {
							Vec3d entityPos = entity.getPositionVector();

							Vec3d projection = start.add(dir.scale(dir.dotProduct(entityPos.subtract(start))));

							if(projection.subtract(entityPos).length() < range) {

								if(entity instanceof EntityItem) {
									EntityItem entityItem = (EntityItem) entity;
									ItemStack stack = entityItem.getItem();
									Item item = stack.getItem();

									if(item == ItemRegistry.ANGLER_TOOTH_ARROW || item == ItemRegistry.BASILISK_ARROW || item == ItemRegistry.OCTINE_ARROW || item == ItemRegistry.POISONED_ANGLER_TOOTH_ARROW || item == ItemRegistry.SLUDGE_WORM_ARROW) {
										if(this.world.rand.nextInt(5) == 0) {
											int converted = this.world.rand.nextInt(Math.min(stack.getCount(), 5)) + 1;

											stack.shrink(converted);
											if(stack.isEmpty()) {
												entityItem.setDead();
											} else {
												entityItem.setItem(stack);
											}

											EntityItem arrows = new EntityItem(this.world, entityItem.posX, entityItem.posY, entityItem.posZ, new ItemStack(ItemRegistry.SHOCK_ARROW, converted));
											this.world.spawnEntity(arrows);
										}
									} else if(item == ItemRegistry.CHIROBARB_ERUPTER) {
										entityItem.setItem(new ItemStack(ItemRegistry.CHIROBARB_SHOCK_ERUPTER, stack.getCount()));
									}
									
								} else if(!net.minecraftforge.event.ForgeEventFactory.onEntityStruckByLightning(entity, this)) {
									entity.onStruckByLightning(this);
								}

							}
						}
					}
				}
			}
		} else if(this.delay == 0 && !this.world.isRemote) {
			this.setDead();
		}

		if(this.world.isRemote) {
			this.spawnArcs();
		}
	}

	@SideOnly(Side.CLIENT)
	private void spawnArcs() {
		Entity view = Minecraft.getMinecraft().getRenderViewEntity();

		if(view != null && (this.delay < 30 || this.ticksExisted % (this.delay / 20 + 1) == 0)) {
			float dst = view.getDistance(this);

			if(dst < 100) {
				float ox = (this.world.rand.nextFloat() - 0.5f) * 4;
				float oy;
				if(this.isFloatingTarget) {
					oy = (this.world.rand.nextFloat() - 0.5f) * 4;
				} else {
					oy = this.world.rand.nextFloat() * 2;
				}
				float oz = (this.world.rand.nextFloat() - 0.5f) * 4;

				ParticleLightningArc particle = (ParticleLightningArc) BLParticles.LIGHTNING_ARC.create(this.world, this.posX, this.posY, this.posZ, 
						ParticleArgs.get()
						.withColor(0.5f, 0.4f, 1.0f, 0.9f)
						.withData(new Vec3d(this.posX + ox, this.posY + oy, this.posZ + oz)));

				if(dst > 30) {
					//lower quality
					particle.setBaseSize(0.1f);
					particle.setSubdivs(2, 1);
					particle.setSplits(2);
				}

				BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.BEAM, particle);

				if(dst < 16) {
					this.world.playSound(this.posX, this.posY, this.posZ, SoundRegistry.ZAP, SoundCategory.AMBIENT, 1, 1, false);
				}
			}
		}
	}

	@Override
	public void writeSpawnData(ByteBuf buf) {
		buf.writeBoolean(this.isFloatingTarget);
		buf.writeInt(this.delay);
		buf.writeInt(this.startPos.getX());
		buf.writeInt(this.startPos.getY());
		buf.writeInt(this.startPos.getZ());
	}

	@Override
	public void readSpawnData(ByteBuf buf) {
		this.isFloatingTarget = buf.readBoolean();
		this.delay = buf.readInt();
		this.startPos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
	}
}

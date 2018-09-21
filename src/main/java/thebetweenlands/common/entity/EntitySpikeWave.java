package thebetweenlands.common.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.render.particle.entity.ParticleRootSpike;
import thebetweenlands.client.render.particle.entity.ParticleRootSpike.RootRenderer;

public class EntitySpikeWave extends Entity implements IEntityAdditionalSpawnData {
	protected List<BlockPos> positions = new ArrayList<>();

	private AxisAlignedBB blockEnclosingBounds;
	private AxisAlignedBB renderingBounds;

	public BlockPos origin;
	public int delay;

	@SideOnly(Side.CLIENT)
	public Map<BlockPos, List<RootRenderer>> modelParts;

	public EntitySpikeWave(World world) {
		super(world);
		this.setSize(1, 1);
		this.noClip = true;

		if(world.isRemote) {
			this.modelParts = new HashMap<>();
		}
	}

	public void addPosition(BlockPos pos) {
		if(this.origin == null) {
			this.origin = pos;
			this.lastTickPosX = this.prevPosX = this.posX = pos.getX() + 0.5D;
			this.lastTickPosY = this.prevPosY = this.posY = pos.getY();
			this.lastTickPosZ = this.prevPosZ = this.posZ = pos.getZ() + 0.5D;
		}

		this.positions.add(pos);

		AxisAlignedBB aabb = new AxisAlignedBB(pos).expand(0, 1, 0);
		if(this.blockEnclosingBounds == null) {
			this.blockEnclosingBounds = aabb;
		} else {
			this.blockEnclosingBounds = this.blockEnclosingBounds.union(aabb);
		}
		this.renderingBounds = this.blockEnclosingBounds.offset(this.posX - (this.origin.getX() + 0.5D), this.posY - this.origin.getY(), this.posZ - (this.origin.getZ() + 0.5D));

		if(this.world.isRemote) {
			this.addRootModels(pos);
		}
	}

	@SideOnly(Side.CLIENT)
	protected void addRootModels(BlockPos pos) {
		int models = 1 + this.rand.nextInt(2);
		List<RootRenderer> renderers = new ArrayList<>();
		for(int i = 0; i < models; i++) {
			Vec3d offset = new Vec3d(
					pos.getX() + this.rand.nextDouble() * 0.6D - 0.3D - this.posX,
					pos.getY() + 0.25D - this.posY,
					pos.getZ() + this.rand.nextDouble() * 0.6D - 0.3D - this.posZ
					);
			float scale = 0.4F + this.rand.nextFloat() * 0.2F;
			RootRenderer renderer = new RootRenderer(2, scale * 0.5F, scale, this.rand.nextLong(), offset.x, offset.y, offset.z).build(DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL, Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(ParticleRootSpike.TEXTURE.toString()));
			renderers.add(renderer);
		}
		this.modelParts.put(pos, renderers);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getBrightnessForRender() {
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(MathHelper.floor(this.posX), 0, MathHelper.floor(this.posZ));

		if (this.world.isBlockLoaded(pos)) {
			pos.setY(MathHelper.floor(this.posY + (double)this.getEyeHeight()) + 1);
			return this.world.getCombinedLight(pos, 0);
		} else {
			return 0;
		}
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

		if(this.ticksExisted >= this.delay) {
			if(this.world.isRemote && this.ticksExisted == this.delay) {
				this.spawnEmergeParticles();
			}
			if(this.ticksExisted == this.delay && this.motionY <= 0.0D) {
				this.motionY += 0.25D;
			} else {
				this.motionY -= 0.05D;

				if(!this.world.isRemote && (this.posY <= this.origin.getY() || this.onGround)) {
					this.setDead();
				}
			}
		} else {
			this.motionY = 0.0D;
		}

		if(this.posY < -64.0D) {
			this.setDead();
		}

		if(this.posY + this.motionY <= this.origin.getY()) {
			this.motionY = 0.0D;
			this.setLocationAndAngles(this.posX, this.origin.getY(), this.posZ, 0, 0);
		} else {
			this.move(MoverType.SELF, 0, this.motionY, 0);
		}

		if(this.motionY > 0.1D && !this.world.isRemote) {
			DamageSource damageSource = new EntityDamageSource("bl.spikewave", this);
			for(BlockPos pos : this.positions) {
				AxisAlignedBB aabb = new AxisAlignedBB(pos).offset(this.posX - (this.origin.getX() + 0.5D), this.posY - this.origin.getY(), this.posZ - (this.origin.getZ() + 0.5D)).shrink(0.1D).offset(0, 0.2D, 0);
				List<EntityLivingBase> entities = this.world.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
				for(EntityLivingBase entity : entities) {      
					if (entity instanceof EntityLivingBase) {    
						//TODO
						entity.attackEntityFrom(damageSource, 10F);
					}
				}
			}
		}

		this.renderingBounds = this.blockEnclosingBounds.offset(this.posX - (this.origin.getX() + 0.5D), this.posY - this.origin.getY(), this.posZ - (this.origin.getZ() + 0.5D));

		this.firstUpdate = false;
		this.world.profiler.endSection();
	}

	@SideOnly(Side.CLIENT)
	private void spawnEmergeParticles() {
		if(!this.positions.isEmpty()) {
			int particles = 8 + this.rand.nextInt(8);
			for(int i = 0; i < particles; i++) {
				BlockPos pos = this.positions.get(this.rand.nextInt(this.positions.size()));

				double x = pos.getX() + this.rand.nextDouble();
				double y = pos.getY() + 1;
				double z = pos.getZ() + this.rand.nextDouble();
				double mx = (this.rand.nextDouble() - 0.5D) * 0.8F;
				double my = 0.1D + this.rand.nextDouble() * 0.4F;
				double mz = (this.rand.nextDouble() - 0.5D) * 0.8F;

				BLParticles.ROOT_SPIKE.spawn(this.world, x, y, z, ParticleArgs.get().withMotion(mx, my, mz));
			}

			for(BlockPos pos : this.positions) {
				IBlockState state = this.world.getBlockState(pos);

				if(!state.getBlock().isAir(state, this.world, pos)) {
					int dustParticles = 1 + this.rand.nextInt(3);

					for(int i = 0; i < dustParticles; i++) {
						double x = pos.getX() + this.rand.nextDouble();
						double y = pos.getY() + 1;
						double z = pos.getZ() + this.rand.nextDouble();
						double mx = (this.rand.nextDouble() - 0.5D) * 0.3F;
						double my = 0.1D + this.rand.nextDouble() * 0.2F;
						double mz = (this.rand.nextDouble() - 0.5D) * 0.3F;

						this.world.spawnParticle(EnumParticleTypes.BLOCK_DUST, x, y, z, mx, my, mz, Block.getStateId(state));
					}
				}
			}
		}
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
	public void writeSpawnData(ByteBuf data) {
		data.writeLong(this.origin.toLong());

		data.writeInt(this.positions.size());
		for(BlockPos pos : this.positions) {
			data.writeLong(pos.toLong());
		}

		data.writeInt(this.delay);
	}

	@Override
	public void readSpawnData(ByteBuf data) {
		this.origin = BlockPos.fromLong(data.readLong());

		this.modelParts.clear();
		this.blockEnclosingBounds = null;
		this.positions.clear();
		int size = data.readInt();
		for(int i = 0; i < size; i++) {
			this.addPosition(BlockPos.fromLong(data.readLong()));
		}

		this.delay = data.readInt();
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return this.renderingBounds;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		this.delay = nbt.getInteger("delay");
		this.origin = BlockPos.fromLong(nbt.getLong("origin"));

		this.positions.clear();
		NBTTagList blocks = nbt.getTagList("positions", Constants.NBT.TAG_LONG);
		for(int i = 0; i < blocks.tagCount(); i++) {
			this.addPosition(BlockPos.fromLong(((NBTTagLong)blocks.get(i)).getLong()));
		}
		if(this.positions.isEmpty()) {
			this.addPosition(this.origin);
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setInteger("delay", this.delay);
		nbt.setLong("origin", this.origin.toLong());

		NBTTagList blocks = new NBTTagList();
		for(BlockPos pos : this.positions) {
			blocks.appendTag(new NBTTagLong(pos.toLong()));
		}
		nbt.setTag("positions", blocks);
	}

	@Override
	protected void entityInit() { }
}
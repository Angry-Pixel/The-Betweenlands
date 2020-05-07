package thebetweenlands.common.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.SoundType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.util.NonNullDelegateList;

public class EntityGreeblingCorpse extends Entity implements IEntityAdditionalSpawnData {
	private static final byte EVENT_FADE = 80;

	private float rotation;

	private boolean looted = false;
	private NonNullList<ItemStack> loot = NonNullList.create();

	private boolean fading;
	public int fadeTimer = 0;

	public EntityGreeblingCorpse(World worldIn) {
		super(worldIn);
		this.setSize(0.6f, 0.2f);
		this.rotation = this.world.rand.nextFloat() * 360.0f;
	}

	@Override
	protected void entityInit() {

	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setBoolean("Fading", this.fading);
		nbt.setInteger("FadeTimer", this.fadeTimer);
		nbt.setBoolean("Looted", this.looted);
		nbt.setInteger("LootCount", this.loot.size());
		nbt.setTag("Loot", ItemStackHelper.saveAllItems(new NBTTagCompound(), this.loot, false));
		nbt.setFloat("Rotation", this.rotation);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		this.fading = nbt.getBoolean("Fading");
		this.fadeTimer = nbt.getInteger("FadeTimer");
		this.looted = nbt.getBoolean("Looted");
		this.loot = NonNullList.withSize(nbt.getInteger("LootCount"), ItemStack.EMPTY);
		ItemStackHelper.loadAllItems(nbt.getCompoundTag("Loot"), this.loot);
		if(nbt.hasKey("Rotation", Constants.NBT.TAG_FLOAT)) {
			this.rotation = nbt.getFloat("Rotation");
		}
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (!this.hasNoGravity())
		{
			this.motionY -= 0.08D;
		}

		this.move(MoverType.SELF, 0, this.motionY, 0);

		this.prevRotationYaw = this.rotationYaw = this.rotation;

		if(this.world.isRemote && this.rand.nextInt(40) == 0) {
			this.spawnParticles();
		}

		if(this.fading) {
			this.fadeTimer++;
		}

		if(!this.world.isRemote && this.fadeTimer > 50) {
			this.setDead();
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);

		if(id == EVENT_FADE) {
			this.fading = true;
		}
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles() {
		BLParticles.MOSQUITO.spawn(this.world, this.posX, this.posY + this.height / 2, this.posZ);
	}

	@SideOnly(Side.CLIENT)
	private void spawnBreakParticles() {
		for(int i = 0; i < 12; i++) {
			BLParticles.WEEDWOOD_LEAF.spawn(this.world, this.posX + (this.world.rand.nextFloat() - 0.5f) * 0.5f, this.posY + this.height / 2, this.posZ + (this.world.rand.nextFloat() - 0.5f) * 0.5f,
					ParticleArgs.get().withMotion(
							(this.world.rand.nextFloat() - 0.5f) * 0.1f,
							this.world.rand.nextFloat() * 0.05f + 0.05f,
							(this.world.rand.nextFloat() - 0.5f) * 0.1f
							));
		}
	}

	@Override
	public boolean hitByEntity(Entity entity) {
		if(!this.fading && entity instanceof EntityPlayer) {
			if(!this.world.isRemote && this.world.rand.nextBoolean()) {
				if(!this.dropLoot((EntityPlayer) entity)) {
					this.fading = true;
					this.world.setEntityState(this, EVENT_FADE);
				}
			}

			if(this.world.isRemote) {
				this.spawnBreakParticles();

				SoundType soundType = SoundType.WOOD;
				this.world.playSound(this.posX, this.posY, this.posZ, soundType.getHitSound(), SoundCategory.NEUTRAL, (soundType.getVolume() + 1.0F) / 4.0F, soundType.getPitch() * 0.5F, false);
			}
		}

		return true;
	}

	protected boolean dropLoot(EntityPlayer player) {
		if(!this.world.isRemote) {
			if(!this.looted) {
				this.looted = true;

				LootTable lootTable = this.world.getLootTableManager().getLootTableFromLocation(LootTableRegistry.GREEBLING_CORPSE);
				LootContext.Builder builder = (new LootContext.Builder((WorldServer)this.world)).withLootedEntity(this).withPlayer(player).withLuck(player.getLuck());

				this.loot = new NonNullDelegateList<ItemStack>(lootTable.generateLootForPools(this.rand, builder.build()), ItemStack.EMPTY);
			}

			for(int i = 0; i < this.loot.size(); i++) {
				ItemStack stack = this.loot.get(i);
				if(!stack.isEmpty()) {
					this.entityDropItem(stack, 0.0F);
					this.loot.set(i, ItemStack.EMPTY);
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public void writeSpawnData(ByteBuf buf) {
		buf.writeFloat(this.rotation);
		buf.writeBoolean(this.fading);
		buf.writeInt(this.fadeTimer);
	}

	@Override
	public void readSpawnData(ByteBuf buf) {
		this.rotation = buf.readFloat();
		this.fading = buf.readBoolean();
		this.fadeTimer = buf.readInt();
	}
}

package thebetweenlands.common.entity.mobs;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import thebetweenlands.common.entity.ai.EntityAIHurtByTargetImproved;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationSpiritTree;

public abstract class EntitySpiritTreeFaceSmallBase extends EntitySpiritTreeFace implements IEntityAdditionalSpawnData {
	private int variant;

	public EntitySpiritTreeFaceSmallBase(World world) {
		super(world);
	}

	@Override
	protected void initEntityAI() {
		super.initEntityAI();

		this.targetTasks.addTask(0, new EntityAIHurtByTargetImproved(this, true));
		this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, false));

		this.tasks.addTask(0, new AITrackTargetSpiritTreeFace(this, true, 28.0D));
		this.tasks.addTask(1, new AIAttackMelee(this, 1, true));
		this.tasks.addTask(2, new AISpit(this, 3.0F));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(2.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
		this.setVariant(this.rand.nextInt(2));
		return super.onInitialSpawn(difficulty, livingdata);
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.SPIRIT_TREE_FACE_SMALL;
	}

	@Override
	public List<BlockPos> findNearbyBlocksForMovement() {
		List<LocationSpiritTree> locations = BetweenlandsWorldStorage.forWorld(this.world).getLocalStorageHandler().getLocalStorages(LocationSpiritTree.class, this.getEntityBoundingBox(), loc -> loc.isInside(this));
		if(!locations.isEmpty()) {
			List<BlockPos> positions = new ArrayList<>();
			positions.addAll(locations.get(0).getSmallFacePositions());
			if(!positions.isEmpty()) {
				return positions;
			}
		}
		return super.findNearbyBlocksForMovement();
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		
		if(this.isAnchored()) {
			this.setSize(0.9F, 0.9F);
		} else {
			this.setSize(0.9F, 0.2F);
		}
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("variant", this.getVariant());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		this.setVariant(nbt.getInteger("variant"));
	}

	public void setVariant(int variant) {
		this.variant = variant;
	}

	public int getVariant() {
		return this.variant;
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeInt(this.variant);
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		this.variant = additionalData.readInt();
	}

	@Override
	protected void playSpitSound() {
		this.playSound(SoundRegistry.SPIRIT_TREE_FACE_SMALL_SPIT, 1, 0.8F + this.rand.nextFloat() * 0.3F);
	}

	@Override
	protected void playEmergeSound() {
		this.playSound(SoundRegistry.SPIRIT_TREE_FACE_SMALL_EMERGE, 1, 0.8F + this.rand.nextFloat() * 0.3F);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.SPIRIT_TREE_FACE_SMALL_LIVING;
	}

	@Override
	public void playLivingSound() {
		SoundEvent sound = this.getAmbientSound();

		if(sound != null) {
			this.playSound(sound, this.getSoundVolume() * 0.5F, this.getSoundPitch() * 1.3F);
		}
	}
}

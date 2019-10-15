package thebetweenlands.common.entity;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.gui.GuiGalleryFrame;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.ItemRegistry;

public class EntityGalleryFrame extends EntityHanging implements IEntityAdditionalSpawnData {
	protected static final DataParameter<String> URL = EntityDataManager.createKey(EntityGalleryFrame.class, DataSerializers.STRING);

	public static enum Type {
		LARGE, SMALL;
	}

	private Type type;

	public EntityGalleryFrame(World world) {
		super(world);
	}

	public EntityGalleryFrame(World world, BlockPos pos, EnumFacing facing, Type type) {
		super(world, pos);
		this.type = type;
		this.updateFacingWithBoundingBox(facing);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(URL, "");
	}

	public void setUrl(String url) {
		this.dataManager.set(URL, url);
	}

	public String getUrl() {
		return this.dataManager.get(URL);
	}

	public Type getType() {
		return this.type;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("type", this.type.ordinal());
		nbt.setString("url", this.getUrl());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		this.type = Type.values()[nbt.getInteger("type")];
		this.setUrl(nbt.getString("url"));
	}

	@Override
	public int getWidthPixels() {
		return this.type == Type.LARGE ? 32 : 16;
	}

	@Override
	public int getHeightPixels() {
		return this.type == Type.LARGE ? 32 : 16;
	}

	@Override
	public void onBroken(@Nullable Entity brokenEntity) {
		if (this.world.getGameRules().getBoolean("doEntityDrops")) {
			this.playSound(SoundEvents.BLOCK_WOOD_BREAK, 1.0F, 1.0F);

			if (brokenEntity instanceof EntityPlayer) {
				EntityPlayer entityplayer = (EntityPlayer)brokenEntity;

				if (entityplayer.capabilities.isCreativeMode) {
					return;
				}
			}

			this.entityDropItem(new ItemStack(this.type == Type.LARGE ? ItemRegistry.GALLERY_FRAME_LARGE : ItemRegistry.GALLERY_FRAME_SMALL), 0.0F);
		}
	}

	@Override
	public void playPlaceSound() {
		this.playSound(SoundEvents.BLOCK_WOOD_PLACE, 1.0F, 1.0F);
	}

	@Override
	public void setLocationAndAngles(double x, double y, double z, float yaw, float pitch)  {
		this.setPosition(x, y, z);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
		this.setPosition((double)this.hangingPosition.getX(), (double)this.hangingPosition.getY(), (double)this.hangingPosition.getZ());
	}

	@Override
	public void writeSpawnData(ByteBuf buf) {
		buf.writeInt(this.type.ordinal());
		buf.writeLong(this.hangingPosition.toLong());
		buf.writeBoolean(this.facingDirection != null);
		if(this.facingDirection != null) {
			buf.writeInt(this.facingDirection.getIndex());
		}
	}

	@Override
	public void readSpawnData(ByteBuf buf) {
		this.type = Type.values()[buf.readInt()];
		this.hangingPosition = BlockPos.fromLong(buf.readLong());
		if(buf.readBoolean()) {
			this.facingDirection = EnumFacing.byIndex(buf.readInt());
			this.updateFacingWithBoundingBox(this.facingDirection);
		}
	}

	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
		if(this.world.isRemote && player == TheBetweenlands.proxy.getClientPlayer()) {
			this.showGalleryGui();
			return true;
		}
		return super.processInitialInteract(player, hand);
	}

	@SideOnly(Side.CLIENT)
	private void showGalleryGui() {
		Minecraft.getMinecraft().displayGuiScreen(new GuiGalleryFrame(this));
	}
}
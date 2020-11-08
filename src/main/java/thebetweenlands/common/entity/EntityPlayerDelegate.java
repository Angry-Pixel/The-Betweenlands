package thebetweenlands.common.entity;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nullable;

import org.apache.commons.lang3.Validate;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class EntityPlayerDelegate extends FakePlayer {
	public static class InventoryDelegateList extends NonNullList<ItemStack> {
		private final IInventory inv;
		private final int invSize;
		private final int size;
		private final NonNullList<ItemStack> excess;

		public InventoryDelegateList(@Nullable IInventory inv, int requiredSize, List<NonNullList<ItemStack>> excess) {
			this.inv = inv;
			this.invSize = inv == null ? 0 : inv.getSizeInventory();
			if(this.invSize < requiredSize) {
				excess.add(this.excess = NonNullList.withSize(requiredSize - this.invSize, ItemStack.EMPTY));
				this.size = requiredSize;
			} else {
				this.excess = NonNullList.withSize(0, ItemStack.EMPTY);
				this.size = this.invSize;
			}
		}

		@Override
		public ItemStack get(int index) {
			if(index >= this.invSize) {
				return this.excess.get(index - this.invSize);
			}
			return this.inv.getStackInSlot(index);
		}

		@Override
		public ItemStack set(int index, ItemStack stack) {
			Validate.notNull(stack);
			ItemStack old;
			if(index >= this.invSize) {
				old = this.excess.get(index - this.invSize);
				this.excess.set(index - this.invSize, stack);
			} else {
				old = this.inv.getStackInSlot(index);
				this.inv.setInventorySlotContents(index, stack);
			}
			return old;
		}

		@Override
		public void add(int index, ItemStack stack) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ItemStack remove(int index) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int size() {
			return this.size;
		}

		@Override
		public void clear() {
			if(this.inv != null) {
				this.inv.clear();
			}
			this.excess.clear();
		}
	}

	public static class DelegateInventoryPlayer extends InventoryPlayer {
		public DelegateInventoryPlayer(EntityPlayer playerIn, @Nullable IInventory main, @Nullable IInventory armor, @Nullable IInventory offhand, List<NonNullList<ItemStack>> excess) {
			super(playerIn);
			this.mainInventory = new InventoryDelegateList(main, 36, excess);
			this.armorInventory = new InventoryDelegateList(main, 4, excess);
			this.offHandInventory = new InventoryDelegateList(main, 1, excess);
			ObfuscationReflectionHelper.setPrivateValue(InventoryPlayer.class, this, Arrays.asList(this.mainInventory, this.armorInventory, this.offHandInventory), "allInventories", "field_184440_g", "f");
		}
	}

	public static class Builder {
		private final Function<Builder, EntityPlayerDelegate> constructor;

		private final WorldServer world;
		private final GameProfile profile;
		private final List<NonNullList<ItemStack>> excess;

		private Entity entity;
		private InventoryPlayer playerInv;
		private IInventory mainInv, armorInv, offhandInv;

		protected Builder(Function<Builder, EntityPlayerDelegate> constructor, WorldServer world, GameProfile profile, List<NonNullList<ItemStack>> excess) {
			this.constructor = constructor;
			this.world = world;
			this.profile = profile;
			this.excess = excess;
		}

		public WorldServer world() {
			return this.world;
		}

		public GameProfile profile() {
			return this.profile;
		}

		public List<NonNullList<ItemStack>> excess() {
			return this.excess;
		}

		public Builder entity(@Nullable Entity entity) {
			this.entity = entity;
			return this;
		}

		public Entity entity() {
			return this.entity;
		}

		public Builder playerInventory(@Nullable InventoryPlayer inv) {
			this.playerInv = inv;
			return this;
		}

		public InventoryPlayer playerInventory() {
			return this.playerInv;
		}

		public Builder mainInventory(@Nullable IInventory inv) {
			this.mainInv = inv;
			return this;
		}

		public IInventory mainInventory() {
			return this.mainInv;
		}

		public Builder armorInventory(@Nullable IInventory inv) {
			this.armorInv = inv;
			return this;
		}

		public IInventory armorInventory() {
			return this.armorInv;
		}

		public Builder offhandInventory(@Nullable IInventory inv) {
			this.offhandInv = inv;
			return this;
		}

		public IInventory offhandInventory() {
			return this.offhandInv;
		}

		public EntityPlayerDelegate build() {
			return this.constructor.apply(this);
		}
	}

	private final Entity entity;

	public EntityPlayerDelegate(WorldServer world, GameProfile name, Entity entity) {
		super(world, name);
		this.entity = entity;
		this.connection = new NetHandlerPlayServer(world.getMinecraftServer(), new NetworkManager(EnumPacketDirection.SERVERBOUND), this);
	}

	private EntityPlayerDelegate(WorldServer world, GameProfile name, @Nullable Entity parent, @Nullable IInventory main, @Nullable IInventory armor, @Nullable IInventory offhand, List<NonNullList<ItemStack>> excess) {
		this(world, name, parent);
		this.inventory = new DelegateInventoryPlayer(parent instanceof EntityPlayer ? (EntityPlayer)parent : this, main, armor, offhand, excess);
	}

	private EntityPlayerDelegate(WorldServer world, GameProfile name, @Nullable Entity entity, InventoryPlayer inv) {
		this(world, name, entity);
		this.inventory = inv;
	}

	public static Builder from(WorldServer world, GameProfile profile, List<NonNullList<ItemStack>> excess) {
		return new Builder((builder) -> {
			if(builder.playerInventory() != null) {
				return new EntityPlayerDelegate(builder.world(), builder.profile(), builder.entity(), builder.playerInventory());
			} else {
				return new EntityPlayerDelegate(builder.world(), builder.profile(), builder.entity(), builder.mainInventory(), builder.armorInventory(), builder.offhandInventory(), builder.excess());
			}
		}, world, profile, excess);
	}

	@Override
	public boolean isSilent() {
		return true;
	}

	@Override
	public void playSound(SoundEvent soundIn, float volume, float pitch) { }

	@Override
	public boolean startRiding(Entity entityIn) { return false; }

	@Override
	public boolean startRiding(Entity entityIn, boolean force) { return false; }

	@Override
	public AbstractAttributeMap getAttributeMap() {
		if(this.entity instanceof EntityLivingBase) {
			return ((EntityLivingBase) this.entity).getAttributeMap();
		}
		return super.getAttributeMap();
	}

	@Override
	public void addPotionEffect(PotionEffect potioneffectIn) {
		if(this.entity instanceof EntityLivingBase) {
			((EntityLivingBase) this.entity).addPotionEffect(potioneffectIn);
		}
	}

	@Override
	public void curePotionEffects(ItemStack curativeItem) {
		if(this.entity instanceof EntityLivingBase) {
			((EntityLivingBase) this.entity).curePotionEffects(curativeItem);
		}
	}

	@Override
	public boolean canBeHitWithPotion() {
		if(this.entity instanceof EntityLivingBase) {
			return ((EntityLivingBase) this.entity).canBeHitWithPotion();
		}
		return super.canBeHitWithPotion();
	}

	@Override
	public void clearActivePotions() {
		if(this.entity instanceof EntityLivingBase) {
			((EntityLivingBase) this.entity).clearActivePotions();
		} else {
			super.clearActivePotions();
		}
	}

	@Override
	public PotionEffect getActivePotionEffect(Potion potionIn) {
		if(this.entity instanceof EntityLivingBase) {
			return ((EntityLivingBase) this.entity).getActivePotionEffect(potionIn);
		}
		return super.getActivePotionEffect(potionIn);
	}

	@Override
	public Collection<PotionEffect> getActivePotionEffects() {
		if(this.entity instanceof EntityLivingBase) {
			return ((EntityLivingBase) this.entity).getActivePotionEffects();
		}
		return super.getActivePotionEffects();
	}

	@Override
	public Map<Potion, PotionEffect> getActivePotionMap() {
		if(this.entity instanceof EntityLivingBase) {
			return ((EntityLivingBase) this.entity).getActivePotionMap();
		}
		return super.getActivePotionMap();
	}

	@Override
	public boolean isPotionActive(Potion potionIn) {
		if(this.entity instanceof EntityLivingBase) {
			return ((EntityLivingBase) this.entity).isPotionActive(potionIn);
		}
		return super.isPotionActive(potionIn);
	}

	@Override
	public boolean isPotionApplicable(PotionEffect potioneffectIn) {
		if(this.entity instanceof EntityLivingBase) {
			return ((EntityLivingBase) this.entity).isPotionApplicable(potioneffectIn);
		}
		return super.isPotionApplicable(potioneffectIn);
	}

	@Override
	public PotionEffect removeActivePotionEffect(Potion potioneffectin) {
		if(this.entity instanceof EntityLivingBase) {
			return ((EntityLivingBase) this.entity).removeActivePotionEffect(potioneffectin);
		}
		return super.removeActivePotionEffect(potioneffectin);
	}

	@Override
	public void removePotionEffect(Potion potionIn) {
		if(this.entity instanceof EntityLivingBase) {
			((EntityLivingBase) this.entity).removePotionEffect(potionIn);
		} else {
			super.removePotionEffect(potionIn);
		}
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		if(this.entity instanceof EntityLivingBase) {
			return ((EntityLivingBase) this.entity).attackEntityAsMob(entityIn);
		}
		return super.attackEntityAsMob(entityIn);
	}

	@Override
	public boolean attackable() {
		if(this.entity instanceof EntityLivingBase) {
			return ((EntityLivingBase) this.entity).attackable();
		}
		return super.attackable();
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if(this.entity != null) {
			return this.entity.attackEntityFrom(source, amount);
		}
		return super.attackEntityFrom(source, amount);
	}

	@Override
	public boolean canBeAttackedWithItem() {
		if(this.entity != null) {
			return this.entity.canBeAttackedWithItem();
		}
		return super.canBeAttackedWithItem();
	}

	@Override
	public boolean canAttackPlayer(EntityPlayer player) {
		if(this.entity instanceof EntityPlayer) {
			return ((EntityPlayer) this.entity).canAttackPlayer(player);
		}
		return super.canAttackPlayer(player);
	}

	@Override
	public boolean getIsInvulnerable() {
		if(this.entity != null) {
			return this.entity.getIsInvulnerable();
		}
		return super.getIsInvulnerable();
	}

	@Override
	public boolean isEntityInvulnerable(DamageSource source) {
		if(this.entity != null) {
			return this.entity.isEntityInvulnerable(source);
		}
		return super.isEntityInvulnerable(source);
	}
}

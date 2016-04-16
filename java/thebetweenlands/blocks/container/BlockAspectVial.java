package thebetweenlands.blocks.container;

import java.util.ArrayList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import thebetweenlands.herblore.Amounts;
import thebetweenlands.herblore.aspects.Aspect;
import thebetweenlands.herblore.aspects.AspectManager;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.tileentities.TileEntityAspectVial;

public class BlockAspectVial extends BlockContainer {

	public static final String[] iconPaths = new String[] { "dentrothyst1", "dentrothyst2" };

	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	public BlockAspectVial() {
		super(Material.glass);
		setStepSound(this.soundTypeGlass);
		setHardness(0.4F);
		setBlockName("thebetweenlands.aspectVialBlock");
		setBlockBounds(0.1F, 0.0F, 0.1F, 0.9F, 0.8F, 0.9F);
	}

	@Override
	public void registerBlockIcons(IIconRegister reg) {
		icons = new IIcon[iconPaths.length];
		int i = 0;
		for (String path : iconPaths)
			this.icons[i++] = reg.registerIcon("thebetweenlands:" + path);
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		if (meta < 0 || meta >= this.icons.length)
			return null;
		return this.icons[meta];
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack is) {
		/*int rot = MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		world.setBlockMetadataWithNotify(x, y, z, rot == 0 ? 2 : rot == 1 ? 5 : rot == 2 ? 3 : 4, 3);*/
		//Maybe add rotation
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metadata, float hitX, float hitY, float hitZ) {
		if (world.getTileEntity(x, y, z) instanceof TileEntityAspectVial) {
			TileEntityAspectVial tile = (TileEntityAspectVial) world.getTileEntity(x, y, z);

			if (player.getHeldItem() != null) {
				ItemStack heldItem = player.getHeldItem();

				if(heldItem.getItem() == BLItemRegistry.aspectVial && AspectManager.getDynamicAspects(heldItem).size() == 1) {
					Aspect itemAspect = AspectManager.getDynamicAspects(heldItem).get(0);
					if(!player.isSneaking()) {
						if(tile.getAspect() == null || tile.getAspect().type == itemAspect.type) {
							if(!world.isRemote) {
								if(tile.getAspect() == null)
									tile.setAspect(new Aspect(itemAspect.type, 0.0F));
								float added = tile.addAmount(Math.min(itemAspect.amount, 0.25F));
								if(added > 0.0F) {
									AspectManager.removeDynamicAspects(heldItem);
									float leftAmount = itemAspect.amount - added;
									if(leftAmount > 0.0F) {
										AspectManager.addDynamicAspects(heldItem, new Aspect(itemAspect.type, itemAspect.amount - added));
									} else {
										int type = heldItem.getItemDamage();
										switch(type) {
										default:
										case 0:
											player.setCurrentItemOrArmor(0, BLItemRegistry.dentrothystVial.createStack(0));
											break;
										case 1:
											player.setCurrentItemOrArmor(0, BLItemRegistry.dentrothystVial.createStack(2));
											break;
										}
									}
								}
							}
							player.swingItem();
							return true;
						}
					} else {
						if(tile.getAspect() != null && tile.getAspect().type == itemAspect.type) {
							if(!world.isRemote) {
								float toRemove = Math.min(0.25F, Amounts.MAX_VIAL_ASPECT_AMOUNT - itemAspect.amount);
								if(toRemove > 0.0F) {
									float removedAmount = tile.removeAmount(toRemove);
									AspectManager.removeDynamicAspects(heldItem);
									AspectManager.addDynamicAspects(heldItem, new Aspect(itemAspect.type, itemAspect.amount + removedAmount));
								}
							}
							player.swingItem();
							return true;
						}
					}
				} else if(heldItem.getItem() == BLItemRegistry.dentrothystVial && player.isSneaking() && tile.getAspect() != null && heldItem.getItemDamage() != 1) {
					if(!world.isRemote) {
						Aspect aspect = tile.getAspect();
						float removedAmount = tile.removeAmount(0.25F);
						if(removedAmount > 0.0F) {
							ItemStack vial = new ItemStack(BLItemRegistry.aspectVial);
							switch(heldItem.getItemDamage()) {
							default:
							case 0:
								vial.setItemDamage(0);
								break;
							case 2:
								vial.setItemDamage(1);
								break;
							}
							AspectManager.addDynamicAspects(vial, new Aspect(aspect.type, removedAmount));

							heldItem.stackSize--;
							if(heldItem.stackSize <= 0)
								player.setCurrentItemOrArmor(0, null);

							//Drop new aspect item
							EntityItem itemEntity = player.dropPlayerItemWithRandomChoice(vial, false);
							if(itemEntity != null) itemEntity.delayBeforeCanPickup = 0;
						}
					}
					player.swingItem();
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityAspectVial();
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		TileEntityAspectVial tile = (TileEntityAspectVial) world.getTileEntity(x, y, z);
		if(tile != null) {
			if(tile.getAspect() != null) {
				if(tile.getAspect().amount > 0.0F) {
					ItemStack vial = new ItemStack(BLItemRegistry.aspectVial);
					vial.setItemDamage(metadata);
					AspectManager.addDynamicAspects(vial, tile.getAspect());
					ret.add(vial);
				}
			} else {
				ItemStack vial = new ItemStack(BLItemRegistry.dentrothystVial);
				switch(metadata) {
				default:
				case 0:
					vial.setItemDamage(0);
					break;
				case 1:
					vial.setItemDamage(2);
					break;
				}
				ret.add(vial);
			}
		}
		return ret;
	}

	@Override
	public void onBlockHarvested(World world, int x, int y, int z, int id, EntityPlayer player) {
		if(world.isRemote) return;

		player.addStat(StatList.mineBlockStatArray[getIdFromBlock(this)], 1);
		player.addExhaustion(0.025F);
		if (!world.isRemote && !world.restoringBlockSnapshots && world.getGameRules().getGameRuleBooleanValue("doTileDrops") && !player.capabilities.isCreativeMode) {
			ArrayList<ItemStack> drops = this.getDrops(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			float chance = ForgeEventFactory.fireBlockHarvesting(drops, world, this, x, y, z, world.getBlockMetadata(x, y, z), 0, 1, false, harvesters.get());
			for (ItemStack item : drops) {
				if (world.rand.nextFloat() <= chance) {
					float f = 0.7F;
					double d0 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
					double d1 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
					double d2 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
					EntityItem entityitem = new EntityItem(world, (double)x + d0, (double)y + d1, (double)z + d2, item);
					entityitem.delayBeforeCanPickup = 10;
					world.spawnEntityInWorld(entityitem);
				}
			}
		}

		world.setBlockToAir(x, y, z);
	}
}
package thebetweenlands.common.block.container;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.AspectItem;
import thebetweenlands.api.aspect.DiscoveryContainer;
import thebetweenlands.api.aspect.DiscoveryContainer.AspectDiscovery;
import thebetweenlands.api.aspect.DiscoveryContainer.AspectDiscovery.EnumDiscoveryResult;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.mobs.EntityGecko;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityGeckoCage;
import thebetweenlands.util.TranslationHelper;

public class BlockGeckoCage extends BlockContainer {
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

	public BlockGeckoCage() {
		super(Material.WOOD);
		setHardness(2.0F);
		setResistance(5.0F);
		setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	@Override
	public boolean hasCustomBreakingProgress(IBlockState state) {
		return true;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getHorizontalIndex();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[]{FACING});
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		int rotation = MathHelper.floor(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		state = state.withProperty(FACING, EnumFacing.byHorizontalIndex(rotation).getOpposite());
		worldIn.setBlockState(pos, state, 3);
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		if (!world.isRemote) {
			TileEntity te = world.getTileEntity(pos);
			if (te instanceof TileEntityGeckoCage) {
				TileEntityGeckoCage tile = (TileEntityGeckoCage) te;
				if (tile.hasGecko()) {
					EntityGecko gecko = new EntityGecko(world);
					gecko.setHealth(tile.getGeckoUsages());
					gecko.setLocationAndAngles(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, 0.0F, 0.0F);
					if (tile.getGeckoName() != null && !tile.getGeckoName().isEmpty())
						gecko.setCustomNameTag(tile.getGeckoName());
					world.spawnEntity(gecko);
					gecko.playLivingSound();
					if (player instanceof EntityPlayerMP)
						AdvancementCriterionRegistry.GECKO_TRIGGER.trigger((EntityPlayerMP) player, false, true);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	protected void spawnHeartParticles(World world, BlockPos pos) {
		for (int i = 0; i < 7; ++i) {
			double d0 = world.rand.nextGaussian() * 0.02D;
			double d1 = world.rand.nextGaussian() * 0.02D;
			double d2 = world.rand.nextGaussian() * 0.02D;
			world.spawnParticle(EnumParticleTypes.HEART, pos.getX() + world.rand.nextFloat(), pos.getY() + world.rand.nextFloat(), pos.getZ() + world.rand.nextFloat(), d0, d1, d2, new int[0]);
		}
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,  EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack heldItemStack = player.getHeldItem(hand);
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityGeckoCage) {
			TileEntityGeckoCage tile = (TileEntityGeckoCage) te;

			if(player.isSneaking())
				return false;

			if(!heldItemStack.isEmpty()) {

				Item heldItem = heldItemStack.getItem();
				if(heldItem == ItemRegistry.GECKO) {
					if(!tile.hasGecko()) {
						if(!world.isRemote) {
							String name = "";
							if (!(heldItemStack.getDisplayName().equals(TranslationHelper.translateToLocal(heldItemStack.getTranslationKey()))) && heldItemStack.hasDisplayName())
									name = heldItemStack.getDisplayName();
	
							tile.addGecko(heldItemStack.hasTagCompound() && heldItemStack.getTagCompound().hasKey("Health") ? (int) heldItemStack.getTagCompound().getFloat("Health") : 12, name);
							if(!player.capabilities.isCreativeMode)
								heldItemStack.shrink(1);
						}
						return true;
					}
					return false;
				}
				if(heldItem == ItemRegistry.SAP_SPIT && tile.hasGecko() && tile.getGeckoUsages() < 12) {
					if(!world.isRemote) {
						tile.setGeckoUsages(12);
						if(!player.capabilities.isCreativeMode)
							heldItemStack.shrink(1);
					} else {
						this.spawnHeartParticles(world, pos);
					}
					return true;
				} else if(tile.getAspectType() == null) {
					if(tile.hasGecko()) {
						if(DiscoveryContainer.hasDiscoveryProvider(player)) {
							if(!world.isRemote) {
								AspectManager manager = AspectManager.get(world);
								AspectItem aspectItem = AspectManager.getAspectItem(heldItemStack);
								List<Aspect> aspects = manager.getStaticAspects(aspectItem);
								if(aspects.size() > 0) {
									DiscoveryContainer<?> mergedKnowledge = DiscoveryContainer.getMergedDiscoveryContainer(player);
									AspectDiscovery discovery = mergedKnowledge.discover(manager, aspectItem);
									switch(discovery.result) {
									case NEW:
									case LAST:
										DiscoveryContainer.addDiscoveryToContainers(player, aspectItem, discovery.discovered.type);
										tile.setAspectType(discovery.discovered.type, 600);
										if (player instanceof EntityPlayerMP) {
											AdvancementCriterionRegistry.GECKO_TRIGGER.trigger((EntityPlayerMP) player, true, false);
											if (discovery.result == EnumDiscoveryResult.LAST && DiscoveryContainer.getMergedDiscoveryContainer(player).haveDiscoveredAll(manager))
												AdvancementCriterionRegistry.HERBLORE_FIND_ALL.trigger((EntityPlayerMP) player);
										}
										player.sendStatusMessage(new TextComponentTranslation("chat.aspect.discovery." + discovery.discovered.type.getName().toLowerCase()), false);
										if(discovery.result == EnumDiscoveryResult.LAST) {
                                            player.sendStatusMessage(new TextComponentTranslation("chat.aspect.discovery.last"), true);
                                            player.sendStatusMessage(new TextComponentTranslation("chat.aspect.discovery.last"), false);
                                        } else {
                                            player.sendStatusMessage(new TextComponentTranslation("chat.aspect.discovery.more"), true);
                                            player.sendStatusMessage(new TextComponentTranslation("chat.aspect.discovery.more"), false);
                                        }
										if(!player.capabilities.isCreativeMode)
                                            heldItemStack.shrink(1);
										return true;
									case END:
										//already all discovered
										player.sendStatusMessage(new TextComponentTranslation("chat.aspect.discovery.end"), true);
										return false;
									default:
										//no aspects
										player.sendStatusMessage(new TextComponentTranslation("chat.aspect.discovery.none"), true);
										return false;
									}
								} else {
									player.sendStatusMessage(new TextComponentTranslation("chat.aspect.discovery.none"), true);
									return true;
								}
							} else {
								//no aspects
								return false;
							}
						} else {
							//no herblore book
							if(!world.isRemote) 
								player.sendStatusMessage(new TextComponentTranslation("chat.aspect.discovery.book.none"), true);
							return false;
						}
					} else {
						//no gecko
						if(!world.isRemote) 
							player.sendStatusMessage(new TextComponentTranslation("chat.aspect.discovery.gecko.none"), true);
						return false;
					}
				} else {
					//recovering
					if(!world.isRemote) 
						player.sendStatusMessage(new TextComponentTranslation("chat.aspect.discovery.gecko.recovering"), true);
					return false;
				}
			}
		}

		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityGeckoCage();
	}
	
	@Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    	return BlockFaceShape.UNDEFINED;
    }
}

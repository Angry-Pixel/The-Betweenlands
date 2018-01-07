package thebetweenlands.common.block.container;


import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.ItemAspectContainer;
import thebetweenlands.common.block.terrain.BlockDentrothyst;
import thebetweenlands.common.herblore.Amounts;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityAspectVial;

import javax.annotation.Nullable;

public class BlockAspectVial extends BlockContainer implements BlockRegistry.ICustomItemBlock {

    public static PropertyEnum<BlockDentrothyst.EnumDentrothyst> TYPE = PropertyEnum.create("type", BlockDentrothyst.EnumDentrothyst.class);

    public static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0.1F, 0.0F, 0.1F, 0.9F, 0.8F, 0.9F);

    public BlockAspectVial() {
        super(Material.GLASS);
        setSoundType(SoundType.GLASS);
        setHardness(0.4F);
        setDefaultState(this.blockState.getBaseState().withProperty(TYPE, BlockDentrothyst.EnumDentrothyst.GREEN));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BOUNDING_BOX;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TYPE, meta == 0 ? BlockDentrothyst.EnumDentrothyst.GREEN : BlockDentrothyst.EnumDentrothyst.ORANGE);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE).getMeta();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }

    @Nullable
    @Override
    public ItemStack getRenderedItem() {
        return new ItemStack(ItemRegistry.ASPECT_VIAL);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.getTileEntity(pos) instanceof TileEntityAspectVial) {
            TileEntityAspectVial tile = (TileEntityAspectVial) world.getTileEntity(pos);

            if (!player.getHeldItem(hand).isEmpty()) {
                ItemStack heldItem = player.getHeldItem(hand);

                ItemAspectContainer container = null;
                if(heldItem.getItem() == ItemRegistry.ASPECT_VIAL && (container = ItemAspectContainer.fromItem(heldItem)).getAspects().size() == 1) {
                    Aspect itemAspect = container.getAspects().get(0);
                    if(!player.isSneaking()) {
                        if(tile.getAspect() == null || tile.getAspect().type == itemAspect.type) {
                            if(!world.isRemote) {
                                if(tile.getAspect() == null)
                                    tile.setAspect(new Aspect(itemAspect.type, 0));
                                int added = tile.addAmount(Math.min(itemAspect.amount, 25));
                                if(added > 0) {
                                    int leftAmount = itemAspect.amount - added;
                                    container.set(itemAspect.type, itemAspect.amount - added);
                                    if (leftAmount <= 0) {
                                        int type = heldItem.getItemDamage();
                                        switch(type) {
                                            default:
                                            case 0:
                                                player.setHeldItem(hand, ItemRegistry.DENTROTHYST_VIAL.createStack(0));
                                                break;
                                            case 1:
                                                player.setHeldItem(hand, ItemRegistry.DENTROTHYST_VIAL.createStack(2));
                                                break;
                                        }
                                    }
                                }
                            }
                            player.swingArm(hand);
                            return true;
                        }
                    } else {
                        if(tile.getAspect() != null && tile.getAspect().type == itemAspect.type) {
                            if(!world.isRemote) {
                                int toRemove = (int) Math.min(25, Amounts.MAX_ASPECT_AMOUNT - itemAspect.amount);
                                if(toRemove > 0) {
                                    int removedAmount = tile.removeAmount(toRemove);
                                    container.set(itemAspect.type, itemAspect.amount + removedAmount);
                                }
                            }
                            player.swingArm(hand);
                            return true;
                        }
                    }
                } else if(heldItem.getItem() == ItemRegistry.DENTROTHYST_VIAL && player.isSneaking() && tile.getAspect() != null && heldItem.getItemDamage() != 1) {
                    if(!world.isRemote) {
                        Aspect aspect = tile.getAspect();
                        int removedAmount = tile.removeAmount(25);
                        if(removedAmount > 0) {
                            ItemStack vial = new ItemStack(ItemRegistry.ASPECT_VIAL);
                            switch(heldItem.getItemDamage()) {
                                default:
                                case 0:
                                    vial.setItemDamage(0);
                                    break;
                                case 2:
                                    vial.setItemDamage(1);
                                    break;
                            }
                            container = ItemAspectContainer.fromItem(vial);
                            container.add(aspect.type, removedAmount);

                            heldItem.shrink(1);
                            if(heldItem.getCount() <= 0)
                                player.setHeldItem(hand, heldItem);

                            //Drop new aspect item
                            EntityItem itemEntity = player.dropItem(vial, false);
                            if(itemEntity != null) itemEntity.setPickupDelay(0);
                        }
                    }
                    player.swingArm(hand);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityAspectVial();
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        NonNullList<ItemStack> list = NonNullList.create();
        getDrops(list, world, pos, state, 0);
        return list.size() > 0 ? list.get(0): ItemStack.EMPTY;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        TileEntityAspectVial tile = (TileEntityAspectVial) world.getTileEntity(pos);
        int metadata = getMetaFromState(state);
        if(tile != null) {
            if(tile.getAspect() != null) {
                if(tile.getAspect().amount > 0) {
                    ItemStack vial = new ItemStack(ItemRegistry.ASPECT_VIAL);
                    vial.setItemDamage(metadata);
                    ItemAspectContainer.fromItem(vial).add(tile.getAspect().type, tile.getAspect().amount);
                    drops.add(vial);
                }
            } else {
                ItemStack vial = new ItemStack(ItemRegistry.DENTROTHYST_VIAL);
                switch(metadata) {
                    default:
                    case 0:
                        vial.setItemDamage(0);
                        break;
                    case 1:
                        vial.setItemDamage(2);
                        break;
                }
                drops.add(vial);
            }
        }
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        if(world.isRemote) return;

        player.addStat(StatList.getBlockStats(this), 1);
        player.addExhaustion(0.025F);
        if (!world.isRemote && !world.restoringBlockSnapshots && world.getGameRules().getBoolean("doTileDrops") && !player.capabilities.isCreativeMode) {
            NonNullList<ItemStack> drops = NonNullList.create();
            getDrops(drops, world, pos, state, 0);
            float chance = ForgeEventFactory.fireBlockHarvesting(drops, world, pos, world.getBlockState(pos), 0, 1, false, harvesters.get());
            for (ItemStack item : drops) {
                if (world.rand.nextFloat() <= chance) {
                    float f = 0.7F;
                    double d0 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                    double d1 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                    double d2 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                    EntityItem entityitem = new EntityItem(world, (double)pos.getX() + d0, (double)pos.getY() + d1, (double)pos.getZ() + d2, item);
                    entityitem.setPickupDelay(10);
                    world.spawnEntity(entityitem);
                }
            }
        }

        world.setBlockToAir(pos);
    }
    
    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    	return BlockFaceShape.UNDEFINED;
    }
    
    @Override
    public boolean isBlockNormalCube(IBlockState state) {
    	return false;
    }
    
	@Override
	public boolean isNormalCube(IBlockState state) {
		return false;
	}
    
    @Override
    public boolean isFullBlock(IBlockState state)    {
    	return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }
}

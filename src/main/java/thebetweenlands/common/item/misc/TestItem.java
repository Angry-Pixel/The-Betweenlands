package thebetweenlands.common.item.misc;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.world.gen.dungeon.Test;
import thebetweenlands.common.world.gen.dungeon.layout.grid.Cell;
import thebetweenlands.common.world.gen.dungeon.layout.grid.Connector;
import thebetweenlands.common.world.gen.dungeon.layout.grid.Grid;
import thebetweenlands.common.world.gen.dungeon.layout.grid.Link;
import thebetweenlands.common.world.gen.dungeon.layout.grid.Link.Bound;
import thebetweenlands.common.world.gen.dungeon.layout.grid.Link.Constraint;
import thebetweenlands.common.world.gen.dungeon.layout.pathfinder.PathfinderLinkMeta;


public class TestItem extends Item {


	public TestItem() {
		this.setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		if(worldIn.isRemote && handIn == EnumHand.MAIN_HAND) {
			try {
				if(playerIn.isSneaking()) {
					long start = System.currentTimeMillis();
					int runs = 1;
					for(int i = 0; i < runs; i++) {
						Test.TEST.init();
					}
					System.out.println("Time: " + ((System.currentTimeMillis() - start) / runs) + "ms, Cells: " + Test.TEST.grid.getCells().size());
				} else {
					long start = System.currentTimeMillis();
					int runs = 1;
					for(int i = 0; i < runs; i++) {
						//Test.TEST.init();
						Test.TEST.step();
					}
					System.out.println("Time: " + ((System.currentTimeMillis() - start) / runs) + "ms, Cells: " + Test.TEST.grid.getCells().size());
				}
				//Test.TEST.graph();
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			playerIn.swingArm(handIn);
		}
		/*if(!worldIn.isRemote && handIn == EnumHand.MAIN_HAND) {
			try {
				Test.TEST.init();
				for(int i = 0; i < 100; i++) {
					Test.TEST.step();
				}

				Grid grid = Test.TEST.grid;
				if(grid != null) {
					final int xo = 0;
					final int yo = 10;
					final int zo = 0;
					final int unit = 1;

					for(int x = 0; x < grid.getWidth(); x++) {
						for(int y = 0; y < grid.getHeight(); y++) {
							for(int z = 0; z < grid.getDepth(); z++) {
								Cell cell = grid.getCell(x, y, z);

								if(cell != null) {
									for(int bx = cell.getTileX() * unit; bx < (cell.getTileX() + cell.getTileSizeX()) * unit; bx++) {
										for(int by = cell.getTileY() * unit; by < (cell.getTileY() + cell.getTileSizeY()) * unit; by++) {
											for(int bz = cell.getTileZ() * unit; bz < (cell.getTileZ() + cell.getTileSizeZ()) * unit; bz++) {
												int mx = bx == cell.getTileX() * unit || bx == (cell.getTileX() + cell.getTileSizeX()) * unit - 1 ? 1 : 0;
												int my = by == cell.getTileY() * unit || by == (cell.getTileY() + cell.getTileSizeY()) * unit - 1 ? 1 : 0;
												int mz = bz == cell.getTileZ() * unit || bz == (cell.getTileZ() + cell.getTileSizeZ()) * unit - 1 ? 1 : 0;

												if(mx + my + mz >= 2) {
													worldIn.setBlockState(new BlockPos(xo + bx, yo + by, zo + bz), Blocks.STONE.getDefaultState(), 2);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			playerIn.swingArm(handIn);
		}*/
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void renderWorld(RenderWorldLastEvent event) {
		for(ItemStack stack : Minecraft.getMinecraft().player.getHeldEquipment()) {
			if(!stack.isEmpty() && stack.getItem() instanceof TestItem) {
				GlStateManager.pushMatrix();

				GlStateManager.disableTexture2D();
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
				GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0f);
				GlStateManager.color(1, 1, 1, 1);

				GL11.glEnable(GL11.GL_LINE_SMOOTH);

				GlStateManager.doPolygonOffset(-0.1F, -10.0F);
				GlStateManager.enablePolygonOffset();

				RenderManager rm = Minecraft.getMinecraft().getRenderManager();

				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder buffer = tessellator.getBuffer();

				double xo = 0;
				double yo = 5;
				double zo = 0;

				double scale = 0.1D;

				try {
					Grid grid = Test.TEST.grid;

					if(grid != null) {
						GlStateManager.depthMask(false);

						GlStateManager.glLineWidth(1.0f);

						float lineOffset = 0.001f;

						for(Cell cell : grid.getCells()) {
							double scaleX = scale * cell.getTileSizeX();
							double scaleY = scale * cell.getTileSizeY();
							double scaleZ = scale * cell.getTileSizeZ();

							AxisAlignedBB aabb = new AxisAlignedBB(xo + cell.getTileX() * scale - rm.viewerPosX - lineOffset, yo + cell.getTileY() * scale - rm.viewerPosY - lineOffset, zo + cell.getTileZ() * scale - rm.viewerPosZ - lineOffset, xo + cell.getTileX() * scale - rm.viewerPosX + scaleX + lineOffset, yo + cell.getTileY() * scale - rm.viewerPosY + scaleY + lineOffset, zo + cell.getTileZ() * scale - rm.viewerPosZ + scaleZ + lineOffset);
							RenderGlobal.drawSelectionBoundingBox(aabb, 0.0f, 0.0f, 0.0f, 0.15f);
						}

						for(Link link : grid.getLinks()) {
							for(Bound aabb : link.getBounds()) {
								double minX = aabb.minX * scale;
								double minY = aabb.minY * scale;
								double minZ = aabb.minZ * scale;
								double maxX = (aabb.maxX + 1) * scale;
								double maxY = (aabb.maxY + 1) * scale;
								double maxZ = (aabb.maxZ + 1) * scale;

								RenderGlobal.drawSelectionBoundingBox(new AxisAlignedBB(xo + minX - rm.viewerPosX - lineOffset, yo + minY - rm.viewerPosY - lineOffset, zo + minZ - rm.viewerPosZ - lineOffset, xo + maxX - rm.viewerPosX + lineOffset, yo + maxY - rm.viewerPosY + lineOffset, zo + maxZ - rm.viewerPosZ + lineOffset), 0.0f, 0.0f, 0.0f, 0.15f);
							}
						}

						GlStateManager.depthMask(true);

						GlStateManager.glLineWidth(2.0f);

						buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

						for(Link link : grid.getLinks()) {
							PathfinderLinkMeta meta = link.getMeta(Test.TEST.pathfinder);

							if(meta != null) {
								for(PathfinderLinkMeta.Piece piece : meta.pieces) {
									renderPathPiece(xo - rm.viewerPosX, yo - rm.viewerPosY, zo - rm.viewerPosZ, scale, piece, buffer, 0, 1, 0, 1);
								}
							}
						}

						tessellator.draw();

						buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);

						float r;
						float g;
						float b;
						float a;

						for(Link link : grid.getLinks()) {
							buffer.setTranslation(xo - rm.viewerPosX, yo - rm.viewerPosY, zo - rm.viewerPosZ);

							r = 1.0f;
							g = 0.8f;
							b = 0.3f;
							a = 0.6f;

							PathfinderLinkMeta meta = link.getMeta(Test.TEST.pathfinder);

							if(meta != null) {
								a = 0.0f;
							}

							if(link.isColliding(-1)) {
								g = 0.3f;
								b = 0.1f;
								a = 0.8f;
							}
							if(link.getConstraint(null)) {
								g = 0.3f;
								b = 0.6f;
								a = 0.8f;
							}

							if(link == Test.invalidLink) {
								r = 0;
								g = 1;
								b = 1;
								a = 1;
							}

							for(Bound aabb : link.getBounds()) {
								double minX = aabb.minX * scale;
								double minY = aabb.minY * scale;
								double minZ = aabb.minZ * scale;
								double maxX = (aabb.maxX + 1) * scale;
								double maxY = (aabb.maxY + 1) * scale;
								double maxZ = (aabb.maxZ + 1) * scale;

								buffer.pos(minX, minY, maxZ).tex(0, 0).color(r, g, b, a).normal(0, 0, 1).endVertex();
								buffer.pos(maxX, minY, maxZ).tex(1, 0).color(r, g, b, a).normal(0, 0, 1).endVertex();
								buffer.pos(maxX, maxY, maxZ).tex(1, 1).color(r, g, b, a).normal(0, 0, 1).endVertex();
								buffer.pos(minX, maxY, maxZ).tex(0, 1).color(r, g, b, a).normal(0, 0, 1).endVertex();

								buffer.pos(minX, minY, minZ).tex(0, 0).color(r, g, b, a).normal(0, 0, -1).endVertex();
								buffer.pos(minX, maxY, minZ).tex(0, 1).color(r, g, b, a).normal(0, 0, -1).endVertex();
								buffer.pos(maxX, maxY, minZ).tex(1, 1).color(r, g, b, a).normal(0, 0, -1).endVertex();
								buffer.pos(maxX, minY, minZ).tex(1, 0).color(r, g, b, a).normal(0, 0, -1).endVertex();

								buffer.pos(minX, minY, minZ).tex(0, 0).color(r, g, b, a).normal(-1, 0, 0).endVertex();
								buffer.pos(minX, minY, maxZ).tex(1, 0).color(r, g, b, a).normal(-1, 0, 0).endVertex();
								buffer.pos(minX, maxY, maxZ).tex(1, 1).color(r, g, b, a).normal(-1, 0, 0).endVertex();
								buffer.pos(minX, maxY, minZ).tex(0, 1).color(r, g, b, a).normal(-1, 0, 0).endVertex();

								buffer.pos(maxX, minY, minZ).tex(0, 0).color(r, g, b, a).normal(1, 0, 0).endVertex();
								buffer.pos(maxX, maxY, minZ).tex(0, 1).color(r, g, b, a).normal(1, 0, 0).endVertex();
								buffer.pos(maxX, maxY, maxZ).tex(1, 1).color(r, g, b, a).normal(1, 0, 0).endVertex();
								buffer.pos(maxX, minY, maxZ).tex(1, 0).color(r, g, b, a).normal(1, 0, 0).endVertex();

								buffer.pos(minX, maxY, minZ).tex(0, 0).color(r, g, b, a).normal(0, 1, 0).endVertex();
								buffer.pos(minX, maxY, maxZ).tex(0, 1).color(r, g, b, a).normal(0, 1, 0).endVertex();
								buffer.pos(maxX, maxY, maxZ).tex(1, 1).color(r, g, b, a).normal(0, 1, 0).endVertex();
								buffer.pos(maxX, maxY, minZ).tex(1, 0).color(r, g, b, a).normal(0, 1, 0).endVertex();

								buffer.pos(minX, minY, minZ).tex(0, 0).color(r, g, b, a).normal(0, -1, 0).endVertex();
								buffer.pos(maxX, minY, minZ).tex(1, 0).color(r, g, b, a).normal(0, -1, 0).endVertex();
								buffer.pos(maxX, minY, maxZ).tex(1, 1).color(r, g, b, a).normal(0, -1, 0).endVertex();
								buffer.pos(minX, minY, maxZ).tex(0, 1).color(r, g, b, a).normal(0, -1, 0).endVertex();
							}

							if(meta != null) {
								r = 0.5f;
								g = 1.0f;
								b = 0.3f;
								a = 0.25f;

								for(PathfinderLinkMeta.Piece piece : meta.pieces) {
									int sx = piece.x + Math.min(piece.outdx * (piece.length - 1), 0);
									int sy = piece.y;
									int sz = piece.z + Math.min(piece.outdz * (piece.length - 1), 0);
									int ex = piece.x + Math.max(piece.outdx * (piece.length - 1), 0);
									int ey = piece.y + (piece.shape == PathfinderLinkMeta.Shape.STAIRS ? 1 : 0);
									int ez = piece.z + Math.max(piece.outdz * (piece.length - 1), 0);

									double minX = sx * scale - 0.001f;
									double minY = sy * scale - 0.001f;
									double minZ = sz * scale - 0.001f;
									double maxX = (ex + 1) * scale + 0.001f;
									double maxY = (ey + 1) * scale + 0.001f;
									double maxZ = (ez + 1) * scale + 0.001f;

									buffer.pos(minX, minY, maxZ).tex(0, 0).color(r, g, b, a).normal(0, 0, 1).endVertex();
									buffer.pos(maxX, minY, maxZ).tex(1, 0).color(r, g, b, a).normal(0, 0, 1).endVertex();
									buffer.pos(maxX, maxY, maxZ).tex(1, 1).color(r, g, b, a).normal(0, 0, 1).endVertex();
									buffer.pos(minX, maxY, maxZ).tex(0, 1).color(r, g, b, a).normal(0, 0, 1).endVertex();

									buffer.pos(minX, minY, minZ).tex(0, 0).color(r, g, b, a).normal(0, 0, -1).endVertex();
									buffer.pos(minX, maxY, minZ).tex(0, 1).color(r, g, b, a).normal(0, 0, -1).endVertex();
									buffer.pos(maxX, maxY, minZ).tex(1, 1).color(r, g, b, a).normal(0, 0, -1).endVertex();
									buffer.pos(maxX, minY, minZ).tex(1, 0).color(r, g, b, a).normal(0, 0, -1).endVertex();

									buffer.pos(minX, minY, minZ).tex(0, 0).color(r, g, b, a).normal(-1, 0, 0).endVertex();
									buffer.pos(minX, minY, maxZ).tex(1, 0).color(r, g, b, a).normal(-1, 0, 0).endVertex();
									buffer.pos(minX, maxY, maxZ).tex(1, 1).color(r, g, b, a).normal(-1, 0, 0).endVertex();
									buffer.pos(minX, maxY, minZ).tex(0, 1).color(r, g, b, a).normal(-1, 0, 0).endVertex();

									buffer.pos(maxX, minY, minZ).tex(0, 0).color(r, g, b, a).normal(1, 0, 0).endVertex();
									buffer.pos(maxX, maxY, minZ).tex(0, 1).color(r, g, b, a).normal(1, 0, 0).endVertex();
									buffer.pos(maxX, maxY, maxZ).tex(1, 1).color(r, g, b, a).normal(1, 0, 0).endVertex();
									buffer.pos(maxX, minY, maxZ).tex(1, 0).color(r, g, b, a).normal(1, 0, 0).endVertex();

									buffer.pos(minX, maxY, minZ).tex(0, 0).color(r, g, b, a).normal(0, 1, 0).endVertex();
									buffer.pos(minX, maxY, maxZ).tex(0, 1).color(r, g, b, a).normal(0, 1, 0).endVertex();
									buffer.pos(maxX, maxY, maxZ).tex(1, 1).color(r, g, b, a).normal(0, 1, 0).endVertex();
									buffer.pos(maxX, maxY, minZ).tex(1, 0).color(r, g, b, a).normal(0, 1, 0).endVertex();

									buffer.pos(minX, minY, minZ).tex(0, 0).color(r, g, b, a).normal(0, -1, 0).endVertex();
									buffer.pos(maxX, minY, minZ).tex(1, 0).color(r, g, b, a).normal(0, -1, 0).endVertex();
									buffer.pos(maxX, minY, maxZ).tex(1, 1).color(r, g, b, a).normal(0, -1, 0).endVertex();
									buffer.pos(minX, minY, maxZ).tex(0, 1).color(r, g, b, a).normal(0, -1, 0).endVertex();
								}
							}
						}

						for(Cell cell : grid.getCells()) {
							if(cell != null) {
								double scaleX = scale * cell.getTileSizeX();
								double scaleY = scale * cell.getTileSizeY();
								double scaleZ = scale * cell.getTileSizeZ();

								for(Connector connector : cell.getTile().getConnectors()) {
									buffer.setTranslation(xo + connector.getTileX() * scale - rm.viewerPosX, yo + connector.getTileY() * scale - rm.viewerPosY, zo + connector.getTileZ() * scale - rm.viewerPosZ);

									float boxOffset = 0.0008f;

									r = 1.0f;
									g = 0.3f;
									b = 0.5f;
									a = 0.5f;

									for(Link link : grid.getLinks()) {
										if(link.getEnd() == connector) {
											r = 0.3f;
											g = 1.0f;
											b = 0.5f;
											a = 0.5f;
											break;
										}
									}

									buffer.pos(-boxOffset, -boxOffset, scale+boxOffset).tex(0, 0).color(r, g, b, a).normal(0, 0, 1).endVertex();
									buffer.pos(scale+boxOffset, -boxOffset, scale+boxOffset).tex(1, 0).color(r, g, b, a).normal(0, 0, 1).endVertex();
									buffer.pos(scale+boxOffset, scale+boxOffset, scale+boxOffset).tex(1, 1).color(r, g, b, a).normal(0, 0, 1).endVertex();
									buffer.pos(-boxOffset, scale+boxOffset, scale+boxOffset).tex(0, 1).color(r, g, b, a).normal(0, 0, 1).endVertex();

									buffer.pos(-boxOffset, -boxOffset, -boxOffset).tex(0, 0).color(r, g, b, a).normal(0, 0, -1).endVertex();
									buffer.pos(-boxOffset, scale+boxOffset, -boxOffset).tex(0, 1).color(r, g, b, a).normal(0, 0, -1).endVertex();
									buffer.pos(scale+boxOffset, scale+boxOffset, -boxOffset).tex(1, 1).color(r, g, b, a).normal(0, 0, -1).endVertex();
									buffer.pos(scale+boxOffset, -boxOffset, -boxOffset).tex(1, 0).color(r, g, b, a).normal(0, 0, -1).endVertex();

									buffer.pos(-boxOffset, -boxOffset, -boxOffset).tex(0, 0).color(r, g, b, a).normal(-1, 0, 0).endVertex();
									buffer.pos(-boxOffset,-boxOffset, scale+boxOffset).tex(1, 0).color(r, g, b, a).normal(-1, 0, 0).endVertex();
									buffer.pos(-boxOffset, scale+boxOffset, scale+boxOffset).tex(1, 1).color(r, g, b, a).normal(-1, 0, 0).endVertex();
									buffer.pos(-boxOffset, scale+boxOffset, -boxOffset).tex(0, 1).color(r, g, b, a).normal(-1, 0, 0).endVertex();

									buffer.pos(scale+boxOffset, -boxOffset, -boxOffset).tex(0, 0).color(r, g, b, a).normal(1, 0, 0).endVertex();
									buffer.pos(scale+boxOffset, scale+boxOffset, -boxOffset).tex(0, 1).color(r, g, b, a).normal(1, 0, 0).endVertex();
									buffer.pos(scale+boxOffset, scale+boxOffset, scale+boxOffset).tex(1, 1).color(r, g, b, a).normal(1, 0, 0).endVertex();
									buffer.pos(scale+boxOffset, -boxOffset, scale+boxOffset).tex(1, 0).color(r, g, b, a).normal(1, 0, 0).endVertex();

									buffer.pos(-boxOffset, scale+boxOffset, -boxOffset).tex(0, 0).color(r, g, b, a).normal(0, 1, 0).endVertex();
									buffer.pos(-boxOffset, scale+boxOffset, scale+boxOffset).tex(0, 1).color(r, g, b, a).normal(0, 1, 0).endVertex();
									buffer.pos(scale+boxOffset, scale+boxOffset, scale+boxOffset).tex(1, 1).color(r, g, b, a).normal(0, 1, 0).endVertex();
									buffer.pos(scale+boxOffset, scale+boxOffset, -boxOffset).tex(1, 0).color(r, g, b, a).normal(0, 1, 0).endVertex();

									buffer.pos(-boxOffset, -boxOffset, -boxOffset).tex(0, 0).color(r, g, b, a).normal(0, -1, 0).endVertex();
									buffer.pos(scale+boxOffset, -boxOffset, -boxOffset).tex(1, 0).color(r, g, b, a).normal(0, -1, 0).endVertex();
									buffer.pos(scale+boxOffset, -boxOffset, scale+boxOffset).tex(1, 1).color(r, g, b, a).normal(0, -1, 0).endVertex();
									buffer.pos(-boxOffset, -boxOffset, scale+boxOffset).tex(0, 1).color(r, g, b, a).normal(0, -1, 0).endVertex();
								}

								buffer.setTranslation(xo + cell.getTileX() * scale - rm.viewerPosX, yo + cell.getTileY() * scale - rm.viewerPosY, zo + cell.getTileZ() * scale - rm.viewerPosZ);

								r = 0.3f;
								g = 0.3f;
								b = 1.0f;
								a = 0.5f;

								/*if(cell.getTileX() < 0 || cell.getTileY() < 0 || cell.getTileZ() < 0) {
									r = 1.0f;
									g = 0.0f;
									b = 0.0f;
								}*/
								
								/*if(cell.getTile().getTag((int)((System.currentTimeMillis() / 500) % 200L))) {
									r = 0.0f;
									g = 1.0f;
									b = 0.0f;
								}*/

								buffer.pos(0, 0, scaleZ).tex(0, 0).color(r, g, b, a).normal(0, 0, 1).endVertex();
								buffer.pos(scaleX, 0, scaleZ).tex(1, 0).color(r, g, b, a).normal(0, 0, 1).endVertex();
								buffer.pos(scaleX, scaleY, scaleZ).tex(1, 1).color(r, g, b, a).normal(0, 0, 1).endVertex();
								buffer.pos(0, scaleY, scaleZ).tex(0, 1).color(r, g, b, a).normal(0, 0, 1).endVertex();

								buffer.pos(0, 0, 0).tex(0, 0).color(r, g, b, a).normal(0, 0, -1).endVertex();
								buffer.pos(0, scaleY, 0).tex(0, 1).color(r, g, b, a).normal(0, 0, -1).endVertex();
								buffer.pos(scaleX, scaleY, 0).tex(1, 1).color(r, g, b, a).normal(0, 0, -1).endVertex();
								buffer.pos(scaleX, 0, 0).tex(1, 0).color(r, g, b, a).normal(0, 0, -1).endVertex();

								buffer.pos(0, 0, 0).tex(0, 0).color(r, g, b, a).normal(-1, 0, 0).endVertex();
								buffer.pos(0, 0, scaleZ).tex(1, 0).color(r, g, b, a).normal(-1, 0, 0).endVertex();
								buffer.pos(0, scaleY, scaleZ).tex(1, 1).color(r, g, b, a).normal(-1, 0, 0).endVertex();
								buffer.pos(0, scaleY, 0).tex(0, 1).color(r, g, b, a).normal(-1, 0, 0).endVertex();

								buffer.pos(scaleX, 0, 0).tex(0, 0).color(r, g, b, a).normal(1, 0, 0).endVertex();
								buffer.pos(scaleX, scaleY, 0).tex(0, 1).color(r, g, b, a).normal(1, 0, 0).endVertex();
								buffer.pos(scaleX, scaleY, scaleZ).tex(1, 1).color(r, g, b, a).normal(1, 0, 0).endVertex();
								buffer.pos(scaleX, 0, scaleZ).tex(1, 0).color(r, g, b, a).normal(1, 0, 0).endVertex();

								buffer.pos(0, scaleY, 0).tex(0, 0).color(r, g, b, a).normal(0, 1, 0).endVertex();
								buffer.pos(0, scaleY, scaleZ).tex(0, 1).color(r, g, b, a).normal(0, 1, 0).endVertex();
								buffer.pos(scaleX, scaleY, scaleZ).tex(1, 1).color(r, g, b, a).normal(0, 1, 0).endVertex();
								buffer.pos(scaleX, scaleY, 0).tex(1, 0).color(r, g, b, a).normal(0, 1, 0).endVertex();

								buffer.pos(0, 0, 0).tex(0, 0).color(r, g, b, a).normal(0, -1, 0).endVertex();
								buffer.pos(scaleX, 0, 0).tex(1, 0).color(r, g, b, a).normal(0, -1, 0).endVertex();
								buffer.pos(scaleX, 0, scaleZ).tex(1, 1).color(r, g, b, a).normal(0, -1, 0).endVertex();
								buffer.pos(0, 0, scaleZ).tex(0, 1).color(r, g, b, a).normal(0, -1, 0).endVertex();
							}
						}

						tessellator.draw();
						buffer.setTranslation(0, 0, 0);

						GlStateManager.glLineWidth(1.5f);

						for(Cell cell : grid.getCells()) {
							double scaleX = scale * cell.getTileSizeX();
							double scaleY = scale * cell.getTileSizeY();
							double scaleZ = scale * cell.getTileSizeZ();

							AxisAlignedBB aabb = new AxisAlignedBB(xo + cell.getTileX() * scale - rm.viewerPosX - lineOffset, yo + cell.getTileY() * scale - rm.viewerPosY - lineOffset, zo + cell.getTileZ() * scale - rm.viewerPosZ - lineOffset, xo + cell.getTileX() * scale - rm.viewerPosX + scaleX + lineOffset, yo + cell.getTileY() * scale - rm.viewerPosY + scaleY + lineOffset, zo + cell.getTileZ() * scale - rm.viewerPosZ + scaleZ + lineOffset);

							RenderGlobal.drawSelectionBoundingBox(aabb, 0.0f, 0.0f, 0.0f, 0.25f);
						}

						for(Link link : grid.getLinks()) {
							for(Bound aabb : link.getBounds()) {
								double minX = aabb.minX * scale;
								double minY = aabb.minY * scale;
								double minZ = aabb.minZ * scale;
								double maxX = (aabb.maxX + 1) * scale;
								double maxY = (aabb.maxY + 1) * scale;
								double maxZ = (aabb.maxZ + 1) * scale;

								RenderGlobal.drawSelectionBoundingBox(new AxisAlignedBB(xo + minX - rm.viewerPosX - lineOffset, yo + minY - rm.viewerPosY - lineOffset, zo + minZ - rm.viewerPosZ - lineOffset, xo + maxX - rm.viewerPosX + lineOffset, yo + maxY - rm.viewerPosY + lineOffset, zo + maxZ - rm.viewerPosZ + lineOffset), 0.0f, 0.0f, 0.0f, 0.25f);
							}
						}

						for(Cell cell : grid.getCells()) {
							if(!cell.isTileDetached()) {
								double scaleX = scale * cell.getSizeX();
								double scaleY = scale * cell.getSizeY();
								double scaleZ = scale * cell.getSizeZ();
								AxisAlignedBB aabb = new AxisAlignedBB(xo + cell.getCellTileX() * scale - rm.viewerPosX, yo + cell.getCellTileY() * scale - rm.viewerPosY, zo + cell.getCellTileZ() * scale - rm.viewerPosZ, xo + cell.getCellTileX() * scale - rm.viewerPosX + scaleX, yo + cell.getCellTileY() * scale - rm.viewerPosY + scaleY, zo + cell.getCellTileZ() * scale - rm.viewerPosZ + scaleZ);

								r = 0.0f;
								g = 0.0f;
								b = 0.0f;
								a = 0.4f;
								GlStateManager.glLineWidth(1.0f);

								for(Connector connector : cell.getTile().getConnectors()) {
									for(Link link : grid.getLinks()) {
										if(link.getStart() == connector || link.getEnd() == connector) {
											if(link.getConstraint(null)) {
												r = 1.0f;
												g = 0.3f;
												b = 0.6f;
												a = 0.75f;
												GlStateManager.glLineWidth(3.0f);
											} else if(link.isColliding(-1)) {
												r = 1.0f;
												g = 0.3f;
												b = 0.1f;
												a = 0.75f;
												GlStateManager.glLineWidth(3.0f);
											}
											break;
										}
									}
								}

								RenderGlobal.drawSelectionBoundingBox(aabb, r, g, b, a);
							}
						}

						GlStateManager.glLineWidth(1.0f);

						final int regionSize = grid.getAccelerator().getRegionSize();
						grid.getAccelerator().iterateRegions(region -> {
							double scaleX = scale * regionSize;
							double scaleY = scale * regionSize;
							double scaleZ = scale * regionSize;
							AxisAlignedBB aabb = new AxisAlignedBB(xo + region.getX() * regionSize * scale - rm.viewerPosX, yo + region.getY() * regionSize * scale - rm.viewerPosY, zo + region.getZ() * regionSize * scale - rm.viewerPosZ, xo + region.getX() * regionSize * scale - rm.viewerPosX + scaleX, yo + region.getY() * regionSize * scale - rm.viewerPosY + scaleY, zo + region.getZ() * regionSize * scale - rm.viewerPosZ + scaleZ);
							/*if(region.getTag((int)((System.currentTimeMillis() / 500) % 200L))) {
								RenderGlobal.drawSelectionBoundingBox(aabb, 0.0f, 1.0f, 0.0f, 1.0f);
							}*/
							RenderGlobal.drawSelectionBoundingBox(aabb, 0, 0, 0, 0.25f);
							return false;
						});
					}
				} catch(Exception ex) {
					ex.printStackTrace();
				}


				GlStateManager.glLineWidth(1.0f);
				GL11.glDisable(GL11.GL_LINE_SMOOTH);

				GlStateManager.disablePolygonOffset();

				GlStateManager.color(1, 1, 1, 1);
				GlStateManager.depthMask(true);
				GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);
				GlStateManager.enableTexture2D();
				GlStateManager.enableDepth();
				GlStateManager.disableBlend();

				GlStateManager.popMatrix();
			}
		}
	}

	private static void renderPathPiece(double xo, double yo, double zo, double scale, PathfinderLinkMeta.Piece piece, BufferBuilder buffer, float r, float g, float b, float a) {
		double sx = piece.x + 0.5D - piece.indx * 0.5D;
		double sy = piece.y + 0.5D - piece.indy * 0.5D;
		double sz = piece.z + 0.5D - piece.indz * 0.5D;
		double ex = piece.x + piece.indx * (piece.length - 1) + 0.5D + piece.outdx * 0.5D;
		double ey = piece.y + (piece.shape == PathfinderLinkMeta.Shape.STAIRS ? 1 : 0) + 0.5D + piece.outdy * 0.5D;
		double ez = piece.z + piece.indz * (piece.length - 1) + 0.5D + piece.outdz * 0.5D;

		if(piece.shape == PathfinderLinkMeta.Shape.STAIRS) {
			buffer.pos(xo + sx * scale, yo + sy * scale, zo + sz * scale).color(r, g, b, a).endVertex();
			buffer.pos(xo + ex * scale, yo + ey * scale, zo + ez * scale).color(r, g, b, a).endVertex();
		} else {
			double mx = piece.x + 0.5D;
			double my = piece.y + 0.5D;
			double mz = piece.z + 0.5D;
			buffer.pos(xo + sx * scale, yo + sy * scale, zo + sz * scale).color(r, g, b, a).endVertex();
			buffer.pos(xo + mx * scale, yo + my * scale, zo + mz * scale).color(r, g, b, a).endVertex();
			buffer.pos(xo + mx * scale, yo + my * scale, zo + mz * scale).color(r, g, b, a).endVertex();
			buffer.pos(xo + ex * scale, yo + ey * scale, zo + ez * scale).color(r, g, b, a).endVertex();
		}
	}
}

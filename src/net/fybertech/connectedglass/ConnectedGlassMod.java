package net.fybertech.connectedglass;

import net.fybertech.meddle.MeddleMod;
import net.fybertech.meddleapi.MeddleClient;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

import java.util.Map;

@MeddleMod(author="FyberOptic", depends={"dynamicmappings", "meddleapi"}, id="connectedglass", name="ConnectedGlass", version="6.1-CompileFixed")
public class ConnectedGlassMod
{
	private static final double P = 0.0625D;

	public void init()
	{
		System.out.println("|Meddle Mod Info");
		System.out.println("|Compilation target : Minecraft 1.RV-pre1 based Minecraft 1.9.1-pre1");
		System.out.println("|Compilation target : Meddle 1.3; DynamicMapping 023; MeddleAPI 1.0.6");
		System.out.println("|Auth: FyberOptic, Syphlix Oauthes");
		System.out.println("|Package Name : Connected Glass");
		System.out.println("|Package Version : 1.0");
		System.out.println("|Package Third-Party Update Name : Connected Glass X");
		System.out.println("|Package Third-Party Version : 1.1");
		System.out.println("|Package Third-Party Build : 11");

		registerBlockRenderer("minecraft:glass");
		registerBlockRenderer("minecraft:stained_glass");

		registerPaneRenderer("minecraft:glass_pane");
		registerPaneRenderer("minecraft:stained_glass_pane");

		registerSlabRenderer("minecraft:stone_slab");
		registerBlockRenderer("minecraft:double_stone_slab");
	}

	private void registerBlockRenderer(String name) {
		Block block = Block.getBlockFromName(name);
		if(block != null) MeddleClient.registerCustomBlockRenderer(block, new BlockRenderer());
	}

	private void registerPaneRenderer(String name) {
		Block block = Block.getBlockFromName(name);
		if(block != null) MeddleClient.registerCustomBlockRenderer(block, new PaneRenderer());
	}

	private void registerSlabRenderer(String name) {
		Block block = Block.getBlockFromName(name);
		if(block != null) MeddleClient.registerCustomBlockRenderer(block, new SlabRenderer());
	}

	public class BlockRenderer implements MeddleClient.ICustomBlockRenderer
	{
		@Override
		public boolean renderBlock(IBlockState state, BlockPos pos, IBlockAccess world, WorldRenderer renderer)
		{
			TextureAtlasSprite sprite = getSprite(state, EnumFacing.NORTH);
			if (sprite == null) return false;

			int brightness = state.getBlock().getMixedBrightnessForBlock(state, world, pos);

			boolean up = canConnect(world, pos, pos.up(), EnumFacing.UP);
			boolean down = canConnect(world, pos, pos.down(), EnumFacing.DOWN);
			boolean north = canConnect(world, pos, pos.north(), EnumFacing.NORTH);
			boolean south = canConnect(world, pos, pos.south(), EnumFacing.SOUTH);
			boolean west = canConnect(world, pos, pos.west(), EnumFacing.WEST);
			boolean east = canConnect(world, pos, pos.east(), EnumFacing.EAST);

			double x = pos.getX(); double y = pos.getY(); double z = pos.getZ();

			// North Face (Z=0)
			if (!north) {
				double u1 = east ? P : 0; double u2 = west ? 1-P : 1;
				double v1 = up   ? P : 0; double v2 = down ? 1-P : 1;
				addQuad(renderer, x+1, y+1, z, x+1, y, z, x, y, z, x, y+1, z, u1, u2, v1, v2, sprite, brightness);
			}
			// South Face (Z=1)
			if (!south) {
				double u1 = west ? P : 0; double u2 = east ? 1-P : 1;
				double v1 = up   ? P : 0; double v2 = down ? 1-P : 1;
				addQuad(renderer, x, y+1, z+1, x, y, z+1, x+1, y, z+1, x+1, y+1, z+1, u1, u2, v1, v2, sprite, brightness);
			}
			// West Face (X=0)
			if (!west) {
				double u1 = north ? P : 0; double u2 = south ? 1-P : 1;
				double v1 = up    ? P : 0; double v2 = down  ? 1-P : 1;
				addQuad(renderer, x, y+1, z, x, y, z, x, y, z+1, x, y+1, z+1, u1, u2, v1, v2, sprite, brightness);
			}
			// East Face (X=1)
			if (!east) {
				double u1 = south ? P : 0; double u2 = north ? 1-P : 1;
				double v1 = up    ? P : 0; double v2 = down  ? 1-P : 1;
				addQuad(renderer, x+1, y+1, z+1, x+1, y, z+1, x+1, y, z, x+1, y+1, z, u1, u2, v1, v2, sprite, brightness);
			}

			sprite = getSprite(state, EnumFacing.UP);

			// Top Face (Y=1)
			if (!up) {
				double u1 = west  ? P : 0; double u2 = east  ? 1-P : 1;
				double v1 = north ? P : 0; double v2 = south ? 1-P : 1;
				addQuad(renderer, x, y+1, z, x, y+1, z+1, x+1, y+1, z+1, x+1, y+1, z, u1, u2, v1, v2, sprite, brightness);
			}
			// Bottom Face (Y=0)
			if (!down) {
				double u1 = west  ? P : 0; double u2 = east  ? 1-P : 1;
				double v1 = south ? P : 0; double v2 = north ? 1-P : 1;
				addQuad(renderer, x, y, z+1, x, y, z, x+1, y, z, x+1, y, z+1, u1, u2, v1, v2, sprite, brightness);
			}
			return true;
		}
	}

	public class PaneRenderer implements MeddleClient.ICustomBlockRenderer
	{
		@Override
		public boolean renderBlock(IBlockState state, BlockPos pos, IBlockAccess world, WorldRenderer renderer)
		{
			Block block = state.getBlock();
			state = block.getActualState(state, world, pos);
			int brightness = block.getMixedBrightnessForBlock(state, world, pos);
			TextureAtlasSprite sprite = getSprite(state, EnumFacing.NORTH);
			TextureAtlasSprite spriteTop = getSprite(state, EnumFacing.UP);
			if (sprite == null) return false;

			boolean n = state.getValue(BlockPane.NORTH);
			boolean s = state.getValue(BlockPane.SOUTH);
			boolean w = state.getValue(BlockPane.WEST);
			boolean e = state.getValue(BlockPane.EAST);

			boolean glassUp = isSameBlock(world, pos.up(), state);
			boolean glassDown = isSameBlock(world, pos.down(), state);

			if (!n && !s && !w && !e) n = s = w = e = true;

			double x = pos.getX(); double y = pos.getY(); double z = pos.getZ();
			double min = 7*P; double max = 9*P;
			double vStart = glassUp ? P : 0;
			double vEnd = glassDown ? 1-P : 1;

			// Post
			addQuad(renderer, x+max, y+1, z+min, x+max, y, z+min, x+min, y, z+min, x+min, y+1, z+min, min, max, vStart, vEnd, sprite, brightness);
			addQuad(renderer, x+min, y+1, z+max, x+min, y, z+max, x+max, y, z+max, x+max, y+1, z+max, min, max, vStart, vEnd, sprite, brightness);
			addQuad(renderer, x+min, y+1, z+min, x+min, y, z+min, x+min, y, z+max, x+min, y+1, z+max, min, max, vStart, vEnd, sprite, brightness);
			addQuad(renderer, x+max, y+1, z+max, x+max, y, z+max, x+max, y, z+min, x+max, y+1, z+min, min, max, vStart, vEnd, sprite, brightness);

			// Arms
			if (n) {
				addQuad(renderer, x+min, y+1, z, x+min, y, z, x+min, y, z+min, x+min, y+1, z+min, P, min, vStart, vEnd, sprite, brightness);
				addQuad(renderer, x+max, y+1, z+min, x+max, y, z+min, x+max, y, z, x+max, y+1, z, P, min, vStart, vEnd, sprite, brightness);
			}
			if (s) {
				addQuad(renderer, x+min, y+1, z+max, x+min, y, z+max, x+min, y, z+1, x+min, y+1, z+1, max, 1-P, vStart, vEnd, sprite, brightness);
				addQuad(renderer, x+max, y+1, z+1, x+max, y, z+1, x+max, y, z+max, x+max, y+1, z+max, max, 1-P, vStart, vEnd, sprite, brightness);
			}
			if (w) {
				addQuad(renderer, x+min, y+1, z+min, x+min, y, z+min, x, y, z+min, x, y+1, z+min, P, min, vStart, vEnd, sprite, brightness);
				addQuad(renderer, x, y+1, z+max, x, y, z+max, x+min, y, z+max, x+min, y+1, z+max, P, min, vStart, vEnd, sprite, brightness);
			}
			if (e) {
				addQuad(renderer, x+1, y+1, z+min, x+1, y, z+min, x+max, y, z+min, x+max, y+1, z+min, max, 1-P, vStart, vEnd, sprite, brightness);
				addQuad(renderer, x+max, y+1, z+max, x+max, y, z+max, x+1, y, z+max, x+1, y+1, z+max, max, 1-P, vStart, vEnd, sprite, brightness);
			}

			if (!glassUp) renderTopBottom(renderer, x, y, z, n, s, w, e, min, max, spriteTop, brightness, true);
			if (!glassDown) renderTopBottom(renderer, x, y, z, n, s, w, e, min, max, spriteTop, brightness, false);

			return true;
		}

		private void renderTopBottom(WorldRenderer renderer, double x, double y, double z, boolean n, boolean s, boolean w, boolean e, double min, double max, TextureAtlasSprite sprite, int brightness, boolean isTop) {
			float yPos = isTop ? (float)y + 1 : (float)y;
			if(isTop) addQuad(renderer, x+min, yPos, z+min, x+min, yPos, z+max, x+max, yPos, z+max, x+max, yPos, z+min, min, max, min, max, sprite, brightness);
			else      addQuad(renderer, x+min, yPos, z+max, x+min, yPos, z+min, x+max, yPos, z+min, x+max, yPos, z+max, min, max, min, max, sprite, brightness);

			if(isTop) {
				if(n) addQuad(renderer, x+min, yPos, z, x+min, yPos, z+min, x+max, yPos, z+min, x+max, yPos, z, min, max, 0, min, sprite, brightness);
				if(s) addQuad(renderer, x+min, yPos, z+max, x+min, yPos, z+1, x+max, yPos, z+1, x+max, yPos, z+max, min, max, max, 1, sprite, brightness);
				if(w) addQuad(renderer, x, yPos, z+min, x, yPos, z+max, x+min, yPos, z+max, x+min, yPos, z+min, 0, min, min, max, sprite, brightness);
				if(e) addQuad(renderer, x+max, yPos, z+min, x+max, yPos, z+max, x+1, yPos, z+max, x+1, yPos, z+min, max, 1, min, max, sprite, brightness);
			} else {
				if(n) addQuad(renderer, x+min, yPos, z+min, x+min, yPos, z, x+max, yPos, z, x+max, yPos, z+min, min, max, 0, min, sprite, brightness);
				if(s) addQuad(renderer, x+min, yPos, z+1, x+min, yPos, z+max, x+max, yPos, z+max, x+max, yPos, z+1, min, max, max, 1, sprite, brightness);
				if(w) addQuad(renderer, x+min, yPos, z+max, x+min, yPos, z+min, x, yPos, z+min, x, y, z+max, 0, min, min, max, sprite, brightness);
				if(e) addQuad(renderer, x+1, yPos, z+max, x+1, yPos, z+min, x+max, yPos, z+min, x+max, yPos, z+max, max, 1, min, max, sprite, brightness);
			}
		}
	}

	public class SlabRenderer implements MeddleClient.ICustomBlockRenderer
	{
		@Override
		public boolean renderBlock(IBlockState state, BlockPos pos, IBlockAccess world, WorldRenderer renderer)
		{
			Block block = state.getBlock();
			int brightness = block.getMixedBrightnessForBlock(state, world, pos);
			TextureAtlasSprite spriteSide = getSprite(state, EnumFacing.NORTH);
			TextureAtlasSprite spriteTop = getSprite(state, EnumFacing.UP);

			if (spriteSide == null || spriteTop == null) return false;

			boolean isTop = isSlabTop(state);
			double yMin = isTop ? 0.5 : 0.0;
			double yMax = isTop ? 1.0 : 0.5;

			boolean north = canConnectSlab(world, pos, pos.north(), state, isTop);
			boolean south = canConnectSlab(world, pos, pos.south(), state, isTop);
			boolean west = canConnectSlab(world, pos, pos.west(), state, isTop);
			boolean east = canConnectSlab(world, pos, pos.east(), state, isTop);

			double x = pos.getX(); double y = pos.getY(); double z = pos.getZ();

			if (!north) {
				double u1 = east ? P : 0; double u2 = west ? 1-P : 1;
				addQuad(renderer, x+1, y+yMax, z, x+1, y+yMin, z, x, y+yMin, z, x, y+yMax, z, u1, u2, 0, 0.5, spriteSide, brightness);
			}
			if (!south) {
				double u1 = west ? P : 0; double u2 = east ? 1-P : 1;
				addQuad(renderer, x, y+yMax, z+1, x, y+yMin, z+1, x+1, y+yMin, z+1, x+1, y+yMax, z+1, u1, u2, 0, 0.5, spriteSide, brightness);
			}
			if (!west) {
				double u1 = north ? P : 0; double u2 = south ? 1-P : 1;
				addQuad(renderer, x, y+yMax, z, x, y+yMin, z, x, y+yMin, z+1, x, y+yMax, z+1, u1, u2, 0, 0.5, spriteSide, brightness);
			}
			if (!east) {
				double u1 = south ? P : 0; double u2 = north ? 1-P : 1;
				addQuad(renderer, x+1, y+yMax, z+1, x+1, y+yMin, z+1, x+1, y+yMin, z, x+1, y+yMax, z, u1, u2, 0, 0.5, spriteSide, brightness);
			}

			IBlockState upState = world.getBlockState(pos.up());
			boolean upOpaque = false;
			try { upOpaque = upState.getBlock().isOpaqueCube(upState); } catch(Exception ignored) {}

			if (isTop || !upOpaque) {
				// Top Face
				double u1 = west ? P : 0;  double u2 = east ? 1-P : 1;
				double v1 = north ? P : 0; double v2 = south ? 1-P : 1;
				addQuad(renderer, x, y+yMax, z, x, y+yMax, z+1, x+1, y+yMax, z+1, x+1, y+yMax, z, u1, u2, v1, v2, spriteTop, brightness);
			}

			IBlockState downState = world.getBlockState(pos.down());
			boolean downOpaque = false;
			try { downOpaque = downState.getBlock().isOpaqueCube(downState); } catch(Exception ignored) {}

			if (!isTop || !downOpaque) {
				// Bottom Face
				double u1 = west ? P : 0;  double u2 = east ? 1-P : 1;
				double v1 = south ? P : 0; double v2 = north ? 1-P : 1;
				addQuad(renderer, x, y+yMin, z+1, x, y+yMin, z, x+1, y+yMin, z, x+1, y+yMin, z+1, u1, u2, v1, v2, spriteTop, brightness);
			}

			return true;
		}
	}

	private boolean isSameBlock(IBlockAccess world, BlockPos pos, IBlockState sourceState) {
		IBlockState targetState = world.getBlockState(pos);
		Block targetBlock = targetState.getBlock();
		Block sourceBlock = sourceState.getBlock();
		if (targetBlock != sourceBlock) return false;
		return targetBlock.getMetaFromState(targetState) == sourceBlock.getMetaFromState(sourceState);
	}

	private boolean isSlabTop(IBlockState state) {
		for (Comparable<?> value : state.getProperties().values()) {
			if (value.toString().equals("top")) return true;
		}
		return false;
	}

	private static TextureAtlasSprite getSprite(IBlockState state, EnumFacing face) {
		TextureMap map = Minecraft.getMinecraft().getTextureMapBlocks();
		Block block = state.getBlock();
		String blockName = Block.blockRegistry.getNameForObject(block).toString();
		String texName = "minecraft:missingno";

		if (blockName.contains("stained_glass")) {
			int meta = block.getMetaFromState(state);
			EnumDyeColor[] colors = EnumDyeColor.values();
			String color = colors[meta % colors.length].getName();
			if (blockName.contains("pane")) {
				if (face == EnumFacing.UP || face == EnumFacing.DOWN) texName = "minecraft:blocks/glass_pane_top_" + color;
				else texName = "minecraft:blocks/glass_" + color;
			} else {
				texName = "minecraft:blocks/glass_" + color;
			}
		}
		else if (blockName.equals("minecraft:glass")) {
			texName = "minecraft:blocks/glass";
		}
		else if (blockName.equals("minecraft:glass_pane")) {
			if (face == EnumFacing.UP || face == EnumFacing.DOWN) texName = "minecraft:blocks/glass_pane_top";
			else texName = "minecraft:blocks/glass";
		}
		else if (blockName.contains("stone_slab")) {
			if (face == EnumFacing.UP || face == EnumFacing.DOWN) texName = "minecraft:blocks/stone_slab_top";
			else texName = "minecraft:blocks/stone_slab_side";
		}
		else {
			String cleanName = blockName.replace("minecraft:", "");
			texName = "minecraft:blocks/" + cleanName;
		}
		return map.getAtlasSprite(texName);
	}

	private boolean canConnect(IBlockAccess world, BlockPos self, BlockPos target, EnumFacing face) {
		IBlockState selfState = world.getBlockState(self);
		IBlockState targetState = world.getBlockState(target);
		Block selfBlock = selfState.getBlock();
		Block targetBlock = targetState.getBlock();
		if (selfBlock == targetBlock && selfBlock.getMetaFromState(selfState) == targetBlock.getMetaFromState(targetState)) return true;
		String selfName = Block.blockRegistry.getNameForObject(selfBlock).toString();
		if (selfName.equals("minecraft:double_stone_slab")) {
			String targetName = Block.blockRegistry.getNameForObject(targetBlock).toString();
			if (targetName.equals("minecraft:stone_slab")) return isSlabTop(targetState);
		}
		return false;
	}

	private boolean canConnectSlab(IBlockAccess world, BlockPos self, BlockPos target, IBlockState selfState, boolean selfIsTop) {
		IBlockState targetState = world.getBlockState(target);
		Block targetBlock = targetState.getBlock();
		String targetName = Block.blockRegistry.getNameForObject(targetBlock).toString();
		if (targetBlock == selfState.getBlock()) return selfIsTop == isSlabTop(targetState);
		if (targetName.equals("minecraft:double_stone_slab")) return true;
		return false;
	}

	private static void addQuad(WorldRenderer renderer,
								double x1, double y1, double z1,
								double x2, double y2, double z2,
								double x3, double y3, double z3,
								double x4, double y4, double z4,
								double uMin, double uMax, double vMin, double vMax,
								TextureAtlasSprite sprite, int brightness)
	{
		float minU = sprite.getInterpolatedU(uMin * 16.0);
		float maxU = sprite.getInterpolatedU(uMax * 16.0);
		float minV = sprite.getInterpolatedV(vMin * 16.0);
		float maxV = sprite.getInterpolatedV(vMax * 16.0);
		int bx = brightness & 0xFFFF;
		int by = (brightness >> 16) & 0xFFFF;
		renderer.addVertex(x1, y1, z1).setColorRGBA_F(1,1,1,1).setTextureUV(minU, minV).setBrightness(bx, by).endVertex();
		renderer.addVertex(x2, y2, z2).setColorRGBA_F(1,1,1,1).setTextureUV(minU, maxV).setBrightness(bx, by).endVertex();
		renderer.addVertex(x3, y3, z3).setColorRGBA_F(1,1,1,1).setTextureUV(maxU, maxV).setBrightness(bx, by).endVertex();
		renderer.addVertex(x4, y4, z4).setColorRGBA_F(1,1,1,1).setTextureUV(maxU, minV).setBrightness(bx, by).endVertex();
	}
}
package org.bleachhack.util.render;

import org.apache.commons.lang3.ArrayUtils;
import org.bleachhack.util.render.color.LineColor;
import org.bleachhack.util.render.color.QuadColor;

import net.minecraft.client.render.Tessellator;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

public class Vertexer {
	
	public static final int CULL_BACK = 0;
	public static final int CULL_FRONT = 1;
	public static final int CULL_NONE = 2;

	public static void vertexBoxQuads(Tessellator tesselator, Box box, QuadColor quadColor, Direction... excludeDirs) {
		float x1 = (float) box.minX;
		float y1 = (float) box.minY;
		float z1 = (float) box.minZ;
		float x2 = (float) box.maxX;
		float y2 = (float) box.maxY;
		float z2 = (float) box.maxZ;

		int cullMode = excludeDirs.length == 0 ? CULL_BACK : CULL_NONE;

		if (!ArrayUtils.contains(excludeDirs, Direction.DOWN)) {
			vertexQuad(tesselator, x1, y1, z1, x2, y1, z1, x2, y1, z2, x1, y1, z2, cullMode, quadColor);
		}

		if (!ArrayUtils.contains(excludeDirs, Direction.WEST)) {
			vertexQuad(tesselator, x1, y1, z2, x1, y2, z2, x1, y2, z1, x1, y1, z1, cullMode, quadColor);
		}

		if (!ArrayUtils.contains(excludeDirs, Direction.EAST)) {
			vertexQuad(tesselator, x2, y1, z1, x2, y2, z1, x2, y2, z2, x2, y1, z2, cullMode, quadColor);
		}

		if (!ArrayUtils.contains(excludeDirs, Direction.NORTH)) {
			vertexQuad(tesselator, x1, y1, z1, x1, y2, z1, x2, y2, z1, x2, y1, z1, cullMode, quadColor);
		}

		if (!ArrayUtils.contains(excludeDirs, Direction.SOUTH)) {
			vertexQuad(tesselator, x2, y1, z2, x2, y2, z2, x1, y2, z2, x1, y1, z2, cullMode, quadColor);
		}

		if (!ArrayUtils.contains(excludeDirs, Direction.UP)) {
			vertexQuad(tesselator, x1, y2, z2, x2, y2, z2, x2, y2, z1, x1, y2, z1, cullMode, quadColor);
		}
	}

	public static void vertexQuad(Tessellator tesselator, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, int cullMode, QuadColor quadColor) {
		int[] color = quadColor.getAllColors();

		if (cullMode != CULL_FRONT) {
			tesselator.method_1404(color[0], color[1], color[2], color[3]);
			tesselator.method_1398(x1, y1, z1);
			tesselator.method_1404(color[4], color[5], color[6], color[7]);
			tesselator.method_1398(x2, y2, z2);
			tesselator.method_1404(color[8], color[9], color[10], color[11]);
			tesselator.method_1398(x3, y3, z3);
			tesselator.method_1404(color[12], color[13], color[14], color[15]);
			tesselator.method_1398(x4, y4, z4);
		}

		if (cullMode != CULL_BACK) {
			tesselator.method_1404(color[0], color[1], color[2], color[3]);
			tesselator.method_1398(x4, y4, z4);
			tesselator.method_1404(color[4], color[5], color[6], color[7]);
			tesselator.method_1398(x3, y3, z3);
			tesselator.method_1404(color[8], color[9], color[10], color[11]);
			tesselator.method_1398(x2, y2, z2);
			tesselator.method_1404(color[12], color[13], color[14], color[15]);
			tesselator.method_1398(x1, y1, z1);
		}
	}

	public static void vertexBoxLines(Tessellator tesselator, Box box, QuadColor quadColor, Direction... excludeDirs) {
		float x1 = (float) box.minX;
		float y1 = (float) box.minY;
		float z1 = (float) box.minZ;
		float x2 = (float) box.maxX;
		float y2 = (float) box.maxY;
		float z2 = (float) box.maxZ;

		boolean exDown = ArrayUtils.contains(excludeDirs, Direction.DOWN);
		boolean exWest = ArrayUtils.contains(excludeDirs, Direction.WEST);
		boolean exEast = ArrayUtils.contains(excludeDirs, Direction.EAST);
		boolean exNorth = ArrayUtils.contains(excludeDirs, Direction.NORTH);
		boolean exSouth = ArrayUtils.contains(excludeDirs, Direction.SOUTH);
		boolean exUp = ArrayUtils.contains(excludeDirs, Direction.UP);

		int[] color = quadColor.getAllColors();

		if (!exDown) {
			vertexLine(tesselator, x1, y1, z1, x2, y1, z1, LineColor.single(color[0], color[1], color[2], color[3]));
			vertexLine(tesselator, x2, y1, z1, x2, y1, z2, LineColor.single(color[4], color[5], color[6], color[7]));
			vertexLine(tesselator, x2, y1, z2, x1, y1, z2, LineColor.single(color[8], color[9], color[10], color[11]));
			vertexLine(tesselator, x1, y1, z2, x1, y1, z1, LineColor.single(color[12], color[13], color[14], color[15]));
		}

		if (!exWest) {
			if (exDown) vertexLine(tesselator, x1, y1, z1, x1, y1, z2, LineColor.single(color[0], color[1], color[2], color[3]));
			vertexLine(tesselator, x1, y1, z2, x1, y2, z2, LineColor.single(color[4], color[5], color[6], color[7]));
			vertexLine(tesselator, x1, y1, z1, x1, y2, z1, LineColor.single(color[8], color[9], color[10], color[11]));
			if (exUp) vertexLine(tesselator, x1, y2, z1, x1, y2, z2, LineColor.single(color[12], color[13], color[14], color[15]));
		}

		if (!exEast) {
			if (exDown) vertexLine(tesselator, x2, y1, z1, x2, y1, z2, LineColor.single(color[0], color[1], color[2], color[3]));
			vertexLine(tesselator, x2, y1, z2, x2, y2, z2, LineColor.single(color[4], color[5], color[6], color[7]));
			vertexLine(tesselator, x2, y1, z1, x2, y2, z1, LineColor.single(color[8], color[9], color[10], color[11]));
			if (exUp) vertexLine(tesselator, x2, y2, z1, x2, y2, z2, LineColor.single(color[12], color[13], color[14], color[15]));
		}

		if (!exNorth) {
			if (exDown) vertexLine(tesselator, x1, y1, z1, x2, y1, z1, LineColor.single(color[0], color[1], color[2], color[3]));
			if (exEast) vertexLine(tesselator, x2, y1, z1, x2, y2, z1, LineColor.single(color[4], color[5], color[6], color[7]));
			if (exWest) vertexLine(tesselator, x1, y1, z1, x1, y2, z1, LineColor.single(color[8], color[9], color[10], color[11]));
			if (exUp) vertexLine(tesselator, x1, y2, z1, x2, y2, z1, LineColor.single(color[12], color[13], color[14], color[15]));
		}

		if (!exSouth) {
			if (exDown) vertexLine(tesselator, x1, y1, z2, x2, y1, z2, LineColor.single(color[0], color[1], color[2], color[3]));
			if (exEast) vertexLine(tesselator, x2, y1, z2, x2, y2, z2, LineColor.single(color[4], color[5], color[6], color[7]));
			if (exWest) vertexLine(tesselator, x1, y1, z2, x1, y2, z2, LineColor.single(color[8], color[9], color[10], color[11]));
			if (exUp) vertexLine(tesselator, x1, y2, z2, x2, y2, z2, LineColor.single(color[12], color[13], color[14], color[15]));
		}

		if (!exUp) {
			vertexLine(tesselator, x1, y2, z1, x2, y2, z1, LineColor.single(color[0], color[1], color[2], color[3]));
			vertexLine(tesselator, x2, y2, z1, x2, y2, z2, LineColor.single(color[4], color[5], color[6], color[7]));
			vertexLine(tesselator, x2, y2, z2, x1, y2, z2, LineColor.single(color[8], color[9], color[10], color[11]));
			vertexLine(tesselator, x1, y2, z2, x1, y2, z1, LineColor.single(color[12], color[13], color[14], color[15]));
		}
	}

	public static void vertexLine(Tessellator tesselator, float x1, float y1, float z1, float x2, float y2, float z2, LineColor lineColor) {
		int[] color1 = lineColor.getColor(x1, y1, z1, 0);
		int[] color2 = lineColor.getColor(x2, y2, z2, 1);

		tesselator.method_1404(color1[0], color1[1], color1[2], color1[3]);
		tesselator.method_1398(x1, y1, z1);
		tesselator.method_1404(color2[0], color2[1], color2[2], color2[3]);
		tesselator.method_1398(x2, y2, z2);
	}
}

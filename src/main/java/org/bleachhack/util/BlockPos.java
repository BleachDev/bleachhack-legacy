package org.bleachhack.util;

import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.google.common.collect.AbstractIterator;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class BlockPos extends Vec3i {
	/**
	 * The block position which x, y, and z values are all zero.
	 */
	public static final BlockPos ORIGIN = new BlockPos(0, 0, 0);

	public BlockPos(int i, int j, int k) {
		super(i, j, k);
	}

	public BlockPos(double d, double e, double f) {
		super((int) d, (int) e, (int) f);
	}

	public BlockPos(Vec3d pos) {
		this(pos.x, pos.y, pos.z);
	}
	
	public BlockPos(Entity e) {
		this(e.x, e.y, e.z);
	}

	public BlockPos(Position pos) {
		this(pos.getX(), pos.getY(), pos.getZ());
	}

	public BlockPos(Vec3i pos) {
		this(pos.field_4613, pos.field_4614, pos.field_4615);
	}

	public int getX() {
		return field_4613;
	}

	public int getY() {
		return field_4614;
	}

	public int getZ() {
		return field_4615;
	}

	public static long removeChunkSectionLocalY(long y) {
		return y & -16L;
	}
	
	public Box toBox() {
		return Box.method_581(getX(), getY(), getZ(), getX() + 1, getY() + 1, getZ() + 1);
	}

	public BlockPos add(double d, double e, double f) {
		return d == 0.0D && e == 0.0D && f == 0.0D ? this : new BlockPos((double)this.getX() + d, (double)this.getY() + e, (double)this.getZ() + f);
	}

	public BlockPos add(int i, int j, int k) {
		return i == 0 && j == 0 && k == 0 ? this : new BlockPos(this.getX() + i, this.getY() + j, this.getZ() + k);
	}

	public BlockPos add(Vec3i vec3i) {
		return this.add(vec3i.field_4613, vec3i.field_4614, vec3i.field_4615);
	}

	public BlockPos subtract(Vec3i vec3i) {
		return this.add(-vec3i.field_4613, -vec3i.field_4614, -vec3i.field_4615);
	}

	public BlockPos multiply(int i) {
		if (i == 1) {
			return this;
		} else {
			return i == 0 ? ORIGIN : new BlockPos(this.getX() * i, this.getY() * i, this.getZ() * i);
		}
	}

	public BlockPos up() {
		return this.offset(Direction.UP);
	}

	public BlockPos up(int distance) {
		return this.offset(Direction.UP, distance);
	}

	public BlockPos down() {
		return this.offset(Direction.DOWN);
	}

	public BlockPos down(int i) {
		return this.offset(Direction.DOWN, i);
	}

	public BlockPos north() {
		return this.offset(Direction.NORTH);
	}

	public BlockPos north(int distance) {
		return this.offset(Direction.NORTH, distance);
	}

	public BlockPos south() {
		return this.offset(Direction.SOUTH);
	}

	public BlockPos south(int distance) {
		return this.offset(Direction.SOUTH, distance);
	}

	public BlockPos west() {
		return this.offset(Direction.WEST);
	}

	public BlockPos west(int distance) {
		return this.offset(Direction.WEST, distance);
	}

	public BlockPos east() {
		return this.offset(Direction.EAST);
	}

	public BlockPos east(int distance) {
		return this.offset(Direction.EAST, distance);
	}

	public BlockPos offset(Direction direction) {
		return new BlockPos(this.getX() + direction.getOffsetX(), this.getY() + direction.getOffsetY(), this.getZ() + direction.getOffsetZ());
	}

	public BlockPos offset(Direction direction, int i) {
		return i == 0 ? this : new BlockPos(this.getX() + direction.getOffsetX() * i, this.getY() + direction.getOffsetY() * i, this.getZ() + direction.getOffsetZ() * i);
	}

	public BlockPos crossProduct(Vec3i pos) {
		return new BlockPos(this.getY() * pos.field_4615 - this.getZ() * pos.field_4614, this.getZ() * pos.field_4613 - this.getX() * pos.field_4615, this.getX() * pos.field_4614 - this.getY() * pos.field_4613);
	}

	public BlockPos withY(int y) {
		return new BlockPos(this.getX(), y, this.getZ());
	}
	
	/**
     * Iterates through {@code count} random block positions in a given range around the given position.
     * 
     * <p>The iterator yields positions in no specific order. The same position
     * may be returned multiple times by the iterator.
     * 
     * @param random the {@link Random} object used to compute new positions
     * @param count the number of positions to iterate
     * @param around the {@link BlockPos} to iterate around
     * @param range the maximum distance from the given pos in any axis
     */
    public static Iterable<BlockPos> iterateRandomly(Random random, int count, BlockPos around, int range) {
        return BlockPos.iterateRandomly(random, count, around.getX() - range, around.getY() - range, around.getZ() - range, around.getX() + range, around.getY() + range, around.getZ() + range);
    }

    /**
     * Iterates through {@code count} random block positions in the given area.
     * 
     * <p>The iterator yields positions in no specific order. The same position
     * may be returned multiple times by the iterator.
     * 
     * @param maxY the maximum y value for returned positions
     * @param maxZ the maximum z value for returned positions
     * @param minZ the minimum z value for returned positions
     * @param maxX the maximum x value for returned positions
     * @param minX the minimum x value for returned positions
     * @param minY the minimum y value for returned positions
     * @param random the {@link Random} object used to compute new positions
     * @param count the number of positions to iterate
     */
    public static Iterable<BlockPos> iterateRandomly(final Random random, final int count, final int minX, final int minY, final int minZ, int maxX, int maxY, int maxZ) {
        final int i = maxX - minX + 1;
        final int j = maxY - minY + 1;
        final int k = maxZ - minZ + 1;
        return () -> new AbstractIterator<BlockPos>() {
            int remaining = count;

            @Override
            protected BlockPos computeNext() {
                if (this.remaining <= 0) {
                    return (BlockPos)this.endOfData();
                }

                --this.remaining;
                return new BlockPos(minX + random.nextInt(i), minY + random.nextInt(j), minZ + random.nextInt(k));
            }
        };
    }

    /**
     * Iterates block positions around the {@code center}. The iteration order
     * is mainly based on the manhattan distance of the position from the
     * center.
     * 
     * <p>For the same manhattan distance, the positions are iterated by y
     * offset, from negative to positive. For the same y offset, the positions
     * are iterated by x offset, from negative to positive. For the two
     * positions with the same x and y offsets and the same manhattan distance,
     * the one with a positive z offset is visited first before the one with a
     * negative z offset.
     * 
     * @param rangeY the maximum y difference from the center
     * @param rangeZ the maximum z difference from the center
     * @param center the center of iteration
     * @param rangeX the maximum x difference from the center
     */
    public static Iterable<BlockPos> iterateOutwards(BlockPos center, final int rangeX, final int rangeY, final int rangeZ) {
        final int i = rangeX + rangeY + rangeZ;
        final int j = center.getX();
        final int k = center.getY();
        final int l = center.getZ();
        return () -> new AbstractIterator<BlockPos>(){
            private BlockPos lastPos = null;
            private int manhattanDistance;
            private int limitX;
            private int limitY;
            private int dx;
            private int dy;
            private boolean swapZ;

            @Override
            protected BlockPos computeNext() {
                if (this.swapZ) {
                    this.swapZ = false;
                    this.lastPos = new BlockPos(lastPos.getX(), lastPos.getY(), l - (lastPos.getZ() - l));
                    return lastPos;
                }

                BlockPos iterPos = null;
                while (iterPos == null) {
                    if (this.dy > this.limitY) {
                        ++this.dx;
                        if (this.dx > this.limitX) {
                            ++this.manhattanDistance;
                            if (this.manhattanDistance > i) {
                                return (BlockPos)this.endOfData();
                            }
                            this.limitX = Math.min(rangeX, this.manhattanDistance);
                            this.dx = -this.limitX;
                        }
                        this.limitY = Math.min(rangeY, this.manhattanDistance - Math.abs(this.dx));
                        this.dy = -this.limitY;
                    }
                    int i2 = this.dx;
                    int j2 = this.dy;
                    int k2 = this.manhattanDistance - Math.abs(i2) - Math.abs(j2);
                    if (k2 <= rangeZ) {
                        this.swapZ = k2 != 0;
                        iterPos = lastPos = new BlockPos(j + i2, k + j2, l + k2);
                    }
                    ++this.dy;
                }
                return iterPos;
            }
        };
    }

    public static Optional<BlockPos> findClosest(BlockPos pos, int horizontalRange, int verticalRange, Predicate<BlockPos> condition) {
        for (BlockPos blockPos : BlockPos.iterateOutwards(pos, horizontalRange, verticalRange, horizontalRange)) {
            if (!condition.test(blockPos)) continue;
            return Optional.of(blockPos);
        }
        return Optional.empty();
    }

    public static Stream<BlockPos> streamOutwards(BlockPos center, int maxX, int maxY, int maxZ) {
        return StreamSupport.stream(BlockPos.iterateOutwards(center, maxX, maxY, maxZ).spliterator(), false);
    }

    public static Iterable<BlockPos> iterate(BlockPos start, BlockPos end) {
        return BlockPos.iterate(Math.min(start.getX(), end.getX()), Math.min(start.getY(), end.getY()), Math.min(start.getZ(), end.getZ()), Math.max(start.getX(), end.getX()), Math.max(start.getY(), end.getY()), Math.max(start.getZ(), end.getZ()));
    }

    public static Stream<BlockPos> stream(BlockPos start, BlockPos end) {
        return StreamSupport.stream(BlockPos.iterate(start, end).spliterator(), false);
    }

    public static Stream<BlockPos> stream(BlockBox box) {
        return BlockPos.stream(Math.min(box.minX, box.maxX), Math.min(box.minY, box.maxY), Math.min(box.minZ, box.maxZ), Math.max(box.minX, box.maxX), Math.max(box.minY, box.maxY), Math.max(box.minZ, box.maxZ));
    }

    public static Stream<BlockPos> stream(Box box) {
        return BlockPos.stream(MathHelper.floor(box.minX), MathHelper.floor(box.minY), MathHelper.floor(box.minZ), MathHelper.floor(box.maxX), MathHelper.floor(box.maxY), MathHelper.floor(box.maxZ));
    }

    public static Stream<BlockPos> stream(int startX, int startY, int startZ, int endX, int endY, int endZ) {
        return StreamSupport.stream(BlockPos.iterate(startX, startY, startZ, endX, endY, endZ).spliterator(), false);
    }

    public static Iterable<BlockPos> iterate(final int startX, final int startY, final int startZ, int endX, int endY, int endZ) {
        final int i = endX - startX + 1;
        final int j = endY - startY + 1;
        int k = endZ - startZ + 1;
        final int l = i * j * k;
        return () -> new AbstractIterator<BlockPos>(){
            private int index;

            @Override
            protected BlockPos computeNext() {
                if (this.index == l) {
                    return (BlockPos)this.endOfData();
                }
                int i2 = this.index % i;
                int j2 = this.index / i;
                int k = j2 % j;
                int l2 = j2 / j;
                ++this.index;
                return new BlockPos(startX + i2, startY + k, startZ + l2);
            }
        };
    }
    
    public String toShortString() {
		int var10000 = this.getX();
		return var10000 + ", " + this.getY() + ", " + this.getZ();
	}
}
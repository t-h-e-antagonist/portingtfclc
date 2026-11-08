package com.vomiter.tfclc.util.bookshelf;

import net.dries007.tfc.common.blocks.wood.BookshelfBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class LostCitiesBookshelfFacingHelper {

    private static final Direction[] HORIZONTAL = {
            Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST
    };

    private LostCitiesBookshelfFacingHelper() {
    }

    public static BlockState fixFacing(BlockGetter level, BlockPos pos, BlockState state) {
        if (!state.hasProperty(HorizontalDirectionalBlock.FACING)) {
            return state;
        }

        Direction best = chooseBestFacing(level, pos);
        return state.setValue(HorizontalDirectionalBlock.FACING, best);
    }

    public static Direction chooseBestFacing(BlockGetter level, BlockPos pos) {
        Direction bestDir = Direction.NORTH;
        int bestScore = Integer.MIN_VALUE;

        for (Direction facing : HORIZONTAL) {
            int score = scoreFacing(level, pos, facing);
            if (score > bestScore) {
                bestScore = score;
                bestDir = facing;
            }
        }

        return bestDir;
    }

    /**
     * facing = 書架正面朝向
     * 正面開闊，背面靠牆
     */
    private static int scoreFacing(BlockGetter level, BlockPos pos, Direction facing) {
        int score = 0;

        BlockPos frontPos = pos.relative(facing);
        BlockPos backPos = pos.relative(facing.getOpposite());

        BlockState front = level.getBlockState(frontPos);
        BlockState back = level.getBlockState(backPos);

        boolean frontBlocked = isFrontBlocked(level, frontPos, front);
        boolean backSolid = isSolidBacking(level, backPos, back);

        // 前面不能堵住
        if (frontBlocked) {
            score -= 1000;
        } else {
            score += 120;
        }

        // 背後最好是牆
        if (backSolid) {
            score += 80;
        } else {
            score -= 20;
        }

        // 正前方的開闊程度
        score += opennessScore(level, pos, facing, 4);

        // 側邊太擠稍微扣分
        Direction left = facing.getCounterClockWise();
        Direction right = facing.getClockWise();

        if (isSolid(level, pos.relative(left))) score -= 8;
        if (isSolid(level, pos.relative(right))) score -= 8;

        return score;
    }

    private static int opennessScore(BlockGetter level, BlockPos pos, Direction dir, int maxDist) {
        int score = 0;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (int i = 1; i <= maxDist; i++) {
            cursor.set(
                    pos.getX() + dir.getStepX() * i,
                    pos.getY(),
                    pos.getZ() + dir.getStepZ() * i
            );

            BlockState state = level.getBlockState(cursor);

            if (isFrontBlocked(level, cursor, state)) {
                break;
            }

            score += 15;
        }

        return score;
    }

    private static boolean isFrontBlocked(BlockGetter level, BlockPos pos, BlockState state) {
        return state.isCollisionShapeFullBlock(level, pos);
    }

    private static boolean isSolidBacking(BlockGetter level, BlockPos pos, BlockState state) {
        return !(state.getBlock() instanceof BookshelfBlock) && state.isCollisionShapeFullBlock(level, pos);
    }

    private static boolean isSolid(BlockGetter level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isCollisionShapeFullBlock(level, pos);
    }
}
package mcjty.rftoolsutility.modules.screen.blocks;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.varia.SafeClientTools;
import mcjty.rftoolsutility.modules.screen.ScreenModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;

import static mcjty.rftoolsutility.modules.screen.blocks.ScreenBlock.*;

public class ScreenHitBlock extends BaseBlock {

    public ScreenHitBlock() {
        super(new BlockBuilder()
                .properties(Properties.of().strength(-1.0F, 3600000.0F)
                        .pushReaction(PushReaction.BLOCK)
                        .sound(SoundType.METAL))
                .tileEntitySupplier(ScreenHitTileEntity::new));
    }

    @Nonnull
    @Override
    public ItemStack getCloneItemStack(@Nonnull BlockGetter worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        BlockPos screenPos = getScreenBlockPos(worldIn, pos);
        if(screenPos == null) {
            return ItemStack.EMPTY;
        }
        BlockState screenState = worldIn.getBlockState(screenPos);
        return screenState.getBlock().getCloneItemStack(worldIn, screenPos, screenState);
    }

    @Override
    public void attack(@Nonnull BlockState s, Level world, @Nonnull BlockPos pos, @Nonnull Player player) {
        if (world.isClientSide) {
            ScreenHitTileEntity screenHitTileEntity = (ScreenHitTileEntity) world.getBlockEntity(pos);
            int dx = screenHitTileEntity.getDx();
            int dy = screenHitTileEntity.getDy();
            int dz = screenHitTileEntity.getDz();
            BlockState state = world.getBlockState(pos.offset(dx, dy, dz));
            Block block = state.getBlock();
            if (block != ScreenModule.SCREEN.get() && block != ScreenModule.CREATIVE_SCREEN.get()) {
                return;
            }

            HitResult mouseOver = SafeClientTools.getClientMouseOver();
            ScreenTileEntity screenTileEntity = (ScreenTileEntity) world.getBlockEntity(pos.offset(dx, dy, dz));
            if (mouseOver instanceof BlockHitResult blockHit) {
                screenTileEntity.hitScreenClient(mouseOver.getLocation().x - pos.getX() - dx, mouseOver.getLocation().y - pos.getY() - dy, mouseOver.getLocation().z - pos.getZ() - dz,
                        blockHit.getDirection(), state.getValue(ScreenBlock.HORIZ_FACING));
            }
        }
    }

    @Nonnull
    @Override
    public InteractionResult use(@Nonnull BlockState state, Level world, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult result) {
        return activate(world, pos, state, player, hand, result);
    }

    public InteractionResult activate(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand, BlockHitResult result) {
        pos = getScreenBlockPos(world, pos);
        if (pos == null) {
            return InteractionResult.PASS;
        }
        Block block = world.getBlockState(pos).getBlock();
        return ((ScreenBlock) block).activate(world, pos, state, player, hand, result);
    }

    @Override
    public BlockState rotate(BlockState state, LevelAccessor world, BlockPos pos, Rotation rot) {
        // Doesn't make sense to rotate a potentially 3x3 screen,
        // and is incompatible with our special wrench actions.
        return state;
    }

    public BlockPos getScreenBlockPos(BlockGetter world, BlockPos pos) {
        ScreenHitTileEntity screenHitTileEntity = (ScreenHitTileEntity) world.getBlockEntity(pos);
        int dx = screenHitTileEntity.getDx();
        int dy = screenHitTileEntity.getDy();
        int dz = screenHitTileEntity.getDz();
        pos = pos.offset(dx, dy, dz);
        Block block = world.getBlockState(pos).getBlock();
        if (block != ScreenModule.SCREEN.get() && block != ScreenModule.CREATIVE_SCREEN.get()) {
            return null;
        }
        return pos;
    }

    @Nonnull
    @Override
    public VoxelShape getShape(BlockState state, @Nonnull BlockGetter worldIn, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        if (facing == Direction.NORTH) {
            return NORTH_AABB;
        } else if (facing == Direction.SOUTH) {
            return SOUTH_AABB;
        } else if (facing == Direction.WEST) {
            return WEST_AABB;
        } else if (facing == Direction.EAST) {
            return EAST_AABB;
        } else if (facing == Direction.UP) {
            return UP_AABB;
        } else if (facing == Direction.DOWN) {
            return DOWN_AABB;
        } else {
            return BLOCK_AABB;
        }
    }
//
//    @Override
//    public boolean isOpaqueCube(BlockState state) {
//        return false;
//    }
//
//    @Override
//    public boolean isBlockNormalCube(BlockState state) {
//        return false;
//    }
//
//    @Override
//    public boolean isFullBlock(BlockState state) {
//        return false;
//    }
//
//    @Override
//    public boolean isFullCube(BlockState state) {
//        return false;
//    }

    @Nonnull
    @Override
    public RenderShape getRenderShape(@Nonnull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public boolean canEntityDestroy(BlockState state, BlockGetter world, BlockPos pos, Entity entity) {
        return false;
    }

    @Override
    public void wasExploded(@Nonnull Level world, @Nonnull BlockPos pos, @Nonnull Explosion explosion) {
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
//        builder.add(BlockStateProperties.FACING);
    }

    // @todo 1.14
//    @Override
//    public int quantityDropped(Random random) {
//        return 0;
//    }
}

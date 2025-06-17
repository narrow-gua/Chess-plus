package com.cookie.chess.block;

import com.cookie.chess.tileentity.TileEntityCChessPVP;
import com.github.tartaricacid.touhoulittlemaid.api.game.xqwlight.Position;
import com.github.tartaricacid.touhoulittlemaid.block.properties.GomokuPart;
import com.github.tartaricacid.touhoulittlemaid.init.InitSounds;
import com.github.tartaricacid.touhoulittlemaid.util.CChessUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.UUID;

public class BlockCChessPVP extends BaseEntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<GomokuPart> PART = EnumProperty.create("part", GomokuPart.class);
    public static final VoxelShape AABB = Block.box(0, 0, 0, 16, 2, 16);

    public BlockCChessPVP() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).sound(SoundType.WOOD).strength(2.0F, 3.0F).forceSolidOn().noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(PART, GomokuPart.CENTER));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level instanceof ServerLevel) || hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }

        GomokuPart part = state.getValue(PART);
        BlockPos centerPos = pos.subtract(new Vec3i(part.getPosX(), 0, part.getPosY()));
        BlockEntity te = level.getBlockEntity(centerPos);

        if (!(te instanceof TileEntityCChessPVP chess)) {
            return InteractionResult.FAIL;
        }

        UUID playerId = player.getUUID();

        // 注册玩家（如果尚未注册）
        if (!chess.isPlayerRegistered(playerId)) {
            if (!chess.registerPlayer(playerId)) {
                player.sendSystemMessage(Component.literal("游戏玩家已满"));
                return InteractionResult.FAIL;
            } else {
                player.sendSystemMessage(Component.literal("你已加入游戏！"));
            }
        }

        // 检查是否是当前玩家的回合
        if (!chess.isPlayerTurn(playerId)) {
            player.sendSystemMessage(Component.literal("不是你的回合！"));
            return InteractionResult.FAIL;
        }

        Direction facing = state.getValue(FACING);
        Vec3 clickPos = hit.getLocation()
                .subtract(pos.getX(), pos.getY(), pos.getZ())
                .add(part.getPosX() - 0.5, 0, part.getPosY() - 0.5)
                .yRot(facing.toYRot() * Mth.DEG_TO_RAD);

        // 重置棋盘逻辑
        boolean clickResetArea = CChessUtil.isClickResetArea(clickPos);
        if (clickResetArea) {
            chess.reset();
            chess.refresh();
            level.playSound(null, centerPos, InitSounds.GOMOKU_RESET.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
            player.sendSystemMessage(Component.literal("棋盘已重置！"));
            return InteractionResult.SUCCESS;
        }

        // 获取点击位置
        int nowClick = CChessUtil.getClickPosition(clickPos);
        if (nowClick < 0 || !Position.IN_BOARD(nowClick)) {
            return InteractionResult.PASS;
        }

        // 检查游戏状态限制
        if (chess.isMoveNumberLimit() || chess.isRepeat()) {
            player.sendSystemMessage(Component.literal("当前不能移动棋子！"));
            return InteractionResult.FAIL;
        }

        Position chessData = chess.getChessData();
        byte[] squares = chessData.squares;
        int preClick = chess.getSelectChessPoint();

        // 初始化选中位置
        if (preClick < 0 || preClick >= squares.length) {
            preClick = -1;
        }

        byte nowPiece = squares[nowClick];

        // 1. 如果之前没有选中棋子
        if (preClick < 0) {
            // 检查玩家是否可以操作该棋子
            if (chess.canPlayerControlPiece(playerId, nowPiece)) {
                chess.setSelectChessPoint(nowClick);
                chess.refresh();
                level.playSound(null, pos, InitSounds.GOMOKU.get(), SoundSource.BLOCKS, 1.0f, 0.8F + level.random.nextFloat() * 0.4F);
                player.sendSystemMessage(Component.literal("选中棋子！"));
                return InteractionResult.SUCCESS;
            } else {
                player.sendSystemMessage(Component.literal("不能操作对方棋子！"));
                return InteractionResult.FAIL;
            }
        }

        // 2. 如果之前已选中棋子
        byte prePiece = squares[preClick];

        // 检查是否点击了自己的其他棋子（切换选中）
        if (chess.canPlayerControlPiece(playerId, nowPiece)) {
            chess.setSelectChessPoint(nowClick);
            chess.refresh();
            level.playSound(null, pos, InitSounds.GOMOKU.get(), SoundSource.BLOCKS, 1.0f, 0.8F + level.random.nextFloat() * 0.4F);
            player.sendSystemMessage(Component.literal("切换选中棋子！"));
            return InteractionResult.SUCCESS;
        }

        // 3. 尝试移动棋子
        int move = Position.MOVE(preClick, nowClick);

        // 验证移动是否合法
        if (!chessData.legalMove(move)) {
            player.sendSystemMessage(Component.literal("非法移动！"));
            return InteractionResult.FAIL;
        }

        // 执行移动
        boolean moveSuccess = chessData.makeMove(move);
        if (moveSuccess) {
            // 更新游戏状态
            if (chessData.captured()) {
                chessData.setIrrev(); // 标记不可逆状态
            }

            // 切换回合
            chess.switchTurn();
            chess.setSelectChessPoint(-1); // 清除选中
            chess.addChessCounter();
            chess.refresh();

            // 播放音效和通知
            level.playSound(null, pos, InitSounds.GOMOKU.get(), SoundSource.BLOCKS, 1.0f, 0.8F + level.random.nextFloat() * 0.4F);
            player.sendSystemMessage(Component.literal("移动成功！"));

            // 检查游戏结束条件
            if (chess.isCheckmate()) {
                level.playSound(null, pos, SoundEvents.NOTE_BLOCK_BELL.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
                level.players().forEach(p ->
                        p.sendSystemMessage(Component.literal("游戏结束！" + player.getName().getString() + "获胜！"))
                );
//                chess.setInProgress(false);
            }

            return InteractionResult.SUCCESS;
        } else {
            player.sendSystemMessage(Component.literal("移动失败！"));
            return InteractionResult.FAIL;
        }
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (!worldIn.isClientSide) {
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    BlockPos searchPos = pos.offset(i, 0, j);
                    GomokuPart part = GomokuPart.getPartByPos(i, j);
                    if (part != null && !part.isCenter()) {
                        worldIn.setBlock(searchPos, state.setValue(PART, part), Block.UPDATE_ALL);
                    }
                }
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PART);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (state.getValue(PART).isCenter()) {
            return new TileEntityCChessPVP(pos, state);
        }
        return null;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return AABB;
    }


}

package com.cookie.chess.tileentity;

import com.cookie.chess.init.InitEntities;
import com.github.tartaricacid.touhoulittlemaid.api.game.xqwlight.Position;
import com.github.tartaricacid.touhoulittlemaid.util.CChessUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.UUID;

public class TileEntityCChessPVP extends BlockEntity {
    public static final BlockEntityType<TileEntityCChessPVP> TYPE = InitEntities.CCHESS_PVP.get();

    private static final String CHESS_DATA = "ChessData";
    private static final String CHESS_COUNTER = "ChessCounter";
    private static final String SELECT_CHESS_POINT = "SelectChessPoint";
    private static final String CHECKMATE = "Checkmate";
    private static final String REPEAT = "Repeat";
    private static final String MOVE_NUMBER_LIMIT = "MoveNumberLimit";
    private static final String PLAYER_RED = "PlayerRed";
    private static final String PLAYER_BLACK = "PlayerBlack";

    private UUID playerRed;
    private UUID playerBlack;

    private final Position chessData;

    // 回合计数器
    private int chessCounter = 0;
    // 当前选中的棋子
    private int selectChessPoint = -1;
    // 将死（依据下棋方，判断谁输谁赢）
    private boolean checkmate = false;
    // 长打（判和）
    private boolean repeat = false;
    // 60 回自然限着（判和）
    private boolean moveNumberLimit = false;

    public TileEntityCChessPVP(BlockPos pos, BlockState blockState) {
        super(TYPE, pos, blockState);
        this.chessData = new Position();
        this.chessData.fromFen(CChessUtil.INIT);
    }

    public boolean registerPlayer(UUID playerId) {
        if (playerId == null) {
            return false;
        }
        if (playerRed == null) {
            playerRed = playerId;
            return true;
        } else if (playerBlack == null && !playerId.equals(playerRed)) {
            playerBlack = playerId;
            return true;
        }
        return false;
    }

    public boolean isPlayerRegistered(UUID playerId) {
        if (playerId == null) {
            return false;
        }
        return playerId.equals(playerRed) || playerId.equals(playerBlack);
    }

    // 检查是否是当前玩家的回合：以棋局引擎内的 sdPlayer 为唯一真相
    public boolean isPlayerTurn(UUID playerId) {
        if (playerId == null) {
            return false;
        }

        boolean isRedTurn = CChessUtil.isPlayer(this.chessData);
        if (playerId.equals(playerRed)) {
            return isRedTurn;
        }
        if (playerId.equals(playerBlack)) {
            return !isRedTurn;
        }
        return false;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        CompoundTag data = getPersistentData();
        data.putString(CHESS_DATA, chessData.toFen());
        data.putInt(CHESS_COUNTER, chessCounter);
        data.putInt(SELECT_CHESS_POINT, selectChessPoint);
        data.putBoolean(CHECKMATE, checkmate);
        data.putBoolean(REPEAT, repeat);
        data.putBoolean(MOVE_NUMBER_LIMIT, moveNumberLimit);

        if (playerRed != null) {
            data.putUUID(PLAYER_RED, playerRed);
        } else {
            data.remove(PLAYER_RED);
        }

        if (playerBlack != null) {
            data.putUUID(PLAYER_BLACK, playerBlack);
        } else {
            data.remove(PLAYER_BLACK);
        }

        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        CompoundTag data = getPersistentData();

        chessCounter = data.getInt(CHESS_COUNTER);
        selectChessPoint = data.contains(SELECT_CHESS_POINT) ? data.getInt(SELECT_CHESS_POINT) : -1;

        String fen = data.getString(CHESS_DATA);
        if (fen == null || fen.isEmpty()) {
            fen = CChessUtil.INIT;
        }
        chessData.fromFen(fen);

        checkmate = data.getBoolean(CHECKMATE);
        repeat = data.getBoolean(REPEAT);
        moveNumberLimit = data.getBoolean(MOVE_NUMBER_LIMIT);

        playerRed = data.hasUUID(PLAYER_RED) ? data.getUUID(PLAYER_RED) : null;
        playerBlack = data.hasUUID(PLAYER_BLACK) ? data.getUUID(PLAYER_BLACK) : null;
    }

    public void reset() {
        this.chessCounter = 0;
        this.selectChessPoint = -1;
        this.chessData.fromFen(CChessUtil.INIT);
        this.checkmate = false;
        this.repeat = false;
        this.moveNumberLimit = false;
    }

    public Position getChessData() {
        return chessData;
    }

    public boolean isCheckmate() {
        return checkmate;
    }

    public void setCheckmate(boolean checkmate) {
        this.checkmate = checkmate;
    }

    public boolean isPlayerTurn() {
        return CChessUtil.isPlayer(this.chessData);
    }

    public int getChessCounter() {
        return chessCounter;
    }

    public void addChessCounter() {
        this.chessCounter += 1;
    }

    public int getSelectChessPoint() {
        return selectChessPoint;
    }

    public void setSelectChessPoint(int selectChessPoint) {
        this.selectChessPoint = selectChessPoint;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public boolean isMoveNumberLimit() {
        return moveNumberLimit;
    }

    public void setMoveNumberLimit(boolean moveNumberLimit) {
        this.moveNumberLimit = moveNumberLimit;
    }

    public void refresh() {
        this.setChanged();
        if (level != null) {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
        }
    }

    public boolean canPlayerControlPiece(UUID playerId, byte piece) {
        if (playerId == null || piece == 0) {
            return false;
        }

        boolean isRedPiece = CChessUtil.isRed(piece);
        if (playerId.equals(playerRed)) {
            return isRedPiece;
        }
        if (playerId.equals(playerBlack)) {
            return !isRedPiece;
        }

        return false;
    }
}

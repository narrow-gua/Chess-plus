package com.cookie.chess.tileentity;

import com.cookie.chess.init.InitBlocks;
import com.cookie.chess.init.InitEntities;
import com.github.tartaricacid.touhoulittlemaid.api.game.gomoku.Point;
import com.github.tartaricacid.touhoulittlemaid.tileentity.TileEntityGomoku;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.UUID;


public class TileEntityGomokuPVP extends BlockEntity {

    private int[][] chessData = new int[15][15];
    public UUID playerBlack;
    public UUID playerWhite;
    public boolean isBlackTurn = true;
    private boolean inProgress = true;
    private int chessCounter = 0;
    public Point latestChessPoint;
    public int winner = 0;

    public static BlockEntityType<TileEntityGomokuPVP> TYPE = InitEntities.GOMOKU_PVP.get();

    public TileEntityGomokuPVP(BlockPos pos, BlockState state) {
        super(InitEntities.GOMOKU_PVP.get(), pos, state);
        this.latestChessPoint = Point.NULL;
    }

    protected void saveAdditional(CompoundTag tag) {
        ListTag listTag = new ListTag();
        int[][] var3 = this.chessData;
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            int[] chessRow = var3[var5];
            listTag.add(new IntArrayTag(chessRow));
        }
        this.getPersistentData().putBoolean("IsBackTurn", isBlackTurn);
        this.getPersistentData().putInt("Winner", winner);
        this.getPersistentData().put("ChessData", listTag);
        this.getPersistentData().putBoolean("InProgress", this.inProgress);
        this.getPersistentData().putInt("ChessCounter", this.chessCounter);
        this.getPersistentData().putString("playerBlack", playerBlack == null  ? "" :  playerBlack.toString());
        this.getPersistentData().putString("playerWhite",playerWhite== null ? "" : playerWhite.toString());
        this.getPersistentData().put("LatestChessPoint", Point.toTag(this.latestChessPoint));
        super.saveAdditional(tag);
    }


    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void refresh() {
        this.setChanged();
        if (level != null) {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
        }
    }


    public boolean isPlayerTurn(UUID player) {
        return (isBlackTurn && player.equals(playerBlack)) || (!isBlackTurn && player.equals(playerWhite));
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }


    public void load(CompoundTag nbt) {
        super.load(nbt);
        ListTag listTag = this.getPersistentData().getList("ChessData", 11);

        for(int i = 0; i < listTag.size(); ++i) {
            int[] intArray = listTag.getIntArray(i);
            this.chessData[i] = intArray;
        }

        //恢复uuid
        String whiteStr = this.getPersistentData().getString("playerWhite");
        this.playerWhite = whiteStr.isEmpty() ? null : UUID.fromString(whiteStr);

        String blackStr = this.getPersistentData().getString("playerBlack");
        this.playerBlack = blackStr.isEmpty() ? null : UUID.fromString(blackStr);
        this.winner = this.getPersistentData().getInt("Winner");
        this.inProgress = this.getPersistentData().getBoolean("InProgress");
        this.isBlackTurn = this.getPersistentData().getBoolean("IsBlackTurn");
        this.chessCounter = this.getPersistentData().getInt("ChessCounter");
        this.latestChessPoint = Point.fromTag(this.getPersistentData().getCompound("LatestChessPoint"));
    }


    public boolean registerPlayer(UUID player) {
        if (playerBlack == null) {
            playerBlack = player;
            return true;
        } else if (playerWhite == null && !player.equals(playerBlack)) {
            playerWhite = player;
            return true;
        }
        return false;
    }


    public void setChessData(int x, int y, int type) {
        this.chessData[x][y] = type;
        this.latestChessPoint = new Point(x, y, type);
        ++this.chessCounter;
    }


    public boolean placeChess(int x, int y) {
        if (chessData[x][y] == Point.EMPTY) {
            chessData[x][y] = isBlackTurn ? Point.BLACK : Point.WHITE;
            return true;
        }
        return false;
    }

    public void switchTurn() {
        isBlackTurn = !isBlackTurn;
    }

    public int[][] getChessData() {
        return chessData;
    }

    public boolean isInProgress() {
        return inProgress;
    }

    public void setInProgress(boolean value) {
        this.inProgress = value;
    }

    public int getChessCounter() {
        return this.chessCounter;
    }

    public Point getLatestChessPoint() {
        return this.latestChessPoint;
    }

    @OnlyIn(Dist.CLIENT)
    public AABB getRenderBoundingBox() {
        return new AABB(this.worldPosition.offset(-2, 0, -2), this.worldPosition.offset(2, 1, 2));
    }


    public void reset() {
        this.chessData = new int[15][15];
        this.inProgress = true;
        this.isBlackTurn = true;
        this.playerWhite = UUID.fromString("") ;
        this.playerBlack = UUID.fromString("") ;
        this.winner = 0;
        this.chessCounter = 0;
        this.latestChessPoint = Point.NULL;
    }

}

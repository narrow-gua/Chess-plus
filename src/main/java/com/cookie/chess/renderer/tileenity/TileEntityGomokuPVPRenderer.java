package com.cookie.chess.renderer.tileenity;

import com.cookie.chess.tileentity.TileEntityGomokuPVP;
import com.github.tartaricacid.touhoulittlemaid.api.game.gomoku.Point;
import com.github.tartaricacid.touhoulittlemaid.block.BlockGomoku;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.SimpleBedrockModel;
import com.github.tartaricacid.touhoulittlemaid.client.resource.BedrockModelLoader;
import com.github.tartaricacid.touhoulittlemaid.tileentity.TileEntityGomoku;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class TileEntityGomokuPVPRenderer implements BlockEntityRenderer<TileEntityGomokuPVP> {
    private static final ResourceLocation CHECKER_BOARD_TEXTURE = new ResourceLocation("touhou_little_maid", "textures/bedrock/block/gomoku.png");
    private static final ResourceLocation BLACK_PIECE_TEXTURE = new ResourceLocation("touhou_little_maid", "textures/bedrock/block/gomoku_black_piece.png");
    private static final ResourceLocation WHITE_PIECE_TEXTURE = new ResourceLocation("touhou_little_maid", "textures/bedrock/block/gomoku_white_piece.png");
    private static final int TIPS_RENDER_DISTANCE = 16;
    private static final int PIECE_RENDER_DISTANCE = 24;
    private final SimpleBedrockModel<Entity> CHECKER_BOARD_MODEL;
    private final SimpleBedrockModel<Entity> PIECE_MODEL;
    private final Font font;
    private final BlockEntityRenderDispatcher dispatcher;

    public TileEntityGomokuPVPRenderer(BlockEntityRendererProvider.Context context) {
        this.CHECKER_BOARD_MODEL = BedrockModelLoader.getModel(BedrockModelLoader.GOMOKU);
        this.PIECE_MODEL = BedrockModelLoader.getModel(BedrockModelLoader.GOMOKU_PIECE);
        this.font = context.getFont();
        this.dispatcher = context.getBlockEntityRenderDispatcher();
    }


    public void render(TileEntityGomokuPVP gomoku, float partialTick, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        this.renderChessboard(gomoku, poseStack, bufferIn, combinedLightIn, combinedOverlayIn);
        this.renderPiece(gomoku, poseStack, bufferIn, combinedLightIn, combinedOverlayIn);
        this.renderLatestChessTips(gomoku, poseStack, bufferIn, combinedLightIn);
        this.renderTipsText(gomoku, poseStack, bufferIn, combinedLightIn);
    }

    private void renderLatestChessTips(TileEntityGomokuPVP gomoku, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn) {
        if (!gomoku.getLatestChessPoint().equals(Point.NULL) && this.inRenderDistance(gomoku, 24)) {
            Camera camera = this.dispatcher.camera;
            Point point = gomoku.getLatestChessPoint();
            poseStack.pushPose();
            poseStack.translate(-0.42, 0.25, -0.42);
            poseStack.translate((double)point.x * 0.1316, 0.0, (double)point.y * 0.1316);
            poseStack.mulPose(Axis.YN.rotationDegrees(180.0F + camera.getYRot()));
            poseStack.scale(0.015625F, -0.015625F, 0.015625F);
            float width = (float)(-this.font.width("▼") / 2) + 0.5F;
            this.font.drawInBatch("▼", width, -1.5F, 16711680, false, poseStack.last().pose(), bufferIn, Font.DisplayMode.POLYGON_OFFSET, 0, combinedLightIn);
            poseStack.popPose();
        }

    }

    private void renderChessboard(TileEntityGomokuPVP gomoku, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        Direction facing = (Direction)gomoku.getBlockState().getValue(BlockGomoku.FACING);
        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.mulPose(Axis.ZN.rotationDegrees(180.0F));
        poseStack.mulPose(Axis.YN.rotationDegrees((float)(facing.get2DDataValue() * 90)));
        if (facing == Direction.SOUTH || facing == Direction.NORTH) {
            poseStack.mulPose(Axis.YN.rotationDegrees(180.0F));
        }

        VertexConsumer checkerBoardBuff = bufferIn.getBuffer(RenderType.entityCutoutNoCull(CHECKER_BOARD_TEXTURE));
        this.CHECKER_BOARD_MODEL.renderToBuffer(poseStack, checkerBoardBuff, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }

    private void renderPiece(TileEntityGomokuPVP gomoku, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        if (this.inRenderDistance(gomoku, 24)) {
            poseStack.pushPose();
            poseStack.translate(0.5, 1.5, 0.5);
            poseStack.mulPose(Axis.ZN.rotationDegrees(180.0F));
            poseStack.translate(0.92, -0.1, -1.055);
            int[][] chessData = gomoku.getChessData();
            int[][] var7 = chessData;
            int var8 = chessData.length;

            for(int var9 = 0; var9 < var8; ++var9) {
                int[] row = var7[var9];

                for(int j = 0; j < chessData[0].length; ++j) {
                    poseStack.translate(0.0, 0.0, 0.1316);
                    VertexConsumer whitePieceBuff;
                    if (row[j] == 1) {
                        whitePieceBuff = bufferIn.getBuffer(RenderType.entityCutoutNoCull(BLACK_PIECE_TEXTURE));
                        this.PIECE_MODEL.renderToBuffer(poseStack, whitePieceBuff, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
                    }

                    if (row[j] == 2) {
                        whitePieceBuff = bufferIn.getBuffer(RenderType.entityCutoutNoCull(WHITE_PIECE_TEXTURE));
                        this.PIECE_MODEL.renderToBuffer(poseStack, whitePieceBuff, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
                    }
                }

                poseStack.translate(-0.1316, 0.0, -1.974);
            }

            poseStack.popPose();
        }

    }

    private void renderTipsText(TileEntityGomokuPVP gomoku, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn) {
        if (!gomoku.isInProgress() && this.inRenderDistance(gomoku, 16)) {
            Camera camera = this.dispatcher.camera;
            MutableComponent resetTips = Component.translatable("message.touhou_little_maid.gomoku.reset").withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.AQUA);
            MutableComponent roundText = Component.translatable("message.touhou_little_maid.gomoku.round", new Object[]{gomoku.getChessCounter()}).withStyle(ChatFormatting.WHITE);
            MutableComponent preRoundIcon = Component.literal("⏹ ").withStyle(ChatFormatting.GREEN);
            MutableComponent postRoundIcon = Component.literal(" ⏹").withStyle(ChatFormatting.GREEN);
            MutableComponent roundTips = preRoundIcon.append(roundText).append(postRoundIcon);
            MutableComponent loseTips;
            if (gomoku.winner == 1) {
                loseTips = Component.translatable("message.chessplus.gomoku_blackwin").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.DARK_PURPLE);
            } else if (gomoku.winner == 2) {
                loseTips = Component.translatable("message.chessplus.gomoku_whitewin").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.DARK_PURPLE);
            } else {
                loseTips = Component.literal("平局").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.DARK_PURPLE);
            }
            float loseTipsWidth = (float)(-this.font.width(loseTips) / 2);
            float resetTipsWidth = (float)(-this.font.width(resetTips) / 2);
            float roundTipsWidth = (float)(-this.font.width(roundTips) / 2);
            poseStack.pushPose();
            poseStack.translate(0.5, 0.75, 0.5);
            poseStack.mulPose(Axis.YN.rotationDegrees(180.0F + camera.getYRot()));
            poseStack.mulPose(Axis.XN.rotationDegrees(camera.getXRot()));
            poseStack.scale(0.03F, -0.03F, 0.03F);
            this.font.drawInBatch(loseTips, loseTipsWidth, -10.0F, 16777215, true, poseStack.last().pose(), bufferIn, Font.DisplayMode.POLYGON_OFFSET, 0, combinedLightIn);
            poseStack.scale(0.5F, 0.5F, 0.5F);
            this.font.drawInBatch(roundTips, roundTipsWidth, -30.0F, 16777215, true, poseStack.last().pose(), bufferIn, Font.DisplayMode.POLYGON_OFFSET, 0, combinedLightIn);
            this.font.drawInBatch(resetTips, resetTipsWidth, 0.0F, 16777215, true, poseStack.last().pose(), bufferIn, Font.DisplayMode.POLYGON_OFFSET, 0, combinedLightIn);
            poseStack.popPose();
        }

    }

    private boolean inRenderDistance(TileEntityGomokuPVP gomoku, int distance) {
        BlockPos pos = gomoku.getBlockPos();
        return this.dispatcher.camera.getPosition().distanceToSqr((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()) < (double)(distance * distance);
    }

    public boolean shouldRenderOffScreen(TileEntityGomokuPVP te) {
        return true;
    }
}

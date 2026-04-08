package com.cookie.chess.init;

import com.cookie.chess.renderer.tileenity.TileEntityCChessPVPRenderer;
import com.cookie.chess.renderer.tileenity.TileEntityGomokuPVPRenderer;
import com.cookie.chess.tileentity.TileEntityCChessPVP;
import com.cookie.chess.tileentity.TileEntityGomokuPVP;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class InitEntitiesRender {
    @SubscribeEvent
    public static void onEntityRenderers(EntityRenderersEvent.RegisterRenderers evt) {
        BlockEntityRenderers.register(TileEntityGomokuPVP.TYPE, TileEntityGomokuPVPRenderer::new);
        BlockEntityRenderers.register(TileEntityCChessPVP.TYPE, TileEntityCChessPVPRenderer::new);
    }
}
package com.cookie.chess;

import com.cookie.chess.init.InitBlocks;
import com.cookie.chess.init.InitEntities;
import com.cookie.chess.init.InitItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ChessPlus.MOD_ID)
public class ChessPlus {
    public static final String MOD_ID = "chessplus";
    public static final Logger LOGGER = LogManager.getLogger("chess_plus");


    public ChessPlus() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        initRegister(modEventBus);
    }
    private void initRegister(IEventBus eventBus) {
        InitBlocks.BLOCKS.register(eventBus);
        InitEntities.BLOCK_ENTITIES.register(eventBus);
        InitItems.ITEMS.register(eventBus);
        MinecraftForge.EVENT_BUS.register(this);
    }

}

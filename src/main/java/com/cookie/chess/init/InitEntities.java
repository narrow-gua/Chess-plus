package com.cookie.chess.init;

import com.cookie.chess.ChessPlus;
import com.cookie.chess.tileentity.TileEntityGomokuPVP;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class InitEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ChessPlus.MOD_ID);


    public static final RegistryObject<BlockEntityType<TileEntityGomokuPVP>> GOMOKU_PVP =
            BLOCK_ENTITIES.register("gomoku_pvp", () ->
                    BlockEntityType.Builder.of(TileEntityGomokuPVP::new, InitBlocks.GOMOKU_PVP.get()).build(null));


}

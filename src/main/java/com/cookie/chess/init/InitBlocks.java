package com.cookie.chess.init;

import com.cookie.chess.ChessPlus;
import com.cookie.chess.block.BlockCChessPVP;
import com.cookie.chess.block.BlockGomokuPVP;
import com.cookie.chess.tileentity.TileEntityCChessPVP;
import com.cookie.chess.tileentity.TileEntityGomokuPVP;
import com.github.tartaricacid.touhoulittlemaid.tileentity.TileEntityGomoku;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class InitBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ChessPlus.MOD_ID);

    public static final DeferredRegister<BlockEntityType<?>>TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ChessPlus.MOD_ID);



    public static final RegistryObject<Block> GOMOKU_PVP = BLOCKS.register("gomoku_pvp", BlockGomokuPVP::new);
    public static final RegistryObject<Block> CCHESS_PVP = BLOCKS.register("cchess_pvp", BlockCChessPVP::new);




    public static RegistryObject<BlockEntityType<TileEntityGomokuPVP>> GOMOKUPVP_TE = TILE_ENTITIES.register("gomoku_pvp", () -> {
        return TileEntityGomokuPVP.TYPE;
    });;

    public static RegistryObject<BlockEntityType<TileEntityCChessPVP>> CCHESS_TE = TILE_ENTITIES.register("cchess_pvp", () -> {
        return TileEntityCChessPVP.TYPE;
    });;

}

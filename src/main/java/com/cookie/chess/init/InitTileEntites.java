package com.cookie.chess.init;

import com.cookie.chess.ChessPlus;
import com.github.tartaricacid.touhoulittlemaid.tileentity.TileEntityGomoku;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class InitTileEntites {
    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ChessPlus.MOD_ID);;
}

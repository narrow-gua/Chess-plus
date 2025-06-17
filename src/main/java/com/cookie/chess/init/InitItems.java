package com.cookie.chess.init;

import com.cookie.chess.ChessPlus;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class InitItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ChessPlus.MOD_ID);

    // 注册 gomoku_pvp 方块的物品形式
    public static final RegistryObject<Item> GOMOKU_PVP_ITEM = ITEMS.register(
            "gomoku_pvp", // 与方块同名
            () -> new BlockItem(
                    InitBlocks.GOMOKU_PVP.get(), // 指向已注册的方块
                    new Item.Properties() // 物品基础属性
            )
    );
}

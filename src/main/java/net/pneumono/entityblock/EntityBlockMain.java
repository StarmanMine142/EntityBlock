package net.pneumono.entityblock;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.pneumono.entityblock.content.EntityBlock;
import net.pneumono.entityblock.content.EntityBlockEntity;
import net.pneumono.entityblock.network.EntityBlockServerNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityBlockMain implements ModInitializer {
	public static final String MOD_ID = "entity_block";

	public static final Logger LOGGER = LoggerFactory.getLogger("Entity Block");

	public static final Block ENTITY_BLOCK = Registry.register(
			BuiltInRegistries.BLOCK,
			id("entity_block"),
			new EntityBlock(BlockBehaviour.Properties.of()
					.mapColor(MapColor.COLOR_LIGHT_GRAY)
					.requiresCorrectToolForDrops()
					.strength(-1.0F, 3600000.0F)
					.noLootTable()
					.setId(ResourceKey.create(Registries.BLOCK, id("entity_block")))
			)
	);

	public static final BlockEntityType<EntityBlockEntity> ENTITY_BLOCK_ENTITY = Registry.register(
			BuiltInRegistries.BLOCK_ENTITY_TYPE,
			id("entity_block"),
			FabricBlockEntityTypeBuilder.create(EntityBlockEntity::new, ENTITY_BLOCK).build()
	);

	public static final Item ENTITY_BLOCK_ITEM = Registry.register(
			BuiltInRegistries.ITEM,
			id("entity_block"),
			new GameMasterBlockItem(ENTITY_BLOCK, new Item.Properties()
					.rarity(Rarity.EPIC)
					.useBlockDescriptionPrefix()
					.setId(ResourceKey.create(Registries.ITEM, id("entity_block"))))
	);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Entity Block");
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.OP_BLOCKS).register(entries -> entries.addAfter(Items.STRUCTURE_BLOCK, ENTITY_BLOCK_ITEM));
		EntityBlockServerNetworking.initialize();
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
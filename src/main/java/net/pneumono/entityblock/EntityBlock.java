package net.pneumono.entityblock;

import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityBlock implements ModInitializer {
	public static final String MOD_ID = "entity_block";

	public static final Logger LOGGER = LoggerFactory.getLogger("Entity Block");

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Entity Block");
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
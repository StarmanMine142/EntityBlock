package net.pneumono.entityblock;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.pneumono.entityblock.content.EntityBlockScreen;
import net.pneumono.entityblock.network.EditEntityBlockPayload;

public class EntityBlockClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(
				EditEntityBlockPayload.START_ID,
				(payload, context) -> context.client().setScreen(
						new EntityBlockScreen(
								payload.pos(),
								payload.entityType(),
								payload.nbt(),
								payload.offset(),
								payload.finalState()
						)
				)
		);
	}
}
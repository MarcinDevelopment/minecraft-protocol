package ru.enke.minecraft.protocol.packet.server.game.entity

import io.netty.buffer.ByteBuf
import ru.enke.minecraft.protocol.packet.*

object UpdateHealthPacket : Packet<UpdateHealth> {

    override fun write(message: UpdateHealth, buffer: ByteBuf) {
        buffer.writeFloat(message.health)
        buffer.writeVarInt(message.food);
        buffer.writeFloat(message.saturation);
    }

    override fun read(buffer: ByteBuf): UpdateHealth {
        val health = buffer.readFloat();
        val food = buffer.readVarInt();
        val saturation = buffer.readFloat();
        return UpdateHealth(health, food, saturation);
    }
}

data class UpdateHealth(val health: Float, val food: Int, val saturation: Float) : PacketMessage

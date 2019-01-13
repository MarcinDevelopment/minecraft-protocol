package ru.enke.minecraft.protocol.packet.server.game.location

import io.netty.buffer.ByteBuf
import ru.enke.minecraft.protocol.packet.Packet
import ru.enke.minecraft.protocol.packet.PacketMessage
import ru.enke.minecraft.protocol.packet.readVarInt
import ru.enke.minecraft.protocol.packet.writeVarInt

object EntityLookPacket : Packet<EntityLook> {

    override fun write(message: EntityLook, buffer: ByteBuf) {
        buffer.writeVarInt(message.id)
        buffer.writeByte((message.yaw  * 256 / 360).toInt())
        buffer.writeByte((message.pitch * 256 / 360).toInt())
        buffer.writeBoolean(message.ground)
    }

    override fun read(buffer: ByteBuf): EntityLook {
        val id = buffer.readVarInt()
        val yaw = buffer.readByte() * 360 / 256f
        val pitch = buffer.readByte() * 360 / 256f
        val ground = buffer.readBoolean()

        return EntityLook(id, yaw, pitch, ground)
    }
}

data class EntityLook(val id: Int, val yaw: Float, val pitch: Float, val ground: Boolean) : PacketMessage
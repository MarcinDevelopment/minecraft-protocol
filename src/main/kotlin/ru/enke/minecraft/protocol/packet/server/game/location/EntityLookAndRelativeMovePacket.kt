package ru.enke.minecraft.protocol.packet.server.game.location

import io.netty.buffer.ByteBuf
import ru.enke.minecraft.protocol.packet.Packet
import ru.enke.minecraft.protocol.packet.PacketMessage
import ru.enke.minecraft.protocol.packet.readVarInt
import ru.enke.minecraft.protocol.packet.writeVarInt

object EntityLookAndRelativeMovePacket : Packet<EntityLookAndRelativeMove> {

    override fun write(message: EntityLookAndRelativeMove, buffer: ByteBuf) {
        buffer.writeVarInt(message.id)
        buffer.writeShort((message.x * 4096).toInt())
        buffer.writeShort((message.y * 4096).toInt())
        buffer.writeShort((message.z * 4096).toInt())
        buffer.writeByte((message.yaw  * 256 / 360).toInt())
        buffer.writeByte((message.pitch * 256 / 360).toInt())
        buffer.writeBoolean(message.ground)
    }

    override fun read(buffer: ByteBuf): EntityLookAndRelativeMove {
        val id = buffer.readVarInt()
        val x = (buffer.readShort() / 4096).toDouble()
        val y = (buffer.readShort() / 4096).toDouble()
        val z = (buffer.readShort() / 4096).toDouble()
        val yaw = buffer.readByte() * 360 / 256f
        val pitch = buffer.readByte() * 360 / 256f
        val ground = buffer.readBoolean()

        return EntityLookAndRelativeMove(id, x, y, z, yaw, pitch, ground)
    }
}

data class EntityLookAndRelativeMove(val id: Int, val x: Double, val y: Double, val z: Double,
                                     val yaw: Float, val pitch: Float, val ground: Boolean) : PacketMessage
package ru.enke.minecraft.protocol.packet.server.game.player

import io.netty.buffer.ByteBuf
import ru.enke.minecraft.protocol.packet.*

object setExperiencePacket : Packet<setExperience> {

    override fun write(message: setExperience, buffer: ByteBuf) {
        buffer.writeFloat(message.bar) //0-1
        buffer.writeVarInt(message.level)
        buffer.writeVarInt(message.total)
    }

    override fun read(buffer: ByteBuf): setExperience {
        val bar = buffer.readFloat();
        val level = buffer.readVarInt();
        val total = buffer.readVarInt();
        return setExperience(bar, level, total);
    }
}
data class setExperience(val bar : Float, val level : Int, val total : Int) : PacketMessage

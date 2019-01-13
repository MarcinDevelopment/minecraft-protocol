package ru.enke.minecraft.protocol.packet.server.game

import io.netty.buffer.ByteBuf
import ru.enke.minecraft.protocol.packet.*
import ru.enke.minecraft.protocol.packet.data.game.GameState

object ServerChangeGameStatePacket : Packet<ChangeGameState> {

    override fun write(message: ChangeGameState, buffer: ByteBuf) {
        buffer.writeEnum(message.reason)
        buffer.writeFloat(message.value)
    }

    override fun read(buffer: ByteBuf): ChangeGameState {
        val reason = buffer.readEnum<GameState>()
        val value = buffer.readFloat()
        return ChangeGameState(reason, value)
    }
}

data class ChangeGameState(val reason : GameState, val value : Float) : PacketMessage

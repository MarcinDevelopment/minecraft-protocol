package ru.enke.minecraft.protocol.packet.data.game

data class ItemStack(val id: Int, val quantity: Int, val metadata: Int, val state: ByteArray? = null)

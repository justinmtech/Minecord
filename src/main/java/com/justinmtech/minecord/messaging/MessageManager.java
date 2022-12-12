package com.justinmtech.minecord.messaging;

import com.justinmtech.minecord.messaging.errors.ChannelNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import java.util.Optional;

/**
 * Deliver notifications to both the Discord and Minecraft server
 */
public class MessageManager {
    private final TextChannel textChannel;

    public MessageManager(TextChannel textChannel) {
        this.textChannel = textChannel;
    }

    /**
     * @param message Message to send
     * @param dest Destination server(s)
     * @param type Message type
     * @param sound Bukkit Sound
     */
    public void sendMessage(@NotNull String message, @NotNull MessageDestination dest, @NotNull NotifyType type, @NotNull Sound sound) {
        if (dest.equals(MessageDestination.MINECRAFT)) {
            sendMinecraftNotification(message, type, sound);
        } else if (dest.equals(MessageDestination.DISCORD)) {
            sendDiscordMessage(message, type);
        } else if (dest.equals(MessageDestination.ALL)) {
            sendMinecraftNotification(message, type, sound);
            sendDiscordMessage(message, type);
        }
    }

    /**
     * @param message Message to send
     * @param dest Destination server(s)
     * @param type Message type
     */
    public void sendMessage(@NotNull String message, @NotNull MessageDestination dest, @NotNull NotifyType type) {
        if (dest.equals(MessageDestination.MINECRAFT)) {
            sendMinecraftNotification(message, type, null);
        } else if (dest.equals(MessageDestination.DISCORD)) {
            sendDiscordMessage(message, type);
        } else if (dest.equals(MessageDestination.ALL)) {
            sendMinecraftNotification(message, type, null);
            sendDiscordMessage(message, type);
        }
    }

    /**
     * @param message Message to send
     * @param type Message type
     * @param sound Bukkit Sound
     */
    public void sendMinecraftNotification(@NotNull String message, @NotNull NotifyType type, @Nullable Sound sound) {
        if (type.equals(NotifyType.NORMAL)) {
            Bukkit.broadcastMessage(message);
        } else {
            if (sound != null) sendMinecraftSoundToOnlinePlayers(sound);
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage(message);
            Bukkit.broadcastMessage("");
        }
    }

    /**
     * @param sound Bukkit Sound
     */
    public void sendMinecraftSoundToOnlinePlayers(@Nullable Sound sound) {
        if (sound == null) return;
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), sound, 1, 1);
        }
    }

    /**
     * @param message Message to send
     * @param type Message type
     */
    public void sendDiscordMessage(@NotNull String message, @NotNull NotifyType type) {
        String messageStripped = ChatColor.stripColor(message);
        if (type.equals(NotifyType.NORMAL)) {
            sendDiscordTextNotification(messageStripped);
        } else {
            sendDiscordCodeNotification(messageStripped);
        }
    }

    /**
     * @param message Message to send
     */
    public void sendDiscordTextNotification(@NotNull String message) {
        new MessageBuilder()
                .append(message)
                .send(getTextChannel());
    }

    /**
     * @param message Message to send
     */
    public void sendDiscordCodeNotification(@NotNull String message) {
        new MessageBuilder()
                .appendCode("", message)
                .send(getTextChannel());
    }

    /**
     * @param discordApi Discord api object
     * @param channelId A String of numbers (right click a channel in developer mode and copy ID to get it)
     * @return TextChannel
     * @throws ChannelNotFoundException Throw ChannelNotFoundException with message
     */
    public static TextChannel getTextChannel(@NotNull DiscordApi discordApi, @NotNull String channelId) throws ChannelNotFoundException {
        if (channelId.equals("")) throw new ChannelNotFoundException("You have the discord bot enabled, but the text channel is invalid.");
        Optional<TextChannel> textChannel = discordApi.getTextChannelById(channelId);
        if (textChannel.isEmpty()) throw new ChannelNotFoundException("You have the discord bot enabled, but the text channel is invalid.");
        return textChannel.get();
    }

    private TextChannel getTextChannel() {
        return textChannel;
    }
}

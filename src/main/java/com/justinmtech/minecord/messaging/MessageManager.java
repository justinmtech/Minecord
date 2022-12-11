package com.justinmtech.minecord.messaging;

import com.justinmtech.minecord.messaging.errors.ChannelNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import java.util.Optional;

//Deliver notifications to both the Discord and Minecraft server
public class MessageManager {
    private final TextChannel textChannel;

    public MessageManager(TextChannel textChannel) {
        this.textChannel = textChannel;
    }

    public void sendNotification(String message, MessageDestination dest, NotifyType type, Sound sound) {
        if (dest.equals(MessageDestination.MINECRAFT)) {
            sendMinecraftNotification(message, type, sound);
        } else if (dest.equals(MessageDestination.DISCORD)) {
            sendDiscordMessage(message, type);
        } else if (dest.equals(MessageDestination.ALL)) {
            sendMinecraftNotification(message, type, sound);
            sendDiscordMessage(message, type);
        }
    }

    public void sendMinecraftNotification(String message, NotifyType type, Sound sound) {
        if (type.equals(NotifyType.NORMAL)) {
            Bukkit.broadcastMessage(message);
        } else {
            if (sound != null) sendMinecraftSoundToOnlinePlayers(sound);
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage(message);
            Bukkit.broadcastMessage("");
        }
    }

    public void sendMinecraftSoundToOnlinePlayers(Sound sound) {
        if (sound == null) return;
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), sound, 1, 1);
        }
    }

    public void sendDiscordMessage(String message, NotifyType type) {
        String messageStripped = ChatColor.stripColor(message);
        if (type.equals(NotifyType.NORMAL)) {
            sendDiscordTextNotification(messageStripped);
        } else {
            sendDiscordCodeNotification(messageStripped);
        }
    }

    public void sendDiscordTextNotification(String message) {
        new MessageBuilder()
                .append(message)
                .send(getTextChannel());
    }

    public void sendDiscordCodeNotification(String message) {
        new MessageBuilder()
                .appendCode("", message)
                .send(getTextChannel());
    }

    public static TextChannel getTextChannel(DiscordApi discordApi, String channelId) throws ChannelNotFoundException {
        if (channelId.equals("")) throw new ChannelNotFoundException("You have the discord bot enabled, but the text channel is invalid.");
        Optional<TextChannel> textChannel = discordApi.getTextChannelById(channelId);
        if (textChannel.isEmpty()) throw new ChannelNotFoundException("You have the discord bot enabled, but the text channel is invalid.");
        return textChannel.get();
    }

    private TextChannel getTextChannel() {
        return textChannel;
    }
}

# Minecord
Javacord Messaging utility for Minecraft bot plugins. 

Make a message in one format and one place, and send it to a Discord server and Minecraft server at once. 
No more Discord message building for simple messages!

Simply instantiate MessageManager with a TextChannel like this:

TextChannel textChannel = MessageManager.getTextChannel("channelIdHere");
MessageManager messageManager = new MessageManager(textChannel);
messageManager.sendMessage("Hi", MessageDestination.ALL, NotifyType.NORMAL, null);

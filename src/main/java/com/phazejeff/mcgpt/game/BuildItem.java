package com.phazejeff.mcgpt.game;

import java.util.List;

import com.phazejeff.mcgpt.openai.Chat;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class BuildItem extends Item {

    private Chat chat;

    public BuildItem(Settings settings) {
        super(settings);
    }
    
    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public ItemStack getItemStack(List<String> messages, int x, int y, int z) {
        ItemStack itemStack = new ItemStack(this);

        NbtCompound nbt = new NbtCompound();

        nbt.putInt("x", x);
        nbt.putInt("y", y);
        nbt.putInt("z", z);

        nbt.putInt("size", messages.size());
        for (int i=0; i < messages.size(); i++) {
            nbt.putString(String.valueOf(i), messages.get(i));
        }
        itemStack.setNbt(nbt);

        return itemStack;
    }

    public ItemStack updateItemStack(NbtCompound oldNbt, List<String> messages) {
        ItemStack newItemStack = getItemStack(messages, oldNbt.getInt("x"), oldNbt.getInt("y"), oldNbt.getInt("z"));
        return newItemStack;
    }
}


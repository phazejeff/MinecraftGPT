# Minecraft GPT
A fabric mod that allows ChatGPT to build structures

This uses gpt3.5, I found that gpt4 was incredibly slow, expensive, and didn't yield any better results.

### Usage

- First, run `/setkey [key]` where `key` is your [OpenAI key](https://platform.openai.com/account/api-keys). 

- Then, you can run `/build [prompt]` where you can ask it to build something. It will build it at the location you are looking.

- After, you will be given an item with default textures, with the prompt as the name. If you want to tell it to make any edits to the structure, hold the item and use `/edit [prompt]` to make changes. It will take context from the previous edits made, so you can talk to it as if it was like a chat.
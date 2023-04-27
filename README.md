# Minecraft GPT
A fabric mod that allows ChatGPT to build structures. You can download [Fabric here](https://fabricmc.net/use/installer/)

### Usage

- First, run `/setkey [key]` where `key` is your [OpenAI key](https://platform.openai.com/account/api-keys). 
    - note: The OpenAI API does cost money, although I think you get free credits on the initial signup. From my testing, it costs around 1 cent for every 3-5 builds for GPT3.5. If you want to use GPT4, [more info is below](#GPT4)

- Then, you can run `/build [prompt]` where you can ask it to build something. It will build it at the location you are looking.
    - note: while it is generating, the world will freeze similar to server lag. this is normal because it's running in the main thread and not a seperate thread. i dont care.


- After, you will be given an item with default textures, with the prompt as the name. If you want to tell it to make any edits to the structure, hold the item and use `/edit [prompt]` to make changes. It will take context from the previous edits made, so you can talk to it as if it was like a chat.

### GPT4
By default, this is set to use GPT3.5 (this is the ChatGPT version that most are familiar with). I personally found that GPT4 is significantly slower, more expensive (Around 5-10 cents per build), and didn't generate any better results compared to GPT3.5. If you want to try GPT4, you can toggle it on/off by simply typing `/gpt4`

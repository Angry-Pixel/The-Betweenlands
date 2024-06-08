<html>
<body>
<a href="https://github.com/Angry-Pixel/The-Betweenlands/actions"><img src="https://github.com/Angry-Pixel/The-Betweenlands/actions/workflows/build.yml/badge.svg" alt="Build Status"/></a>
<a href="https://minecraft.curseforge.com/projects/angry-pixel-the-betweenlands-mod"><img src="http://cf.way2muchnoise.eu/short_angry-pixel-the-betweenlands-mod.svg" alt="CurseForge Project"/></a>

<a href="http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/1441135-the-betweenlands-a-dark-hostile-environment-1-10-2"><img src="http://i.imgur.com/Q3JTGqu.png" alt="The Betweenlands"/></a>

<p align="center">
  <b>The Betweenlands is a mod developed by the 
  <a href="https://github.com/Angry-Pixel"><img src='http://svgshare.com/i/i6.svg' width=10% height=10%></a>
  modding group for the indie sandbox construction game Minecraft. This large and expansive mod focuses on a whole new fully fleshed-out  dimension for Minecraft that adds a plethora of exciting new content.<br><br>
  The Betweenlands dimension is a dark, swampy realm crawling with strange, monstrous creatures. It is the dimension that the majority of the mod revolves around.<br><br></b>
</p>

<p align="center">
  <img src="http://i.imgur.com/5AgEXax.png">
</p>

<p align="center"><b>Creating Issues</b></p>

When reporting problems you must...
<ul>
  <li>use a descriptive and concise title</li>
  <li>include the version of Forge and the Betweenlands you are using</li>
  <li>specify whether it happens in singleplayer or multiplayer (and if so, what server is used, e.g. Vanilla, Thermos, Sponge etc.)</li>
  <li>provide a detailed and comprehensible description, if possible also include instructions on how to reproduce it</li>
  <li>include a link to your mod pack so we can try to reproduce it, otherwise if no such link is available provide a list of <b>all</b> mods you have installed</li>
  <li>upload <b>the full</b> crash log if available and include a link to said crash log</li>
  <li>not post multiple problems in one issue. Create multiple issues instead</li>
</ul>
<p>
Feel free to include pictures of the problem you are having, if that helps.
You may also create issues for suggestions. If you have multiple suggestions please post them in one single issue.
<p>
If you do not follow these rules your issues will be closed and ignored.
  
**But before you start writing an issue, please read up on the following common issues**
</p>
<br>

<p align="center">
  <img src="http://i.imgur.com/5AgEXax.png">
</p>

<p align="center"><b>Contributing</b></p>

If you intend to contribute via pull request please read our [contributing instructions](CONTRIBUTING.md).

<br>

<p align="center">
  <img src="http://i.imgur.com/5AgEXax.png">
</p>

<p align="center"><b>Common Issues</b></p>

<p align="center">

<ol>
  <li><b>Common issues caused by OptiFine</b>
    <ol>
      <br><li><b>My screen is totally black!</b><br>
       This is a common issue when OptiFine is installed.<br>
       The "Fast Render" option in OptiFine causes our shaders to not work.<br>
       To fix this problem you can either set "Fast Render" to OFF<br>
       or disable the Betweenlands shaders in the Betweenlands config.
      </li>
      <br><li><b>Everything is invisible!</b><br>
       Newer versions of OptiFine have built-in shaders that<br>
       don't work well with our shaders. You can either disable the<br>
       Betweenlands shaders or disable the OptiFine shaders by going to the<br>
       main Minecraft directory, opening optionsshaders.txt and changing<br>
       <code>shaderPack=...</code> to <code>shaderPack=OFF</code>.
      </li>
      <br><li><b>The mod crashes on startup in 1.7.10</b><br>
       The 1.7.10 version of the Betweenlands is incompatible with some<br>
       OptiFine versions. Try <code>OptiFine HD U D3</code> instead which should<br>
       work.
      </li>
    </ol>
  </li>
  <br><li><b>Other common issues</b>
    <ol>
      <br><li><b>The water is only yellow/brown/beige</b><br>
        Download the latest recommended version of Forge.
      </li>
      <br><li><b>Everything is black, invisible or looks strange</b><br>
        If you have any other mods installed that add shaders, make sure you either<br>
        disable the shaders of those mods, or disable the Betweenlands shaders (see 1.1)
      </li>
      <br><li><b>The sounds or music sometimes stop working</b><br>
        That's something we're still working on. If this happens to you, you<br>
        can fix it by pressing F3+T (might freeze the game for a few seconds)<br>
        instead of restarting the game.
      </li>
      <br><li><b>This mod makes my game lag horribly!!1!</b><br>
        Try disabling the Betweenlands shaders (see 1.1). Some graphics cards,<br>
        especially integrated ones such as Intel HD Graphics, cannot handle the<br>
        shaders well.
      </li>
    </ol>
  </li>
</ol>
</p>

<br>

<p align="center">
  <img src="http://i.imgur.com/5AgEXax.png">
</p>

<p align="center"><b>For Developers</b></p>
<ul>
  <li>Creating a release build: Tag the last commit on the Minecraft version's main branch to include in the build with <code>v&lt;version&gt;-release</code> (replace <code>&lt;version&gt;</code> with the version to build).</li>
  <li>Currently unused assets can be found here: https://drive.google.com/drive/folders/1iQpbcuUen6Xa7a06Zor6wYVYuY4mnp4Q.</li>
</ul>

<br>

<p align="center">
  <img src="http://i.imgur.com/5AgEXax.png">
</p>

<p align="center"><b>Features Include</b></p>
<ul>
  <li>A complete independent survival experience with hours of gameplay</li>
  <li>A whole new dimension to survive in</li>
  <li>Many new creatures and monsters to fight</li>
  <li>Bosses to defeat</li>
  <li>Plenty of unique biomes and structures to explore and loot, from towering fortresses to scattered underground ruins</li>
  <li>An extensive herblore system that allows you to create over 30 unique infusions from 14 different aspects found in plants</li>
  <li>A unique farming system with several new crop types</li>
  <li>Over 300 new blocks to build with, including various machine blocks and a whole lot of plant life</li>
  <li>Over 350 new items, including lots of unique weapons and loot, complete tool and armour sets, plenty of food items, new raw materials, scraps of lore and more</li>
  <li>Over 250 new sounds, including 7 immersive ambient tracks and 33 music tracks</li>
  <li>Lots of new mechanics, including food sickness, corrosion of tools, decay of the player, and a combat circle revolving around 3 new gem items</li>
  <li>Randomly occuring events, including changes in the weather as well as sometimes more supernatural occurrences</li>
  <li>Special built-in custom shader effects to make the worlds look even prettier</li>
  <li>Multiplayer compatibility so you can survive with your friends</li>
  <li>...and much, more more!</li>
</ul><br>

<p align="center">
  <img src="http://i.imgur.com/5AgEXax.png">
</p>

<p align="center"><b>TERMS AND CONDITIONS</b></p>

> 0. USED TERMS
> MOD - modification, plugin, a piece of software that interfaces with the Minecraft client to extend, add, change or remove original capabilities.
> MOJANG - Mojang AB
> OWNER - , Original author(s) of the MOD. Under the copyright terms accepted when purchasing Minecraft (http://www.minecraft.net/copyright.jsp) the OWNER has full rights over their MOD despite use of MOJANG code.
> USER - End user of the mod, person installing the mod.

> 1. LIABILITY
> THIS MOD IS PROVIDED 'AS IS' WITH NO WARRANTIES, IMPLIED OR OTHERWISE. THE OWNER OF THIS MOD TAKES NO RESPONSIBILITY FOR ANY DAMAGES INCURRED FROM THE USE OF THIS MOD. THIS MOD ALTERS FUNDAMENTAL PARTS OF THE MINECRAFT GAME, PARTS OF MINECRAFT MAY NOT WORK WITH THIS MOD INSTALLED. ALL DAMAGES CAUSED FROM THE USE OR MISUSE OF THIS MOD FALL ON THE USER.

> 2. USE
> Use of this MOD to be installed, manually or automatically, is given to the USER without restriction.

> 3. REDISTRIBUTION
> This MOD may only be distributed where uploaded, mirrored, or otherwise linked to by the OWNER solely with the exception of CurseForge modpacks. All other mirrors of this mod must have advance written permission from the OWNER under the joint agreement of the Angry Pixel modding group. ANY attempts to make money off of this MOD (selling, selling modified versions, adfly, sharecash, etc.) are STRICTLY FORBIDDEN, and the OWNER may claim damages or take other action to rectify the situation. In any cases, full credit to the Angry Pixel modding group must be given.

> 4. DERIVATIVE WORKS/MODIFICATION
> This mod is provided freely and may be decompiled and modified for private use, either with a decompiler or a bytecode editor. Public distribution of modified versions of this MOD require advance written permission of the OWNER and may be subject to certain terms. Use of small sections of code is allowed as long as the result is transformation. The code may also be viewed and learned from for educational purposes.

</body>
</html>

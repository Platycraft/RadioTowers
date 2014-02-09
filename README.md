RadioTowers
===========

Plugin that adds radiotowers to minecraft.


**Author:** Thomas Richner

**License:** [GNU General Public License, version 3 (GPL-3.0)](http://opensource.org/licenses/gpl-3.0)

Credit to redinzane for the configuration file handling.


### How to build a radio tower:


```
                      #   ^
                      #   |
 iron fences    ->    #   |
                      #   | height/antenna gain
                      #   |
                      #   |
                      #   |
                      #   v
 redstone torch ->   \▓|  <- wall sign with message
                      ^
                      |
                obsidian block
                
```
                  
The antenna (iron fences) must have a minimum height, usually this is around 7 fences. This depends on the configuration of the plugin. The higher the antenna, the higher the antenna gain. Usually an antenna with height 40 gives you the maximum antenna gain and therefore the biggest range. But again, this depends on the configuration of the plugin.
If you successfully built a radio tower, you will see a little effect around the obsidian block.
The antenna can be shutdown with redstone power, as long as the redstone torch is lit, the antenna is sending, if you power the obsidian block, the antenna will stop broadcasting.
An antenna has a distinct frequency, each 32x32x32 cube is assigned one frequency. So if you build an antenna in the same cube as before, it will have the same frequency.


### How to receive a radio signal:

In order to reveive a signal, you must held a radio in your hand. A radio is a named vanilla minecraft compass, the name depends on the plugin configuration. If the configuration is set to a colored name, you can only obtain it by looting in survival mode. If it has no color, you can simply name it with an anvil.
Upon receiving a signal, your radio will display the sending frequency, the signal strenght in dBm and the broadcasted message.
Per right clicking in the air with the radio in hand, you can tune your radio to a specific tower. If the reception is good enough, the compass will direct you to the antenna.


### Configuration of the plugin

To get an idea of what each option does, please look at the [config.yml](https://github.com/trichner/RadioTowers/blob/master/config.yml).

### Some bonus features:
- autosaves towers to file asynchronously
- checks validity of towers before broadcasting, minimizing risk of glitches
- order in which tower is built doesn't matter
- fancy effect for tower completion
- realistic signal strength based on distance to tower
- based on the Bukkit Event System

### Wow, such plugin, much coding
DAthmosSZLtk6LC1wJVcgdXchPXuhb1a9E


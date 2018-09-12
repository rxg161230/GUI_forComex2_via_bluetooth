# GUI_forComex2_via_bluetooth
Java GUI to control a thigh exoskeleton named Comex2 over the Bluetooth v4 by sending over specific command values  which are specific to a particular action of handling Torque- be it Passive mode, Quasi-stiffness, Manual or Body Weight Support mode.
The software interface is made using Eclipse Photon which is already installed in the corner computer of the Pod, eclipse can be easily downloaded on any other computer or laptop through Google search as well. This code can run on any version of Eclipse so prefer to download the latest version.
The main point to be noted is that control information is sent to the C2000 in the form of packets having a particular pattern. 
The C2000 has its communication reception designed such that it first checks whether the packet received is legit or not. For that, the C2000 should make sure that the first byte that is being received should be 0x69 in HEX or 105 in Decimal.
This I found out after analyzing Jack’s App program which has such specific data being sent over the Bluetooth.
It is must that packet contains atleast 3 bytes- Start byte, Command, length of arguments and then adding to  that there would be the arguments needed to added in the line according to the command chosen.
For Power, the packet will be-
Start byte= 0x69
Command for STO_HIGH=0x01
Length of arguments =0 (as no specific work of the command)

So Enable and STO have to send together so we need the enable packet added too.
Enable-
Start byte =0x69
Command for Enable_high = 0x02
Length =0 as of now.
If seen on an oscilloscope or logic analyzer the digital signal shown as a result for the Power button will be-
[0x69,0x01,0x00,0x69,0x02,0x00] <- this the packet for power on which will be sent over the Bluetooth  when the power button is pressed.
Similarly there are similar patterns for each button pressed.
The results matched with the packet.h header file in cpu2main.c file in Code Composure Studio which has all the functionality for the exoskeleton.
Power on -> 0x69,0x01,0x00,0x69,0x02,0x00
Stop -> 0x69,0x81,0x00,0x69x0x82,0x00
The first 3 bytes are for STO_LOW and the next 3 bytes here are for ENABLE_LOW. So the command for STO_LOW is 0x81 which matches with the one defined in packet.h. This can be seen when the power off button is pressed both on the app or the java interface.
Passive Mode on -> 0x69, 0x88, 0x04, 0x00, 0x00, 0x00, 0x00
Here once the first byte is detected, the command is SET_CONTROLLER which matches 0x88 as in packet.h. Next comes the length of the arguments sent in the packet. We are sending an argument length of 4 byte arrays which is why length is an interger =4, given as the third byte. Then the args[0]=0 as Passive is case 0 in the switch case choice of Passive, Quasi-Stiffness, manual and BWS. Hence the packet for passive looks like that.
Quasi-stiffness Mode on -> 0x69, 0x88, 0x04, 0x01, 0x00, 0x00, 0x00
Here the packet is similar to the packet of Passive mode with a difference that when the switch case of the 4 mode comes it is 1 instead of 0 as Quasi-stiffness in the second element in the switch-case situation. As Quasi-stiffness mode will generate torque on other conditions available, it seems that the remaining parts args[] won’t matter much.
Manual Mode on -> 0x69, 0x88, 0x04, 0x03, 0x00, 0x00, 0x00
Manual Mode packet would go to a next stage as we need to send the manual torque into the buffer of the C2000 to be read correctly and then transferred over the CAN. Hence after choosing manual mode the next step would be to set a motion plan by inputting manual torque for which when the torque value is chosen between 0.5 to 6.5, the selected button will transfer the packet accordingly-
Set Motion Plan -> 0x69, 0xAA, 0x04,(integer converted to 4 bytes)
Once manual mode is set, the next step is to give in Manual torque. As we know an integer has 32 bits, so which sums up to 4 bytes for eg-
5 in bits will be- 0000 0000 0000 0101 which as a byte will become- [0x00, 0x00, 0x00, 0x05]
Hence the packet being sent over Bluetooth for a torque input of 5 selected from the drop down menu will be will be- 0x69, 0xAA, 0x04, 0x00, 0x00, 0x00, 0x05
The Comex 2000 side will be reading the values inputted accordingly.
Body weight support mode -> 0x69, 0x88, 0x04, 0x04, 0x00, 0x00, 0x00
Although the control scheme for Body weight support has not been written and compiled in a code yet, I included the button for that mode to get selected, how to send manual data for BWS mode is not included in the code and has to be added once the control scheme is ready.

Moving on to using the code that I wrote for the Java interface-
In Java JFrame is used to make GUI which would be similar to any Hardware control module.
There are 3 main external jar files that must be include in the libraries for the GUI to show no errors, these jar files are pre-created libraries to support robust Serial communication.
These there jar files will be in my folder and make sure to import all there to the project. 

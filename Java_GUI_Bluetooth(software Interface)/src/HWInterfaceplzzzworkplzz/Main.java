package HWInterfaceplzzzworkplzz;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.fazecast.jSerialComm.SerialPort;

public class Main {
	public static SerialPort chosenPort; //for serial communication
	private static final byte START_BYTE = 0x69;
		public static void main(String[] args) {
	    
		// create and configure the window
		JFrame window = new JFrame();
		window.setTitle("Comex2 Control GUI");
		window.setSize(1200, 200);
		window.setLayout(new BorderLayout()); //splits up the layout into regions
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//populate the window
		//drop down to choose port and connect button to connect
		//for drop down jCombo to contain strings- Port list
		JComboBox<String> portList = new JComboBox<String>();
		JButton connectButton = new JButton("Connect");
		JButton powerButton = new JButton("Power Up");
		JButton stopButton =new JButton("Stop");
		JButton Passive = new JButton("Passive Mode");
		JButton QuasiStiffness = new JButton("Quasi-Stiffness Mode");
		JButton Manual = new JButton("Manual Mode");
		JButton BodyWeightSupport =new JButton("BWS Mode");
		String[] Torque = {"1","2","3","4","5","6","7","8","9","10"};
		JComboBox<String> ManualTorque =new JComboBox<String>(Torque);
		JLabel lbl = new JLabel();

		//string here is default text on button
				//in layout manager each region can have only one object
				//take multiple objects on a panel- top panel- jpanel
				JPanel topPanel = new JPanel();
				JPanel centerPanel =new JPanel();
			//	JPanel bottomPanel =new JPanel();
				JPanel leftPanel =new JPanel();
				JPanel rightPanel =new JPanel();
				//now can place the drop box and button together on the panel
				topPanel.add(portList);
				topPanel.add(connectButton);
				window.add(topPanel, BorderLayout.NORTH);
				//populate drop box with list of ports
				//method in serial library calls all ports
				SerialPort[] portNames = SerialPort.getCommPorts();
				for(int i = 0; i < portNames.length; i++)
					portList.addItem(portNames[i].getSystemPortName());
				
				centerPanel.add(powerButton);
				centerPanel.add(stopButton);
				window.add(centerPanel, BorderLayout.CENTER);
				
				leftPanel.add(Passive);
				leftPanel.add(QuasiStiffness);
				window.add(leftPanel, BorderLayout.WEST);
				rightPanel.add(Manual);
				rightPanel.add(ManualTorque);
				rightPanel.add(lbl);
				rightPanel.add(BodyWeightSupport);
				window.add(rightPanel, BorderLayout.EAST);
				
				
				connectButton.addActionListener(new ActionListener(){
					//creating an anonymous object and then  defining it, define a method inside the object
					 
					@Override
					public void actionPerformed(ActionEvent arg0) {
						//need to create an object that represents the connected port
						if(connectButton.getText().equals("Connect")) {
							//attempt to connect to the serial port
							//getting the COM port value sand giving it to the getCommPort
							chosenPort = SerialPort.getCommPort(portList.getSelectedItem().toString());
							//now configure that serial port
							//the first argument is a timeout setting
							//Scanner is a a easy way to get text from a stream in this case a serial port
							//the next two are time out values, which set to 0 means wait till infinity
							chosenPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
							//give user try with other ports if not connected
							if(chosenPort.openPort()){
								connectButton.setText("Disconnect");
								portList.setEnabled(false);
							}
										}else{
							//disconnect from the serial port
							chosenPort.closePort();
							portList.setEnabled(true);
							connectButton.setText("Connect");
							powerButton.setText("Power Up");
							stopButton.setText("Stop");
							//series.clear();
							//x = 0;
						}
					}
					
				});
				
				powerButton.addActionListener(new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent e) {
							if(connectButton.getText().equals("Disconnect")){
							Thread thread = new Thread(){
						//define run method when you have a thread object
								@Override public void run() {
									if(powerButton.getText().equals("Power Up")){
										powerButton.setText("Powered");
										stopButton.setText("Stop");
									} 
									// wait after connecting, so the bootloader can finish
									//microcontroller resets as it loads from the USB as well
									try {Thread.sleep(100); } catch(Exception e) {}								
										
									//	byte[] STO = new byte[1];							 
									//	STO[0] = 0x01;
										byte[] STO = new byte[3];							 
										STO[0] = START_BYTE;
										STO[1] =0x01;
								        STO[2] = 0x00;
						        						        
						                byte[] Enable = new byte[3];
								        Enable[0] = START_BYTE;
								        Enable[1] = 0x02;
								        Enable[2] = 0x00;   
						//to send out byte data over the serial port we are using the DataOutputStream io operation         			      
						     		DataOutputStream output = new DataOutputStream(chosenPort.getOutputStream());
									try {
					                output.write(STO);
					               Thread.sleep(7,500);
					                output.write(Enable);
					                System.out.println(STO);
					                 System.out.println(Enable);
					                output.flush();
					                }  
									catch (InterruptedException |IOException e) {
								       	System.out.println("could not write to port");
									e.printStackTrace();
									}
									//now making sure that all other buttons are brought to default mode when this button is clicked
									if (stopButton.getText().equals("Stopped")){
									try {
										output.close();
									} catch (IOException e) {
										e.printStackTrace();
									}
									}else if(QuasiStiffness.getText().equals("Quasi Mode On")){
									try {
										output.close();
									} catch (IOException e) {
										e.printStackTrace();
									}
								}else if(Passive.getText().equals("Passive Mode On")){
									try {
										output.close();
									} catch (IOException e) {
										e.printStackTrace();
									}
								}else if(Manual.getText().equals("Manual Mode On")){
									try {
										output.close();
									} catch (IOException e) {
										e.printStackTrace();
									}
								}else if(BodyWeightSupport.getText().equals("BWS Mode On")){
									try {
										output.close();
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
								try {Thread.sleep(100); } catch(Exception e) {}
							}
					};
					thread.start();
				}
			}				
		});
		
		stopButton.addActionListener(new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent e) {
				if(powerButton.getText().equals("Powered")){
				Thread thread = new Thread(){
				//define run method when you have a thread object
				@Override public void run() {
				// wait after connecting, so the bootloader can finish
				try {Thread.sleep(100); } catch(Exception e) {}
		       //sending text 
				if(stopButton.getText().equals("Stop")){
					stopButton.setText("Stopped");
					powerButton.setText("Power Up");
					QuasiStiffness.setText("Quasi Mode");
					Passive.setText("Passive Mode");	
					Manual.setText("Manual Mode");
					BodyWeightSupport.setText("BWS Mode");
				}
				try {Thread.sleep(100); } catch(Exception e) {}								
				byte[] STO_LOW = new byte[3];							 
				STO_LOW[0] = START_BYTE;
				STO_LOW[1] =(byte) 0x81;
		        STO_LOW[2] = 0x00;
        						        
                byte[] Enable_LOW = new byte[3];
		        Enable_LOW[0] = START_BYTE;
		        Enable_LOW[1] = (byte) 0x82;
		        Enable_LOW[2] = 0x00;
        						        
     		DataOutputStream output = new DataOutputStream(chosenPort.getOutputStream());
			try {
            output.write(STO_LOW);
            Thread.sleep(7,500);
            output.write(Enable_LOW);
            System.out.println(STO_LOW);
            System.out.println(Enable_LOW);
            output.flush();
            }  
			catch (InterruptedException | IOException e) {
		       	System.out.println("could not write to port");
			e.printStackTrace();
			}
		}
	};
	thread.start();
	}
}});
	
	Passive.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {

				if(powerButton.getText().equals("Powered")){
					Thread thread = new Thread(){
					//define run method when you have a thread object
					@Override public void run() {
					try {Thread.sleep(100); } catch(Exception e) {}
     
					if(Passive.getText().equals("Passive Mode")){
						QuasiStiffness.setText("Quasi-Stiffness Mode");
						Passive.setText("Passive Mode On");	
						Manual.setText("Manual Mode");
						BodyWeightSupport.setText("BWS Mode");
				}
								
					try {Thread.sleep(100); } catch(Exception e) {}								
					byte[] PASSIVE = new byte[7];							 
					PASSIVE[0] = START_BYTE;
					PASSIVE[1] =(byte) 0x88;
					PASSIVE[2] = 0x04;
					PASSIVE[3] = 0x00;
					PASSIVE[4] = 0x00;
					PASSIVE[5] = 0x00;
					PASSIVE[6] = 0x00;
					
					DataOutputStream outputP = new DataOutputStream(chosenPort.getOutputStream());
					try {
			            outputP.write(PASSIVE);
			           	System.out.println(PASSIVE);
			           	Thread.sleep(7,500);
	                    outputP.flush();
			            }  
						catch (InterruptedException | IOException e) {
					       	System.out.println("could not write to port");
						e.printStackTrace();
						}
							
					}
				};
				thread.start();
			}
		
		}
			
	});
	
	QuasiStiffness.addActionListener(new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent e) {
		
			if(powerButton.getText().equals("Powered")){
				Thread thread = new Thread(){
				//define run method when you have a thread object
					@Override public void run() {
						// wait after connecting, so the bootloader can finish
						//microcontroller resets as it loads from the USB as well
						try {Thread.sleep(100); } catch(Exception e) {}
						//sending text 
						//give it output stream of our port
						if(QuasiStiffness.getText().equals("Quasi-Stiffness Mode")){
							QuasiStiffness.setText("Quasi Mode On");
							Passive.setText("Passive Mode");	
							Manual.setText("Manual Mode");
							BodyWeightSupport.setText("BWS Mode");
						}
						try {Thread.sleep(100); } catch(Exception e) {}								
						byte[] QUASI = new byte[7];							 
						QUASI[0] = START_BYTE;
						QUASI[1] =(byte) 0x88;
						QUASI[2] = 0x04;
						QUASI[3] = 0x01;
						QUASI[4] = 0x00;
						QUASI[5] = 0x00;
						QUASI[6] = 0x00;
						
						DataOutputStream outputQ = new DataOutputStream(chosenPort.getOutputStream());
						try {
				            outputQ.write(QUASI);
				           	System.out.println(QUASI);
				           	Thread.sleep(7,500);
		                    outputQ.flush();
				            }  
							catch (InterruptedException | IOException e) {
						       	System.out.println("could not write to port");
							e.printStackTrace();
							}			
					}
				};
				thread.start();
			}	
		}		
	});
	
	Manual.addActionListener(new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent e) {
				
			if(powerButton.getText().equals("Powered")){
				Thread thread = new Thread(){				
					//define run method when you have a thread object
					public void run() {
						// wait after connecting, so the bootloader can finish
						//microcontroller resets as it loads from the USB as well
						
						try {Thread.sleep(100); } catch(Exception e) {}
						//sending text 
						// enter an infinite loop that sends text to the C2000 via serial port
						//give it output stream of our port
						if(Manual.getText().equals("Manual Mode")){
							QuasiStiffness.setText("Quasi-Stiffness Mode");
							Passive.setText("Passive Mode");	
							Manual.setText("Manual Mode On");
							BodyWeightSupport.setText("BWS Mode");
							}
						try {Thread.sleep(100); } catch(Exception e) {}		
						
						byte[] MANUAL = new byte[7];							 
						MANUAL[0] =START_BYTE;
						MANUAL[1] =(byte) 0x88;
						MANUAL[2] = 0x04;
						MANUAL[3] = 0x03;
						MANUAL[4] = 0x00;
						MANUAL[5] = 0x00;
						MANUAL[6] = 0x00;
						
						DataOutputStream outputM = new DataOutputStream(chosenPort.getOutputStream());
						try {
				            outputM.write(MANUAL);
				           	System.out.println(MANUAL);
				           	Thread.sleep(7,500);
		                    outputM.flush();
				            }  
							catch (InterruptedException | IOException e) {
						       	System.out.println("could not write to port");
							e.printStackTrace();
							}									
					}
				};
				thread.start();
			}	
		}		
	});
	
	ManualTorque.addActionListener(new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource()== ManualTorque){
			
				@SuppressWarnings("unchecked")
				JComboBox<String> cb = (JComboBox<String>)e.getSource();
				String msg = (String)cb.getSelectedItem();
				byte[] SET_Motion = new byte[7];
				switch (msg){
		
				case "1": lbl.setText("You gave Torque Magnitute as 1" );
					SET_Motion[0] =START_BYTE;
					SET_Motion[1] =(byte) 0xAA;
					SET_Motion[2] = 0x04;
					SET_Motion[3] = 0x00;
					SET_Motion[4] = 0x00;
					SET_Motion[5] = 0x00;
					SET_Motion[6] = 0x01;
					DataOutputStream output1 = new DataOutputStream(chosenPort.getOutputStream());
					try {
						output1.write(SET_Motion);
						System.out.println(SET_Motion);
						Thread.sleep(7,500);
						output1.flush();
						}  
					catch (InterruptedException | IOException e1) {
				       	System.out.println("could not write to port");
					e1.printStackTrace();
					}	
				break;
			
				case "2": lbl.setText("You gave Torque Magnitute as 2" );
					SET_Motion[0] =START_BYTE;
					SET_Motion[1] =(byte) 0xAA;
					SET_Motion[2] = 0x04;
					SET_Motion[3] = 0x00;
					SET_Motion[4] = 0x00;
					SET_Motion[5] = 0x00;
					SET_Motion[6] = 0x02;
					DataOutputStream output2 = new DataOutputStream(chosenPort.getOutputStream());
					try {
						output2.write(SET_Motion);
						System.out.println(SET_Motion);
						Thread.sleep(7,500);
						output2.flush();
					}  
					catch (InterruptedException | IOException e1) {
						System.out.println("could not write to port");
						e1.printStackTrace();
				}
				break;
			
				case "3": lbl.setText("You gave Torque Magnitute as 3" );
					SET_Motion[0] =START_BYTE;
					SET_Motion[1] =(byte) 0xAA;
					SET_Motion[2] = 0x04;
					SET_Motion[3] = 0x00;
					SET_Motion[4] = 0x00;
					SET_Motion[5] = 0x00;
					SET_Motion[6] = 0x03;
					DataOutputStream output3 = new DataOutputStream(chosenPort.getOutputStream());
					try {
						output3.write(SET_Motion);
						System.out.println(SET_Motion);
						Thread.sleep(7,500);
						output3.flush();
					}  
					catch (InterruptedException | IOException e1) {
						System.out.println("could not write to port");
						e1.printStackTrace();
					}
				break;
				
				case "4": lbl.setText("You gave Torque Magnitute as 4" );
				SET_Motion[0] =START_BYTE;
				SET_Motion[1] =(byte) 0xAA;
				SET_Motion[2] = 0x04;
				SET_Motion[3] = 0x00;
				SET_Motion[4] = 0x00;
				SET_Motion[5] = 0x00;
				SET_Motion[6] = 0x04;
				DataOutputStream output4 = new DataOutputStream(chosenPort.getOutputStream());
				try {
					output4.write(SET_Motion);
					System.out.println(SET_Motion);
					Thread.sleep(7,500);
					output4.flush();
					}  
				catch (InterruptedException | IOException e1) {
			       	System.out.println("could not write to port");
				e1.printStackTrace();
				}
				break;
			
				case "5": lbl.setText("You gave Torque Magnitute as 5" );
				SET_Motion[0] =START_BYTE;
				SET_Motion[1] =(byte) 0xAA;
				SET_Motion[2] = 0x04;
				SET_Motion[3] = 0x00;
				SET_Motion[4] = 0x00;
				SET_Motion[5] = 0x00;
				SET_Motion[6] = 0x05;
				DataOutputStream output5 = new DataOutputStream(chosenPort.getOutputStream());
				try {
					output5.write(SET_Motion);
					System.out.println(SET_Motion);
					Thread.sleep(7,500);
					output5.flush();
					}  
				catch (InterruptedException | IOException e1) {
			       	System.out.println("could not write to port");
				e1.printStackTrace();
				}
				break;
			
				case "6": lbl.setText("You gave Torque Magnitute as 6" );
				SET_Motion[0] =START_BYTE;
				SET_Motion[1] =(byte) 0xAA;
				SET_Motion[2] = 0x04;
				SET_Motion[3] = 0x00;
				SET_Motion[4] = 0x00;
				SET_Motion[5] = 0x00;
				SET_Motion[6] = 0x06;
				DataOutputStream output6 = new DataOutputStream(chosenPort.getOutputStream());
				try {
					output6.write(SET_Motion);
					System.out.println(SET_Motion);
					Thread.sleep(7,500);
					output6.flush();
					}  
				catch (InterruptedException | IOException e1) {
			       	System.out.println("could not write to port");
				e1.printStackTrace();
				}
				break;
				case "7": lbl.setText("You gave Torque Magnitute as 7" );
				SET_Motion[0] =START_BYTE;
				SET_Motion[1] =(byte) 0xAA;
				SET_Motion[2] = 0x04;
				SET_Motion[3] = 0x00;
				SET_Motion[4] = 0x00;
				SET_Motion[5] = 0x00;
				SET_Motion[6] = 0x07;
				DataOutputStream output7 = new DataOutputStream(chosenPort.getOutputStream());
				try {
					output7.write(SET_Motion);
					System.out.println(SET_Motion);
					Thread.sleep(7,500);
					output7.flush();
					}  
				catch (InterruptedException | IOException e1) {
			       	System.out.println("could not write to port");
				e1.printStackTrace();
				}
				break;
				case "8": lbl.setText("You gave Torque Magnitute as 8" );
				SET_Motion[0] =START_BYTE;
				SET_Motion[1] =(byte) 0xAA;
				SET_Motion[2] = 0x04;
				SET_Motion[3] = 0x00;
				SET_Motion[4] = 0x00;
				SET_Motion[5] = 0x00;
				SET_Motion[6] = 0x08;
				DataOutputStream output8 = new DataOutputStream(chosenPort.getOutputStream());
				try {
					output8.write(SET_Motion);
					System.out.println(SET_Motion);
					Thread.sleep(7,500);
					output8.flush();
					}  
				catch (InterruptedException | IOException e1) {
			       	System.out.println("could not write to port");
				e1.printStackTrace();
				}
				break;
				case "9": lbl.setText("You gave Torque Magnitute as 9" );
				SET_Motion[0] =START_BYTE;
				SET_Motion[1] =(byte) 0xAA;
				SET_Motion[2] = 0x04;
				SET_Motion[3] = 0x00;
				SET_Motion[4] = 0x00;
				SET_Motion[5] = 0x00;
				SET_Motion[6] = 0x09;
				DataOutputStream output9 = new DataOutputStream(chosenPort.getOutputStream());
				try {
					output9.write(SET_Motion);
					System.out.println(SET_Motion);
					Thread.sleep(7,500);
					output9.flush();
					}  
				catch (InterruptedException | IOException e1) {
			       	System.out.println("could not write to port");
				e1.printStackTrace();
				}
				break;
				case "10": lbl.setText("You gave Torque Magnitute as 10" );
				SET_Motion[0] =START_BYTE;
				SET_Motion[1] =(byte) 0xAA;
				SET_Motion[2] = 0x04;
				SET_Motion[3] = 0x00;
				SET_Motion[4] = 0x00;
				SET_Motion[5] = 0x00;
				SET_Motion[6] = 0x0A;
				DataOutputStream output10 = new DataOutputStream(chosenPort.getOutputStream());
				try {
					output10.write(SET_Motion);
					System.out.println(SET_Motion);
					Thread.sleep(7,500);
					output10.flush();
					}  
				catch (InterruptedException | IOException e1) {
			       	System.out.println("could not write to port");
				e1.printStackTrace();
				}
				break;
				}
			}
		}});		
	
	BodyWeightSupport.addActionListener(new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent e) {
			if(powerButton.getText().equals("Powered")){
				Thread thread = new Thread(){
				//define run method when you have a thread object
					@Override public void run() {
						// wait after connecting, so the bootloader can finish
						//microcontroller resets as it loads from the USB as well
						try {Thread.sleep(100); } catch(Exception e) {}
						//sending text 
						//give it output stream of our port
						if(BodyWeightSupport.getText().equals("BWS Mode")){
							QuasiStiffness.setText("Quasi-Stiffness Mode");
							Passive.setText("Passive Mode");	
							Manual.setText("Manual Mode");
							BodyWeightSupport.setText("BWS Mode On");
						}
						try {Thread.sleep(100); } catch(Exception e) {}		
						byte[] BWS = new byte[7];							 
						BWS[0] = START_BYTE;
						BWS[1] =(byte) 0x88;
						BWS[2] = 0x04;
						BWS[3] = 0x04;
						BWS[4] = 0x00;
						BWS[5] = 0x00;
						BWS[6] = 0x00;
						
						DataOutputStream outputM = new DataOutputStream(chosenPort.getOutputStream());
						try {
				            outputM.write(BWS);
				           	System.out.println(BWS);
				           	Thread.sleep(7,500);
		                    outputM.flush();
				            }  
							catch (InterruptedException | IOException e) {
						       	System.out.println("could not write to port");
							e.printStackTrace();
							}
				
					}
				};
				thread.start();
			}
	
		}
		
	});
		
	//tell window to show itself
	window.setVisible(true);
 	}			
}

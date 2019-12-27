package org.justte4j.gui;
 

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

 
public class JustTE4JGUI implements Runnable, ActionListener, DropTargetListener  
{
    //Main size X and Y
	int frameWidth=500;
	int frameHeight=600;
	//CurrentDataLIine index
	int currentDataLineIndex=-1;
	//Main component for data lines 
	JList<String> commandsList; 
	//Indicates whether editing is in proress
	 boolean editEnabled=false;  
	//Reference to dataFile
 	File workingFile;
	//UI components
	  private JFrame frame;
	  private JMenuBar menuBar;
	  private JMenu fileMenu;
	  private JMenu helpMenu;
	  private JMenuItem openMenuItem;
	  private JMenuItem createMenuItem;
	  private JMenuItem saveMenuItem;
	  private JMenuItem saveAsMenuItem;
	  private JMenuItem exitMenuItem;
	  private JMenuItem aboutMenuItem;
  
  
	  //
	  JRadioButton tagTarget;
	  JRadioButton classTarget;
	  JRadioButton attrTarget;
	  JRadioButton idTarget;
	  JRadioButton contentCommand;
	  JRadioButton attributeCommand;
	   //
	  JRadioButton replaceSubcommand;
	  JRadioButton insertSubcommand;
	  JRadioButton appendSubcommand;
	  JRadioButton prependSubcommand;
	  JRadioButton nameSubcommand;
	  JRadioButton deleteSubcommand;
	  JRadioButton formerSubcommand;
	  JRadioButton latterSubcommand;
	  //
	  JTextArea paramIField ;
	  JTextArea paramIIField ;
	  JTextArea resultLine ;
	
	  //
	  JButton commNewButton;
	  JButton commEditButton;
	  JButton commSaveButton;
	  JButton commClearButton;
	  JButton commMoveUpButton;
	  JButton commMoveDownButton;
	  //
	  JButton saveEdit;
	  JButton cancelEdit;
	  
	  //CONSTS
	final char TARGET_TYPE_TAG='T';
	final char TARGET_TYPE_CLASS='C';
	final char TARGET_TYPE_ATTRIBUTE='A';
	final char TARGET_TYPE_ID='I';
	//
	final char COMMAND_TYPE_CONTENT='C';
	final char COMMAND_TYPE_ATTRIBUTE='A';
	//
	final char SUBCOMMAND_TYPE_REPLACE='R';
	final char SUBCOMMAND_TYPE_INSERT='I';
	final char SUBCOMMAND_TYPE_APPEND='A';
	final char SUBCOMMAND_TYPE_PREPEND='P';
	final char SUBCOMMAND_TYPE_NAME='N';
	final char SUBCOMMAND_TYPE_DELETE='D';
	final char SUBCOMMAND_TYPE_FORMER='F';
	final char SUBCOMMAND_TYPE_LATTER='L';
	
	//Separates key from value
	 final static String DEFAULT_KV_SEPARATOR = "!=" ;
	 //Denotes end of line
	 final static String DEFAULT_LINE_DELIMITER =  ";\r\n" ;
	 //Indicated whether line is created or edited
	 boolean editMode=false;
	 boolean createMode=false;
	 //Name of file to be saved
	 String saveFileName ;
     //Path of file to be saved
	 String saveFilePath;
	 
	 
  //
  public static void main(String[] args)
  {
    // needed on mac os x
    System.setProperty("apple.laf.useScreenMenuBar", "true");

    // the proper way to show a jframe (invokeLater)
    SwingUtilities.invokeLater(new JustTE4JGUI());
  }
  
  JustTE4JGUI()
  	{
  
		  try {
		      UIManager.setLookAndFeel(
		              "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		  } catch (UnsupportedLookAndFeelException e) {
		      // handle exception
		  } catch (ClassNotFoundException e) {
		      // handle exception
		  } catch (InstantiationException e) {
		      // handle exception
		  } catch (IllegalAccessException e) {
		      // handle exception
		  }
	 
  	}
  
  //Enables working after creatin file or loading file
  void enableGUI()
  {
	//Main list
	   commandsList.setEnabled(true);
	   //
	   commNewButton.setEnabled(true);
	   commEditButton.setEnabled(true);
	   commSaveButton.setEnabled(true);
	   commClearButton.setEnabled(true);
	   commMoveUpButton.setEnabled(true);
	   commMoveDownButton.setEnabled(true);
  }
  
  //Initially disables working woth data
  void disableGUI()
  {
	  //Main list
	   commandsList.setEnabled(false);
	     //
	   commNewButton.setEnabled(false);
	   commEditButton.setEnabled(false);
	   commSaveButton.setEnabled(false);
	   commClearButton.setEnabled(false);
	   commMoveUpButton.setEnabled(false);
	   commMoveDownButton.setEnabled(false);
	   
  }  
	    
  //Enabled editing
  void enableEdit()
  {
	   //Flag
	   editEnabled=true;
	   //Main list
	   commandsList.setEnabled(false);
	    //
	   commNewButton.setEnabled(false);
	   commEditButton.setEnabled(false);
	   commSaveButton.setEnabled(false);
	   commClearButton.setEnabled(false);
	   commMoveUpButton.setEnabled(false);
	   commMoveDownButton.setEnabled(false);
	   //
	   saveEdit.setEnabled(true);
	   cancelEdit.setEnabled(true);
	  
	  //PARAMS
	   paramIField.setEnabled(true);
	   paramIIField.setEnabled(true);
	   resultLine.setEnabled(true);
	    //
	  tagTarget.setEnabled(true);
	  classTarget.setEnabled(true);
	  attrTarget.setEnabled(true);
	  idTarget.setEnabled(true);
	  contentCommand.setEnabled(true);
	  attributeCommand.setEnabled(true);
	  //
	  replaceSubcommand.setEnabled(true);
	  insertSubcommand.setEnabled(true);
	  appendSubcommand.setEnabled(true);
	  prependSubcommand.setEnabled(true);
	  nameSubcommand.setEnabled(true);
	  deleteSubcommand.setEnabled(true);
	  formerSubcommand.setEnabled(true);
	  latterSubcommand.setEnabled(true);
  }
  
  //Diasbles editing
  void disableEdit()
  {
	  //Flag
	  editEnabled=false;
	   //Main list
	   commandsList.setEnabled(true);
	   //
	   commNewButton.setEnabled(true);
	   commEditButton.setEnabled(true);
	   commSaveButton.setEnabled(true);
	   commClearButton.setEnabled(true);
	   commMoveUpButton.setEnabled(true);
	   commMoveDownButton.setEnabled(true);
	    //
	   saveEdit.setEnabled(false);
	   cancelEdit.setEnabled(false);
	   //PARAMS
	   paramIField.setEnabled(false);
	   paramIIField.setEnabled(false);
	   resultLine.setEnabled(false);
	   //
	  tagTarget.setEnabled(false);
	  classTarget.setEnabled(false);
	  attrTarget.setEnabled(false);
	  idTarget.setEnabled(false);
	  contentCommand.setEnabled(false);
	  attributeCommand.setEnabled(false);
	  //
	  replaceSubcommand.setEnabled(false);
	  insertSubcommand.setEnabled(false);
	  appendSubcommand.setEnabled(false);
	  prependSubcommand.setEnabled(false);
	  nameSubcommand.setEnabled(false);
	  deleteSubcommand.setEnabled(false);
	  formerSubcommand.setEnabled(false);
	  latterSubcommand.setEnabled(false);
  }
  
  //Main GUI
 public void run()
  		{
	    //Frame
	    frame = new JFrame("JustTE4J GUI");
	    menuBar = new JMenuBar();
	    
	    // build the File menu
	    fileMenu = new JMenu("File");
	    helpMenu = new JMenu("Help");
	    openMenuItem = new JMenuItem("Open");
	    createMenuItem = new JMenuItem("Create");
	    fileMenu.add(openMenuItem);
	    fileMenu.add(createMenuItem);
	    
	    // build the Edit menuss
	    saveAsMenuItem = new JMenuItem("Save As");
	    saveMenuItem = new JMenuItem("Save");
	    exitMenuItem = new JMenuItem("Exit");
	    //
	    aboutMenuItem= new JMenuItem("About");
	    //
	    fileMenu.add(saveAsMenuItem);
	    fileMenu.add(saveMenuItem);
	    fileMenu.add(exitMenuItem);
        //
	    helpMenu.add(aboutMenuItem);
	    
	    // add menus to menubar
	    menuBar.add(fileMenu);
	    menuBar.add(helpMenu);
	 
	    openMenuItem.addActionListener(this);
	    openMenuItem.setActionCommand("OpenFile");
	    saveAsMenuItem.addActionListener(this);
	    saveAsMenuItem.setActionCommand("SaveAsFile");
	    createMenuItem.addActionListener(this);
	    createMenuItem.setActionCommand("CreateFile");
	    saveMenuItem.addActionListener(this);
	    saveMenuItem.setActionCommand("SaveFile");
	    exitMenuItem.addActionListener(this);
	    exitMenuItem.setActionCommand("ExitApp");
	    aboutMenuItem.addActionListener(this);
	    aboutMenuItem.setActionCommand("AboutApp");
	     // put the menubar on the frame
	    frame.setJMenuBar(menuBar);
	    //
	    tagTarget=new JRadioButton();
	    classTarget=new JRadioButton();
	    attrTarget=new JRadioButton();
	    idTarget=new JRadioButton();
	    contentCommand=new JRadioButton();
	    attributeCommand=new JRadioButton();
	    //TARGET
	    tagTarget.setText("tag");
	    classTarget.setText("class");
	    attrTarget.setText("attribute");
	    idTarget.setText("id");
	    tagTarget.setSelected(true);
	    //
	    ButtonGroup buttonGroup=new ButtonGroup();
	    buttonGroup.add(tagTarget);
	    buttonGroup.add(classTarget);
	    buttonGroup.add(attrTarget);
	    buttonGroup.add(idTarget);
	    //
	    JPanel targetPanel=new JPanel(new GridLayout(1,4,1,2));
	    targetPanel.add(tagTarget);
	    targetPanel.add(classTarget);
	    targetPanel.add(attrTarget);
	    targetPanel.add(idTarget);
	    
	    //COMMAND
	    contentCommand.setText("content");
	    attributeCommand.setText("attribute");
	    contentCommand.setSelected(true);
	    //
	    JPanel commandPanel=new JPanel(new GridLayout(1,2,1,2));
	    commandPanel.add(contentCommand);
	    commandPanel.add(attributeCommand);
	    //
	    ButtonGroup button2Group=new ButtonGroup();
	    button2Group.add(contentCommand);
	    button2Group.add(attributeCommand);
	    
	    //SUBCOMMAND
	    replaceSubcommand=new JRadioButton();
	    replaceSubcommand.setText("replace");
	    insertSubcommand=new JRadioButton();
	    insertSubcommand.setText("insert");
	    appendSubcommand=new JRadioButton();
	    appendSubcommand.setText("append");
	    prependSubcommand=new JRadioButton();
	    prependSubcommand.setText("prepend");
	    nameSubcommand=new JRadioButton();
	    nameSubcommand.setText("name");
	    deleteSubcommand=new JRadioButton();
	    deleteSubcommand.setText("delete");
	    formerSubcommand=new JRadioButton();
	    formerSubcommand.setText("former");
	    latterSubcommand=new JRadioButton();
	    latterSubcommand.setText("latter");
	    //
	    replaceSubcommand.setSelected(true);
	    //
	    JPanel subcommandPanel=new JPanel(new GridLayout(3,2,1,2));
	    subcommandPanel.add(replaceSubcommand);
	    subcommandPanel.add(insertSubcommand);
	    subcommandPanel.add(appendSubcommand);
	    subcommandPanel.add(prependSubcommand);
	    subcommandPanel.add(nameSubcommand);
	    subcommandPanel.add(deleteSubcommand);
	    subcommandPanel.add(formerSubcommand);
	    subcommandPanel.add(latterSubcommand);
	    //
	    ButtonGroup button3Group=new ButtonGroup();
	    button3Group.add(replaceSubcommand);
	    button3Group.add(insertSubcommand);
	    button3Group.add(appendSubcommand);
	    button3Group.add(prependSubcommand);
	    button3Group.add(nameSubcommand);
	    button3Group.add(deleteSubcommand);
	    button3Group.add(formerSubcommand);
	    button3Group.add(latterSubcommand);
	    
	    //PARAMS
	    paramIField=new JTextArea();
	    paramIIField=new JTextArea();
	    
	    //Result
	    resultLine=new JTextArea();
	    
	    /////////////////////////
	  	JPanel p = new JPanel(   );
	  	p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

	    //D&D - drag and drop
	    new DropTarget(frame, this);

		////////////////////LIST//////////
	  	 //
	     commandsList=new JList<String>(new DefaultListModel( ));
	      //Handler for clicking on list
	     MouseListener mouseListener = new MouseAdapter() {
	         public void mouseClicked(MouseEvent mouseEvent) {
	           JList theList = (JList) mouseEvent.getSource();
	           if (mouseEvent.getClickCount() == 1) {
	             int index = theList.locationToIndex(mouseEvent.getPoint());
	             if (index >= 0)
	                {
	            	 //Only if edited disabled
	            	 if(!editEnabled)
	            	   {
	            		 //Get selected object
	                    Object o = theList.getModel().getElementAt(index);
	                    //Current dataLIne
	                    currentDataLineIndex=index;
	                    //The very dataLIne
	                    String dataLine =o.toString();
	                    //Get data from 
	                    inputData(  dataLine   );
	            	   }
	                }
	           }
	         }
	       };
	       
	       //Sets listener
	       commandsList.addMouseListener(mouseListener);
	     
		    //Buttons
		    JPanel listButtonPanel=new JPanel(new GridLayout(3,2,0,0));
		    commNewButton= new JButton("NEW");
		    commEditButton=new JButton("EDIT");
		    commSaveButton=new JButton("SAVE");
		    commClearButton=new JButton("CLEAR");
		    commMoveUpButton=new JButton("UP");
		    commMoveDownButton=new JButton("DOWN");
		    //
		    commNewButton.setActionCommand("NewComm");
		    commEditButton.setActionCommand("EditComm");
		    commSaveButton.setActionCommand("SaveComm");
		    commClearButton.setActionCommand("ClearComm");
		    commMoveUpButton.setActionCommand("MoveUpComm");
		    commMoveDownButton.setActionCommand("MoveDownComm");
		    //
		    listButtonPanel.add(commNewButton);
		    listButtonPanel.add(commEditButton);
		    listButtonPanel.add(commSaveButton);
		    listButtonPanel.add(commClearButton);
		    listButtonPanel.add(commMoveUpButton);
		    listButtonPanel.add(commMoveDownButton);
		     //
		    commNewButton.addActionListener(this);
		    commEditButton.addActionListener(this);
		    commSaveButton.addActionListener(this);
		    commClearButton.addActionListener(this);
		    commMoveUpButton.addActionListener(this);
		    commMoveDownButton.addActionListener(this);
		  
	   
		    JPanel listPanel = new JPanel(new BorderLayout ());
		    //
		    listPanel.add(BorderLayout.CENTER,new JScrollPane(commandsList));
		    listPanel.add(BorderLayout.EAST,listButtonPanel);
		    listPanel.setMinimumSize(new Dimension(frameWidth,100));
		    listPanel.setBorder(BorderFactory.createTitledBorder(""));
		 
		    //
		     p.add(listPanel);
	     
	 
		    ///////////////TARGET//////////////
		    //
		    JPanel tPanel = new JPanel(new BorderLayout ());
		    JLabel tLabel=new JLabel("TARGET:");
		    tLabel.setPreferredSize(new Dimension(100,20));
		    tPanel.add(BorderLayout.WEST,tLabel);
		    tPanel.add(BorderLayout.CENTER,targetPanel);
		    tPanel.setBorder(BorderFactory.createTitledBorder(""));
		 	// 
		    p.add(tPanel);
		   
			//COMMAND
		    JPanel cPanel = new JPanel(new BorderLayout ());
		    JLabel cLabel=new JLabel("COMMAND:");
		    cLabel.setPreferredSize(new Dimension(100,20));
		    cPanel.add(BorderLayout.WEST,cLabel );
		    cPanel.add(BorderLayout.CENTER,commandPanel );
		    cPanel.setBorder(BorderFactory.createTitledBorder(""));
		     // 
		    p.add(cPanel);
		 
	
			//SUBCOMMAND
		    JPanel sPanel = new JPanel(new BorderLayout ());
		    JLabel sLabel=new JLabel("SUBCOMMAND:");
		    sLabel.setPreferredSize(new Dimension(100,20));
		    sPanel.add(BorderLayout.WEST,sLabel );
		    sPanel.add(BorderLayout.CENTER,subcommandPanel );
		    sPanel.setBorder(BorderFactory.createTitledBorder(""));
		    // 
		    p.add(sPanel);
			 
		    //PARAMI
		    JPanel pIPanel = new JPanel(new BorderLayout ());
		    JLabel pILabel=new JLabel("PARAMI:");
		    pILabel.setPreferredSize(new Dimension(100,20));
		    pIPanel.add(BorderLayout.WEST,pILabel );
		    pIPanel.add(BorderLayout.CENTER,paramIField );
		    pIPanel.setBorder(BorderFactory.createTitledBorder(""));
			 // 
		    p.add(pIPanel);
 
		    //PARAMI
		    JPanel pIIPanel = new JPanel(new BorderLayout ());
		    JLabel pIILabel=new JLabel("PARAMII:");
		    pIILabel.setPreferredSize(new Dimension(100,20));
		    pIIPanel.add(BorderLayout.WEST,pIILabel );
		    pIIPanel.add(BorderLayout.CENTER,paramIIField );
		    pIIPanel.setBorder(BorderFactory.createTitledBorder(""));
			 // 
		    p.add(pIIPanel);
	 
		    //RESULT
		    JPanel rPanel = new JPanel(new BorderLayout ());
		    JLabel rLabel=new JLabel("DATA:");
		    rLabel.setPreferredSize(new Dimension(100,20));
		    rPanel.add(BorderLayout.WEST,rLabel );
		    rPanel.add(BorderLayout.CENTER,new JScrollPane(resultLine) ); 
		    rPanel.setBorder(BorderFactory.createTitledBorder(""));
		    rPanel.setPreferredSize(new Dimension(100,150));
			 // 
		    p.add(rPanel);
	      
		    //Buttons
		    saveEdit= new JButton("SAVE");
		    cancelEdit=new JButton("CANCEL");
		    saveEdit.setActionCommand("SaveEdit");
		    cancelEdit.setActionCommand("CancelEdit");
		    //
		    saveEdit.addActionListener(this);
		    cancelEdit.addActionListener(this);
		    
		    JPanel editButtonPanel=new JPanel(new GridLayout(1,1,1,1));
		    editButtonPanel.add(saveEdit);
		    editButtonPanel.add(cancelEdit);
		    editButtonPanel.setBorder(BorderFactory.createTitledBorder(""));
		    //Buttons
		    p.add(editButtonPanel );
       
		     //Initial disable
		     disableEdit();
    
		     //Disables workinig with data
			 disableGUI(); 
	     
		     //Add panel to frame
		    frame.add(p);
	    
		    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    frame.setPreferredSize(new Dimension(frameWidth,frameHeight));
		    frame.pack();
		    frame.setLocationRelativeTo(null);
		    frame.setVisible(true);
     
  		}

  
   //Man action handler
  public void actionPerformed(ActionEvent ev)
  {
   
	   //Initiator - button or menu
	   Object src= ev.getSource();
	   
	   //If button
	   if(src instanceof JButton)
	       {
		      //By action command
			  JButton source=(JButton)src;
			  String text=source.getActionCommand();
			 
				  //
			  if(text.equals("EditComm"))
				    {
				     //Test for loaded file
					  if(workingFile==null)
				        {
					     JOptionPane.showMessageDialog(null, "No file has been loaded.");
					     return  ;
					   } 
					  
					  //Proper mode
				     editMode=true;
					 createMode=false;
					  //Enable UI
				 	 enableEdit();
				    }
			  else if(text.equals("NewComm"))
				    {
					  //Test for loaded file
					  if(workingFile==null)
				        {
					     JOptionPane.showMessageDialog(null, "No file has been loaded.");
					     return  ;
					    } 
					  //Proper mode
					     editMode=false;
						 createMode=true;
					  //Enable UI
				 	    enableEdit();
				    }
				  else if(text.equals("SaveComm"))
				    {
					  //Save current file
				 	   processSaveFile(workingFile);
				    }
				  else if(text.equals("ClearComm"))
				    {
					  //Disable working on that file
					 workingFile=null;
					 //Disable UI components				  
				     disableEdit();
				     //Clear data list
				 	 DefaultListModel<String> model= (DefaultListModel)commandsList.getModel();
				 	 model.clear();
				    }
				  else if(text.equals("MoveUpComm"))
				    {
					//Test for loaded file
					  if(workingFile==null)
					     {
						  JOptionPane.showMessageDialog(null, "No file has been loaded.");
						  return  ;
						 } 
					  
				     //Data list model
					  DefaultListModel<String> model= (DefaultListModel)commandsList.getModel();
					 
					  //If too high
					  if(currentDataLineIndex<=0)
					      {
						   JOptionPane.showMessageDialog(null, "You are at the top.");
						   return  ;
						  }
					  //Disable UI components				  
					     disableEdit();
				   
					     //Switch places
					 	 String aObject = model.getElementAt(currentDataLineIndex);
					 	 String bObject = model.getElementAt(currentDataLineIndex-1);
					 	 model.setElementAt( bObject,currentDataLineIndex );
					 	 model.setElementAt( aObject, currentDataLineIndex-1);
					 	 //New Selection
					 	 commandsList.setSelectedIndex(currentDataLineIndex-1 );
					 	 currentDataLineIndex=currentDataLineIndex-1;
				    }
				  else if(text.equals("MoveDownComm"))
				    {
					//Test for loaded file
					  if(workingFile==null)
				        {
					    JOptionPane.showMessageDialog(null, "No file has been loaded.");
					    return  ;
					    }
					  //Get data model
					  DefaultListModel<String> model= (DefaultListModel)commandsList.getModel();
					  //If too low
					  if(currentDataLineIndex+1>=model.size())
					      {
						   JOptionPane.showMessageDialog(null, "You are at the bottom.");
						   return  ;
						  }
					  //Disable UI components						  
				     disableEdit();
				      //Switch
				 	 String aObject = model.getElementAt(currentDataLineIndex);
				 	 String bObject = model.getElementAt(currentDataLineIndex+1);
				 	 model.setElementAt( bObject,currentDataLineIndex );
				 	 model.setElementAt( aObject, currentDataLineIndex+1);
				 	 //New Selection
				 	 commandsList.setSelectedIndex(currentDataLineIndex+1 );
				 	 currentDataLineIndex=currentDataLineIndex+1;
				     }
			 
				  else if(text.equals("SaveEdit"))
				    {
				 	   //Create data line
					   String newDataLine=createDataLine() ;
					   //If null - error
					   if(newDataLine==null)
						   return;
					    
					   // Disable UI components  
					   disableEdit();
					  //If edited or created
					   if(editMode)
					     {
					      //List model
						  DefaultListModel<String> model= (DefaultListModel)commandsList.getModel();
						  model.setElementAt(newDataLine, currentDataLineIndex);
					     }
					   else if(createMode)
						     {
						      //List model
							  DefaultListModel<String> model= (DefaultListModel)commandsList.getModel();
							  model.add( currentDataLineIndex,newDataLine);
						     } 
					   
						  
				    }
				  else if(text.equals("CancelEdit"))
				    {
					 //Disable editing
				     disableEdit();
				    }
	       }
	   else   if(src instanceof JMenuItem)
	      {
		     JMenuItem source=(JMenuItem)src;
		    String text=source.getActionCommand();
			System.out.println("bvutton="+text);
			  //
			  if(text.equals("OpenFile"))
			    {
				  //Handle openning of data file
				  openFile(  source) ;
				  
			    }
			  else if(text.equals("CreateFile"))
			    {
				  //Point to new empty file
				  File fileToSave= saveFile("");  
				  if(fileToSave!=null)
					  workingFile=fileToSave;
				 
				  //New indexs
				  currentDataLineIndex=0;
					
				  //Enables working with data
				  enableGUI();
			    }
			  else if(text.equals("SaveAsFile"))
			    { 
				  //Save to new file
				  File fileToSave= saveFile("");  
				  if(fileToSave!=null)
				     processSaveFile(fileToSave); 
			    } 
			  else if(text.equals("SaveFile"))
			    {  
				  //Save to current file
				    processSaveFile(workingFile);
			    } 
			  else if(text.equals("ExitApp"))
			    {
				  //Bye
				  System.exit(0);
			    } 
			  else if(text.equals("AboutApp"))
			    {
			 	   AboutDialog dialog = new AboutDialog();
				   dialog.setModal(true); 
				   dialog.setVisible(true);
				 } 
	      }
  } 
  
 //
 void processSaveFile(File fileToSave)
 {
	 if(workingFile==null)
     {
	   JOptionPane.showMessageDialog(null, "No file has been loaded.");
	   return  ;
	   }  
  //File
   try {
		FileOutputStream fos=new FileOutputStream(fileToSave);
		PrintStream dos=new PrintStream(fos);
	   //List model
	   DefaultListModel<String> model= (DefaultListModel)commandsList.getModel();
	   int size=model.size();
	   for(int a=0;a<size;a++)
	     {
		   String dataLine=(String)model.getElementAt(a);
		   dos.print(dataLine );
		   dos.print(DEFAULT_LINE_DELIMITER ); 
		  }
	   
	   dos.flush();
	   dos.close();
	   fos.close();
  
   } 
   catch ( Exception e)
       {
	   //Info
	   JOptionPane.showMessageDialog(null, "Error while saving.");
	   //
	   e.printStackTrace();
	   }  
   
   //Info
   JOptionPane.showMessageDialog(null, "File saved.");
 }
  
//dla zachowania pliku
public File saveFile(String fileName)  
   {
	    JFileChooser chooser = new JFileChooser();
	    chooser.setDialogType(JFileChooser.CUSTOM_DIALOG );
	    chooser.setSelectedFile(new File(fileName));
	    int returnVal = chooser.showSaveDialog(null);
	    if(returnVal == JFileChooser.APPROVE_OPTION)
	       { 
	    	saveFileName=chooser.getSelectedFile().getName();
  	    	//
	    	saveFilePath= chooser.getSelectedFile().toString() ;
  	   
	    	return chooser.getSelectedFile();
	       }
	    
	    return null;
   }
  
//dla zachowania pliku
  public boolean openFile(JMenuItem starter) 
     {
  	    JFileChooser chooser = new JFileChooser();
  	    chooser.setDialogType(JFileChooser.CUSTOM_DIALOG );
  	    FileNameExtensionFilter filter = new FileNameExtensionFilter("ted",   "ted");
  	    chooser.setFileFilter(filter);
  	    int returnVal = chooser.showOpenDialog(starter);
  	    if(returnVal == JFileChooser.APPROVE_OPTION)
  	       { 
  	     	//File data
  	    	File file=chooser.getSelectedFile();
  	        String fileName=chooser.getSelectedFile().getName();
  	        String filePath=chooser.getSelectedFile().toString() ;
  	        String fileDir=chooser.getSelectedFile().getParent();
  	    	Integer fileSize=(int)chooser.getSelectedFile().length();
  	     	 //Process open file
  	    	processOpenFile(  file);
  	    	  
  	    	return true;
  	       } 
  	    
  	    return false;
     }
  
  
  //Create data line form UI imputs
 String createDataLine() 
  {
	  
	 //////////VALIDATION///////////
	 if(paramIField.getText( ).length()==0)
	    {
		 JOptionPane.showMessageDialog(null, "PARAM I cannot be empty.");
	     return null;
	    }
	 
	 if(paramIIField.getText( ).length()==0 && attributeCommand.isSelected())
	    {
		 JOptionPane.showMessageDialog(null, "PARAM II cannot be empty if ATTRIBUTE command is selected.");
	     return null;
	    } 
	 
	 if(resultLine.getText( ).length()==0 && deleteSubcommand.isSelected())
	    {
		 JOptionPane.showMessageDialog(null, "DATA cannot be empty if DELETE cubcommand is selected.");
	     return null;
	    }
	 
	 
	  StringBuffer sb=new StringBuffer();
	  
	  //TARGET
	  if(tagTarget.isSelected())
		  sb.append(TARGET_TYPE_TAG);
	  else  if(classTarget.isSelected())
		  sb.append(TARGET_TYPE_CLASS);
	  else  if(attrTarget.isSelected())
		  sb.append(TARGET_TYPE_ATTRIBUTE);
	   else  if(idTarget.isSelected())
		  sb.append(TARGET_TYPE_ID);
 
	  //
	  sb.append(":");
	  
	  //COMMAND
	  if(contentCommand.isSelected())
		  sb.append(COMMAND_TYPE_CONTENT);
	  else  if(attributeCommand.isSelected())
		  sb.append(COMMAND_TYPE_ATTRIBUTE);
 
	  //
	  sb.append(":");
	   
	  //SUBCOMMAND
	  if(replaceSubcommand.isSelected())
		  sb.append(SUBCOMMAND_TYPE_REPLACE);
	  else  if(insertSubcommand.isSelected())
		  sb.append(SUBCOMMAND_TYPE_INSERT);
	  else  if(appendSubcommand.isSelected())
		  sb.append(SUBCOMMAND_TYPE_APPEND);
	  else  if(prependSubcommand.isSelected())
		  sb.append(SUBCOMMAND_TYPE_PREPEND);
	  else  if(nameSubcommand.isSelected())
		  sb.append(SUBCOMMAND_TYPE_NAME);
	  else  if(deleteSubcommand.isSelected())
		  sb.append(SUBCOMMAND_TYPE_DELETE);
	  else  if(deleteSubcommand.isSelected())
		  sb.append(SUBCOMMAND_TYPE_FORMER);
	  else  if(latterSubcommand.isSelected())
		  sb.append(SUBCOMMAND_TYPE_LATTER);
 
	  //
	  sb.append(":"); 
	  
	  //PARAMI
	  String paramI=paramIField.getText( );
	  if(paramI.length()>0)
		  sb.append(paramI);
	  
	   //
	   sb.append(":"); 
	
	
	   //PARAMII
	   String paramII=paramIIField.getText( );
	   if(paramII.length()>0)
		  sb.append(paramII);
	   
	   //
	   sb.append(DEFAULT_KV_SEPARATOR ); 
	
	   //DATA VALUE
	   String dataValue=resultLine.getText( );
	   if(dataValue.length()>0)
		  sb.append(dataValue);
	 
	   return sb.toString();
  }
  
	//Reads lines from data file and puts it into maps
	public void separateData(File dataFile, JList<String> list )
	{
		
			 //
			 InputStream dataIs=null;
			  try {
				   dataIs = new FileInputStream(dataFile);
			      } 
			  catch (FileNotFoundException e1) {return;}
		     
			 //Define file reader and delimiter
		     Scanner dataTemplate=new Scanner(dataIs);
		     Pattern pattern=Pattern.compile(DEFAULT_LINE_DELIMITER);
		     dataTemplate.useDelimiter(pattern);
		     //List model
		     DefaultListModel<String> model= (DefaultListModel)list.getModel();
		     
	 	     int a=0;
			//Get every line until end 
			while(dataTemplate.hasNext( ))
					{ 
					  //Read from 
					  String dataLine;
					  try {
						   dataLine = dataTemplate.next(  );
					       } 
					  catch (NoSuchElementException e)
					        {
						    break;
					        } 
				 		    
					  a++;
				 	  
					  if(validateDataLine( dataLine ))
					     model.addElement(dataLine);
			 	 } 
	}
	
	//Reads lines from data file and puts it into maps
	public void separateData(ArrayList<String> dataLines, JList<String> list )
       	   {
	 	    //List model
			DefaultListModel<String> model= (DefaultListModel)list.getModel();
	  
	 	     int a=0;
			//Get every line until end 
			for(String dataLine: dataLines)
					{ 
				  	  a++;
     	  
					  if(validateDataLine( dataLine ))
					     model.addElement(dataLine);
				    } 
	}
	
	//Simple data line validation
	boolean validateDataLine(String dataLine )
	  {
		 //Test for empty line
		  if(dataLine.length()==0)
			  return false;
		 
		 	  
		  return true;
	  }
	
//Scans data for datalines	
 ArrayList<String> scanDataFile(File path) throws  Exception
	{
		
			BufferedReader bufferedReader = null;
			 FileInputStream fis = null;
         
            fis = new FileInputStream(path);
            bufferedReader = new BufferedReader(new InputStreamReader(fis));

            String line;
            StringBuilder stringBuilder = new StringBuilder();
            ArrayList<String> list=new ArrayList<String>();
            
            while ((line = bufferedReader.readLine()) != null)
               {
            	//add  \r\n as it is removed by readLine()
            	line=line+"\r\n";
            	
                if (line.contains(DEFAULT_LINE_DELIMITER)  )
                   {   
                    int length = line.indexOf(DEFAULT_LINE_DELIMITER); // get the index when the line contains dot and space in the middle
                    //stringBuilder.append(line.trim().endsWith(".") ? line : line.substring(0, length).replace(". ", "." + System.getProperty("line.separator"))); // when the line contains dot at the end or the line may contain the dot with space
                    int currentLen=stringBuilder.length();
                    stringBuilder.append(line); 
                    list.add(stringBuilder.substring(0, currentLen+length));
                    stringBuilder.delete(0,currentLen+length+DEFAULT_LINE_DELIMITER.length());
                       
                }
                else
                 {
                    stringBuilder.append(line);
                 }
            }
           
           // 
           	return list;
	}
	
 
 //Processes data lines into UI
	public void inputData(String dataLine   )
	      {
         //
          String[] data=dataLine.split(DEFAULT_KV_SEPARATOR); 
		  String key=data[0];
		  String value=data[1];
		  
		  //Separate elements of keys
		  String[] keyEles= key.split(":");
		 	 
		  paramIField.setText(keyEles[3]);
		  if(keyEles.length==4)
			  paramIIField.setText("");
		  else	  
			  paramIIField.setText(keyEles[4]);
	 	   
	      //
		  resultLine.setText(value);
          //Inputs data
	 	  processCommands(  dataLine  );
	   }

 //Inputs data into UI
public void processCommands(String dataLine   )
         {
	
			  //Split by DEFAULT_KV_SEPARATOR 
			  String[] data=dataLine.split(DEFAULT_KV_SEPARATOR); 
			  String key=data[0];
			  String value=data[1];
			  
			  //Separate elements of keys
			  String[] keyEles= key.split(":");
			  
				  
			  //Command - command to process
			  String command =keyEles[1];
			  //Subcommand - subcommand to process
			  String subcommand =keyEles[2];
				   
				
				  
			  //Separate by target and input into map
			  switch(key.charAt(0))
			    {
				 //Comment - skip
				  case '#':
				    	   
				  break;
					 
				  //Tags
				  case TARGET_TYPE_TAG:
					  tagTarget.setSelected(true);
			 	  break;
				   
				  //Classes
				  case TARGET_TYPE_CLASS:
					  classTarget.setSelected(true);
					 
				  break;
				 
				  //Attributes
				  case TARGET_TYPE_ATTRIBUTE:
					  attrTarget.setSelected(true);
					  
				  break;
				  
				  //IDs
			      case TARGET_TYPE_ID:
			    	  idTarget.setSelected(true);
			    	   
			  	  break;
					   
				  }
			 	  
				 switch(command.charAt(0))
				       {
				       //Tag content
					   case COMMAND_TYPE_CONTENT:
							  System.out.println("COMMAND_TYPE_CONTENT="+command );
						   contentCommand.setSelected(true); 
					   break;
					    
				      //Tag attribute
					   case COMMAND_TYPE_ATTRIBUTE:
						   attributeCommand.setSelected(true);
					   break;
					  }
				 
				  //
				   switch(subcommand.charAt(0))
				     {
				      //
				        case SUBCOMMAND_TYPE_REPLACE:
				        	replaceSubcommand.setSelected(true);
				         break;
					    
					 
					   case SUBCOMMAND_TYPE_INSERT:
						   insertSubcommand.setSelected(true);
					   break;
				    
					    
					   case SUBCOMMAND_TYPE_APPEND:
						   appendSubcommand.setSelected(true);
					   break;
				    
				 
					   case SUBCOMMAND_TYPE_PREPEND:
						   prependSubcommand.setSelected(true);
					    break;
				    
						
					   case SUBCOMMAND_TYPE_NAME:
						   nameSubcommand.setSelected(true);
						break;
				    
				    	
					   case SUBCOMMAND_TYPE_DELETE:
						   deleteSubcommand.setSelected(true);
						break;
				    
				 	
					   case SUBCOMMAND_TYPE_FORMER:
						   formerSubcommand.setSelected(true);
					     break;
				    
				 	
					   case SUBCOMMAND_TYPE_LATTER:
						   latterSubcommand.setSelected(true);
					    break;
				    
				}

	      }
 
//Info aboud dialog
  private class AboutDialog extends JDialog implements ActionListener
  {
    private JButton okButton = new JButton("OK");
 
    public AboutDialog( ) {
        super(frame, "About", true);

        Box b = Box.createVerticalBox();
        b.add(Box.createGlue());
        b.add(new JLabel("GUI for JustTE4J Java Web Template Engine"));
        b.add(new JLabel("http://justte4j.sourceforge.net"));
        b.add(new JLabel("https://code.google.com/p/justa-te-4j-gui-manager/"));
        b.add(Box.createGlue());
        getContentPane().add(b, "Center");

        JPanel p2 = new JPanel();
        JButton ok = new JButton("Ok");
        ok.addActionListener(this);
        p2.add(ok);
        getContentPane().add(p2, "South");

        ok.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            setVisible(false);
          }
        });

        setSize(350, 150);
        setLocationRelativeTo(frame);
      }

    
    

    public void actionPerformed(ActionEvent ev)
    {
      setVisible(false);
    }
  }

@Override
public void dragEnter(DropTargetDragEvent dtde) {
	// TODO Auto-generated method stub
	
}

@Override
public void dragOver(DropTargetDragEvent dtde) {
	// TODO Auto-generated method stub
	
}

@Override
public void dropActionChanged(DropTargetDragEvent dtde) {
	// TODO Auto-generated method stub
	
}

@Override
public void dragExit(DropTargetEvent dte) {
	// TODO Auto-generated method stub
	
}

@Override
public void drop(DropTargetDropEvent event) {
	 
	 // Accept copy drops
    event.acceptDrop(DnDConstants.ACTION_COPY);

    // Get the transfer which can provide the dropped item data
    Transferable transferable = event.getTransferable();

    // Get the data formats of the dropped item
    DataFlavor[] flavors = transferable.getTransferDataFlavors();

    // Loop through the flavors
    for (DataFlavor flavor : flavors) {

        try {

            // If the drop items are files
            if (flavor.isFlavorJavaFileListType()) {

                // Get all of the dropped files
                List<File> files = ( List<File>) transferable.getTransferData(flavor);

                // Loop them through
                for (File file : files)
                    {
                	 //
                	 processOpenFile(file);
                    
                    return;
                    }

            }

        } catch (Exception e) {

            // Print out the error stack
            e.printStackTrace();

        }
    }

    // Inform that the drop is complete
    event.dropComplete(true);

	
}

 //
void processOpenFile(File file)
{
	try {
		    
		    //Clean entries
		    DefaultListModel<String> model= (DefaultListModel)commandsList.getModel();
			model.clear();
		    //
			ArrayList<String> dataLines = scanDataFile(file);
		     //
		    separateData(  dataLines,   commandsList );
		    //Reference to dataFile
		  	workingFile=file;
		    //Enables workinig with data
			 enableGUI();
		}
	catch (Exception e)
		{
		e.printStackTrace();
		}
  	
}
 
 
}
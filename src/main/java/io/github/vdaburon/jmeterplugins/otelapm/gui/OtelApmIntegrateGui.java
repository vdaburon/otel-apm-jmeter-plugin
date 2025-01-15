/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.github.vdaburon.jmeterplugins.otelapm.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.MenuElement;

import org.apache.jmeter.exceptions.IllegalUserActionException;
import org.apache.jmeter.gui.GuiPackage;
import org.apache.jmeter.gui.JMeterFileFilter;
import org.apache.jmeter.gui.UnsharedComponent;
import org.apache.jmeter.gui.action.AbstractAction;
import org.apache.jmeter.gui.plugin.MenuCreator;
import org.apache.jmeter.gui.util.EscapeDialog;
import org.apache.jmeter.gui.util.HorizontalPanel;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.save.SaveService;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.gui.ComponentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.vdaburon.jmeter.otelxml.OtelJMeterManager;

public class OtelApmIntegrateGui extends AbstractAction implements
        ActionListener, UnsharedComponent, MenuCreator {
	private static Set<String> commands = new HashSet<>();

    @SuppressWarnings("unused")
	private static final long serialVersionUID = 2438L;

    private static final Logger log = LoggerFactory.getLogger(OtelApmIntegrateGui.class);
    private static final String BROWSE_FILE_IN = "BROWSE_FILE_IN";
    private static final String BROWSE_FILE_OUT = "BROWSE_FILE_OUT";
    private static final String ACTION_CHOICE = "ACTION_CHOICE";
    private static final String ACTION_MODIFY = "ACTION_MODIFY";
    private static final String ACTION_MODIFY_AND_LOAD = "ACTION_MODIFY_AND_LOAD";
    private static final String ACTION_MENU_TOOL = "ACTION_MENU_TOOL"; 

    private EscapeDialog messageDialog;
    
    private JTextField fileInTextField;
    private JTextField fileOutTextField;
    private JButton fileInFileButton;
    @SuppressWarnings("rawtypes")
	private JComboBox actionChoice;
    private JTextField regexTextField;
    private JButton fileOutFileButton;
    private JButton btModify;
    private JButton btModifyAndLoadNewScript;
    private String lastJFCDirectory;
    private JTextField labelStatus;

    static {
        commands.add(ACTION_MENU_TOOL);
    }
    public OtelApmIntegrateGui() {
        super();
        log.debug("Creating OtelApmIntegrateGui");
    }

 
    public String getLabelResource() {
        return this.getClass().getSimpleName();
    }

    public void doAction(ActionEvent e) throws IllegalUserActionException {
    	OtelApmIntegrateGui otelApmIntegrateGui = new OtelApmIntegrateGui();
        JFrame jfMainFrame = GuiPackage.getInstance().getMainFrame();
        otelApmIntegrateGui.showInputDialog(jfMainFrame);
    }



    public void showInputDialog(JFrame parent) {
        setupInputDialog(parent);
        launchInputDialog();
    }

    private void launchInputDialog() {
        messageDialog.pack();
        ComponentUtil.centerComponentInWindow(messageDialog);
        messageDialog.setVisible(true);
    }

    public void setupInputDialog(JFrame parent) {
        messageDialog = new EscapeDialog(parent, "vdn@github - OTEL ELASTIC APM INTEGRATION TOOL", false);
        setupContentPane();
    }

    private void setupContentPane() {
        Container contentPane = messageDialog.getContentPane();
        contentPane.setLayout(new BorderLayout(0,5));

        JPanel mainPanel = new JPanel(new BorderLayout());
        VerticalPanel vertPanel = new VerticalPanel();
        vertPanel.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createEtchedBorder(), "OTEL ELASTIC APM Configuration"));

        vertPanel.add(setupFileChooserPanel());
        vertPanel.add(createInfoPanel());
        vertPanel.add(createControls());
        
        labelStatus = new JTextField("Waiting configuration ... ");
        labelStatus.setEditable(false);
        vertPanel.add(labelStatus);

        mainPanel.add(vertPanel);
        contentPane.add(mainPanel);

    }


	@Override
    public void actionPerformed(ActionEvent action) {
        String command = action.getActionCommand();
        Exception except = null;
 
//        log.info("command=" + command);
        if (command.equals(ACTION_MENU_TOOL)) {
        	try {
				doAction(action);
			} catch (IllegalUserActionException e) {
				e.printStackTrace();
			}
        }
        
        if (command.equals(ACTION_MODIFY) || command.equals(ACTION_MODIFY_AND_LOAD)) {
          	log.info("ACTION_MODIFY");
            String fileIn= fileInTextField.getText();
            
            File fFileIn = new File(fileIn);
            if (!fFileIn.canRead()) {
            	labelStatus.setText("Tool OTEL ELASTIC APM Integration Finished KO, CAN'T READ jmx fileIn = " + fileIn);
            	labelStatus.setForeground(java.awt.Color.RED);
            	return;
            }
            String fileOut=fileOutTextField.getText();
            String sAction=actionChoice.getSelectedItem().toString();
            String regexTc=regexTextField.getText();
            
            if (fileOut.trim().isEmpty()) {
            	fileOut = fileIn.substring(0,fileIn.lastIndexOf(".")) + "_modif.jmx";
            }
            if (regexTc.trim().isEmpty()) {
            	regexTc = ".*";
            }
            
            if (sAction.equalsIgnoreCase(OtelJMeterManager.ACTION_ADD)) {
            	sAction = OtelJMeterManager.ACTION_ADD;
            }
            
            if (sAction.equalsIgnoreCase(OtelJMeterManager.ACTION_REMOVE)) {
            	sAction = OtelJMeterManager.ACTION_REMOVE;
            }
            
            try {
            	btModify.setEnabled(false);
                btModifyAndLoadNewScript.setEnabled(false);
            	labelStatus.setText("Tool OTEL ELASTIC APM Integration Running");
            	log.info("Before OtelJMeterManager.modifyAddSamplerForOtelApm");
            	log.info("fileIn=<"+ fileIn + ">, fileOut=<" + fileOut + ">, ACTION=" + sAction);
     	 		OtelJMeterManager.modifyAddSamplerForOtelApm(fileIn, fileOut, sAction, regexTc, OtelJMeterManager.EXTRACT_START_JSR223, OtelJMeterManager.EXTRACT_END_JSR223, OtelJMeterManager.EXTRACT_UDV_ELASTIC);
                log.info("After OtelJMeterManager.modifyAddSamplerForOtelApm");
                btModify.setEnabled(true);
                btModifyAndLoadNewScript.setEnabled(true);
     	 		File fFileOut = new File(fileOut);
                if (!fFileOut.canRead()) {
                	labelStatus.setText("Tool OTEL ELASTIC APM Integration Finished KO, CAN'T CREATE or jmx file doesn't exist (look in the log file), fileOut  = " + fileOut);
                	labelStatus.setForeground(java.awt.Color.RED);
                	return;
                }
     	 		labelStatus.setText("Tool OTEL ELASTIC APM Integration Finished OK, ACTION = " + sAction + ", fileOut=" + fileOut);
     	 		labelStatus.setForeground(java.awt.Color.BLACK);
             } catch (Exception e) {
                e.printStackTrace();
                except = e;
                btModify.setEnabled(true);
                btModifyAndLoadNewScript.setEnabled(true);
                labelStatus.setText("Tool OTEL ELASTIC APM Integration Finished KO, exception = " + e);
                labelStatus.setForeground(java.awt.Color.RED);
            }
            
            if (null == except) {
                // new script jmx file OK
                btModify.setEnabled(true);
                btModifyAndLoadNewScript.setEnabled(true);

                if (command.equals(ACTION_MODIFY_AND_LOAD)) {
                    // open the script generated in current JMeter
                    final HashTree tree;
                    try {
                        tree = SaveService.loadTree(new File(fileOut));
                        org.apache.jmeter.gui.action.Load.insertLoadedTree(1,tree);
                    } catch (IOException | IllegalUserActionException e) {
                        labelStatus.setText("Tool OTEL ELASTIC APM Integration Finished KO (load new script), exception = " + e);
                        labelStatus.setForeground(java.awt.Color.RED);
                    }
                }
            }
        }

        if (command.equals(BROWSE_FILE_IN)) {
        	fileInTextField.setText(showFileChooser(fileInTextField.getParent(),
        			fileInTextField, false, new String[] { ".jmx" }));
        	labelStatus.setText("Waiting configuration ... ");
        	labelStatus.setForeground(java.awt.Color.BLACK);
        }
        if (command.equals(BROWSE_FILE_OUT)) {
        	fileOutTextField.setText(showFileChooser(fileOutTextField.getParent(),
        			fileOutTextField, false, new String[] { ".jmx" }));
        	labelStatus.setText("Waiting configuration ... ");
        	labelStatus.setForeground(java.awt.Color.BLACK);
        }
    }

    private JPanel createControls() {
    	btModify = new JButton("MODIFY SCRIPT"); 
    	btModify.addActionListener(this);
    	btModify.setActionCommand(ACTION_MODIFY);
    	btModify.setEnabled(true);

        btModifyAndLoadNewScript = new JButton("MODIFY SCRIPT AND LOAD NEW SCRIPT");
        btModifyAndLoadNewScript.addActionListener(this);
        btModifyAndLoadNewScript.setActionCommand(ACTION_MODIFY_AND_LOAD);
        btModifyAndLoadNewScript.setEnabled(true);

        JPanel panel = new JPanel();
        panel.add(btModify);
        panel.add(btModifyAndLoadNewScript);
        return panel;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	private JPanel createInfoPanel() {
        JLabel actionLabel = new JLabel("ACTION : "); 
        // ACTION choice ADD or REMOVE
        String[] arrActions = { "ADD", "REMOVE"};
  
        
        // list choice
        actionChoice = new JComboBox(arrActions);
        actionChoice.setActionCommand(ACTION_CHOICE);
        actionChoice.addActionListener(this); 
    
        JLabel regexTcLabel = new JLabel("Regular expression matches Transaction Controller Label (default all = .*) : ");
        regexTextField = new JTextField(".*", 80);
 
        HorizontalPanel panel = new HorizontalPanel();
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "ACTION and REGEX for Transaction Controller Label")); 

        panel.add(actionLabel);
        panel.add(actionChoice);

        panel.add(regexTcLabel);
        panel.add(regexTextField);

        panel.add(Box.createHorizontalStrut(10));

        return panel;
    }

    private JPanel setupFileChooserPanel() {
        JPanel fileChooserPanel = new JPanel(new GridLayout(0, 3));
        fileChooserPanel.add(new JLabel("JMeter script in (to read) : "));

        fileInTextField = new JTextField();
        fileChooserPanel.add(fileInTextField);

        this.fileInFileButton = new JButton("Browse ...");
        fileInFileButton.setActionCommand(BROWSE_FILE_IN);
        fileInFileButton.addActionListener(this);
        fileChooserPanel.add(fileInFileButton);

        fileChooserPanel.add(new JLabel("JMeter script out (to write) (if empty default <file_in_no_extension>_modif.jmx) : "));

        fileOutTextField = new JTextField();
        fileChooserPanel.add(fileOutTextField);

        this.fileOutFileButton = new JButton("Browse ...");
        fileOutFileButton.setActionCommand(BROWSE_FILE_OUT);
        fileOutFileButton.addActionListener(this);
        fileChooserPanel.add(fileOutFileButton);

        return fileChooserPanel;
    }
    
    /**
     * Show a file chooser to the user
     *
     * @param locationTextField
     *            the textField that will receive the path
     * @param onlyDirectory
     *            whether or not the file chooser will only display directories
     * @param extensions File extensions to filter
     * @return the path the user selected or, if the user cancelled the file
     *         chooser, the previous path
     */
    private String showFileChooser(Component component, JTextField locationTextField, boolean onlyDirectory, String[] extensions) {
        JFileChooser jfc = new JFileChooser();
        if (onlyDirectory) {
            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        } else {
            jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        }
        if(extensions != null && extensions.length > 0) {
            JMeterFileFilter currentFilter = new JMeterFileFilter(extensions);
            jfc.addChoosableFileFilter(currentFilter);
            jfc.setAcceptAllFileFilterUsed(true);
            jfc.setFileFilter(currentFilter);
        }
        if (lastJFCDirectory != null) {
            jfc.setCurrentDirectory(new File(lastJFCDirectory));
        } else {
            String start = System.getProperty("user.dir", ""); //$NON-NLS-1$//$NON-NLS-2$
            if (!start.isEmpty()) {
                jfc.setCurrentDirectory(new File(start));
            }
        }
        int retVal = jfc.showOpenDialog(component);
        if (retVal == JFileChooser.APPROVE_OPTION) {
            lastJFCDirectory = jfc.getCurrentDirectory().getAbsolutePath();
            return jfc.getSelectedFile().getPath();
        } else {
            return locationTextField.getText();
        }
    }

	@Override
	public JMenuItem[] getMenuItemsAtLocation(MENU_LOCATION location) {
	      if (location != MENU_LOCATION.TOOLS) {
	            return new JMenuItem[0];
	        }

	        JMenuItem menuItem = new JMenuItem("vdn@github - OTEL ELASTIC APM Integration Tool", null);
	        menuItem.setName("OTEL ELASTIC APM Integration Tool");
	        menuItem.setActionCommand(ACTION_MENU_TOOL);
	        menuItem.setAccelerator(null);
	        menuItem.addActionListener(this);
	        return new JMenuItem[] { menuItem };
	}

	@Override
	public JMenu[] getTopLevelMenus() {
		return new JMenu[0];
	}

	@Override
	public void localeChanged() {
		// NOOP
		
	}

	@Override
	public boolean localeChanged(MenuElement arg0) {
		return false;
	}


	@Override
	public Set<String> getActionNames() {
		return commands;
	}
}
/* Copyright 2007 by Mordechai (Moti) Ben-Ari. See copyright.txt. */
/*
 *  Dialog for setting SpinSpider options 
 */

package jspin;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;

class SpiderOptions extends JDialog implements ActionListener { 
    private JCheckBox    debugBox;
    private JRadioButton noTrail, emphTrail, onlyTrail, small, large, bold, color, automata;
    private ButtonGroup  trailGroup, dotGroup, styleGroup;
    private JButton      runButton, cancelButton;
    private JLabel       processLabel, variablesLabel, dotLabel, formatLabel, styleLabel;
    private JComboBox    formatBox, processBox;
    private JTextArea    variablesText;
    private JPanel       OKPanel, formatPanel, leftPanel, neverPanel, processPanel, 
    				     variablesPanel, trailPanel, sizePanel, boxPanel, stylePanel;
    private Border       border;

    private boolean      ok;
    private SpiderFile   sp;
    
	SpiderOptions(JFrame parent, SpiderFile sp, java.awt.Font font) {
		super(parent, true);
		this.sp = sp;

	    border = BorderFactory.createEtchedBorder();

        formatLabel  = new JLabel(Config.Format, JLabel.CENTER);
		formatBox = new JComboBox();
        formatBox.setEditable(false);
        formatBox.addItem(Config.DOT);
        formatBox.addItem(Config.PNG);
        formatBox.addItem(Config.FSM);
        formatBox.addItem(Config.PS);
        formatBox.setSelectedItem(sp.getStringProperty("FORMAT"));

        int trailCode = sp.getIntProperty("TRAIL_CODE");
		noTrail = new JRadioButton(Config.NoTrail, trailCode == 0);
        noTrail.setMnemonic(Config.NoTrailMN);
		emphTrail = new JRadioButton(Config.EmphTrail, trailCode == 1);
        emphTrail.setMnemonic(Config.EmphTrailMN);
		onlyTrail = new JRadioButton(Config.OnlyTrail, trailCode == 2);
        onlyTrail.setMnemonic(Config.OnlyTrailMN);
		automata = new JRadioButton(Config.Automata, trailCode == 3);
        automata.setMnemonic(Config.AutomataMN);
        trailGroup = new ButtonGroup();
        trailGroup.add(noTrail);
        trailGroup.add(emphTrail);
        trailGroup.add(onlyTrail);
        trailGroup.add(automata);

        dotLabel  = new JLabel(Config.DotSize, JLabel.CENTER);
        int dotSize = sp.getIntProperty("DOT_SIZE");
		small = new JRadioButton(Config.DotSmall, dotSize == 0);
        small.setMnemonic(Config.DotSmallMN);
		large = new JRadioButton(Config.DotLarge, dotSize == 1);
        large.setMnemonic(Config.DotLargeMN);
        dotGroup = new ButtonGroup();
        dotGroup.add(small);
        dotGroup.add(large);

        styleLabel  = new JLabel(Config.TrailStyle, JLabel.CENTER);
        int trailStyle = sp.getIntProperty("TRAIL_STYLE");
		color = new JRadioButton(Config.TrailColor, trailStyle == 0);
        color.setMnemonic(Config.TrailColorMN);
		bold = new JRadioButton(Config.TrailBold, trailStyle == 1);
        bold.setMnemonic(Config.TrailBoldMN);
        styleGroup = new ButtonGroup();
        styleGroup.add(color);
        styleGroup.add(bold);

        debugBox = new JCheckBox(Config.Debug, 
				sp.getBooleanProperty("SPIDER_DEBUG"));
        debugBox.setMnemonic(Config.DebugMN);
		
		processLabel = new JLabel(Config.Processes, JLabel.CENTER);
		processBox = new JComboBox();
        processBox.setEditable(false);
        for (int i = 0; i < Config.MAX_PROCESS; i++)
        	processBox.addItem("        " + (i+1));
        processBox.setSelectedIndex(sp.getIntProperty("PROCESSES")-1);

		variablesLabel = new JLabel(Config.Variables, JLabel.CENTER);
		variablesText = new JTextArea(sp.getStringProperty("VARIABLES"));
        variablesText.setFont(font);
        
		runButton = new JButton(Config.Run);
		cancelButton = new JButton(Config.Cancel);
        runButton.setMnemonic(Config.RunMN);
		runButton.addActionListener(this);
		cancelButton.addActionListener(this);

		trailPanel = new JPanel();
        trailPanel.setLayout(new java.awt.GridLayout(4,1));
        trailPanel.setBorder(border);
        trailPanel.add(noTrail);
        trailPanel.add(emphTrail);
        trailPanel.add(onlyTrail);
        trailPanel.add(automata);

		sizePanel = new JPanel();
        sizePanel.setLayout(new java.awt.GridLayout(3,1));
        sizePanel.setBorder(border);
        sizePanel.add(dotLabel);
        sizePanel.add(small);
        sizePanel.add(large);

		stylePanel = new JPanel();
        stylePanel.setLayout(new java.awt.GridLayout(3,1));
        stylePanel.setBorder(border);
        stylePanel.add(styleLabel);
        stylePanel.add(color);
        stylePanel.add(bold);

        formatPanel = new JPanel();
        formatPanel.setLayout(new java.awt.GridLayout(2,1));
        formatPanel.setBorder(border);
        formatPanel.add(formatLabel);
        formatPanel.add(formatBox);

        leftPanel = new JPanel();
        leftPanel.setLayout(new java.awt.BorderLayout());
        leftPanel.setBorder(border);
        leftPanel.add(formatPanel, java.awt.BorderLayout.NORTH);
        leftPanel.add(sizePanel, java.awt.BorderLayout.CENTER);
        leftPanel.add(stylePanel, java.awt.BorderLayout.SOUTH);
        leftPanel.setPreferredSize(new java.awt.Dimension(100,0));

        processPanel = new JPanel();
        processPanel.setLayout(new java.awt.GridLayout(2,1));
        processPanel.setBorder(border);
        processPanel.add(processLabel);
        processPanel.add(processBox);

        boxPanel = new JPanel();
        boxPanel.setLayout(new java.awt.BorderLayout());
        boxPanel.setBorder(border);
        boxPanel.add(debugBox, java.awt.BorderLayout.CENTER);
        
        neverPanel = new JPanel();
        neverPanel.setLayout(new java.awt.BorderLayout());
        neverPanel.setBorder(border);
        neverPanel.add(trailPanel, java.awt.BorderLayout.CENTER);
        neverPanel.add(processPanel, java.awt.BorderLayout.NORTH);
        neverPanel.add(boxPanel, java.awt.BorderLayout.SOUTH);

        variablesPanel = new JPanel();
        variablesPanel.setLayout(new java.awt.BorderLayout());
        variablesPanel.setBorder(border);
		variablesPanel.add(variablesLabel, java.awt.BorderLayout.NORTH);
        variablesPanel.add(variablesText, java.awt.BorderLayout.CENTER);

        OKPanel = new JPanel();
        OKPanel.setLayout(new java.awt.GridLayout(1,5));
        OKPanel.setBorder(border);
        OKPanel.add(new JLabel(" "));
        OKPanel.add(runButton);
        OKPanel.add(new JLabel(" "));
        OKPanel.add(cancelButton);
        OKPanel.add(new JLabel(" "));
	
		getRootPane().setDefaultButton(runButton);
        getRootPane().registerKeyboardAction(this,
                    KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                    JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        getContentPane().setLayout(new java.awt.BorderLayout());
        getContentPane().add(neverPanel, java.awt.BorderLayout.EAST);
        getContentPane().add(variablesPanel, java.awt.BorderLayout.CENTER);
        getContentPane().add(leftPanel, java.awt.BorderLayout.WEST);
        getContentPane().add(OKPanel, java.awt.BorderLayout.SOUTH);

        setTitle(Config.SPIDER_TITLE);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setSize(400, 300); 
        setLocationRelativeTo(null);
		validate();
    }

	boolean showDialog() {
		ok = false;
        setVisible(true);
		return ok;
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == runButton) {
			sp.setIntProperty("TRAIL_CODE",
				noTrail.isSelected()   ? 0 : 
	           (emphTrail.isSelected() ? 1 : 
	           (onlyTrail.isSelected() ? 2 : 
	           (automata.isSelected()  ? 3 : 0))));
			sp.setIntProperty("DOT_SIZE",
					small.isSelected() ? 0 : 
		           (large.isSelected() ? 1 : 0)); 
			sp.setIntProperty("TRAIL_STYLE",
					color.isSelected() ? 0 : 
		           (bold.isSelected() ? 1 : 0)); 
			sp.setBooleanProperty("SPIDER_DEBUG", debugBox.isSelected());
			sp.setStringProperty("FORMAT", (String) formatBox.getSelectedItem());
			sp.setIntProperty("PROCESSES", Integer.parseInt(
            		((String) processBox.getSelectedItem()).trim()));
			sp.setStringProperty("VARIABLES", variablesText.getText());
			ok = true;
		}
        dispose();
	}
}

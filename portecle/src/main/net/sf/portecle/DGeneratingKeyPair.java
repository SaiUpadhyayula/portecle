/*
 * DGeneratingKeyPair.java
 *
 * Copyright (C) 2004 Wayne Grant
 * waynedgrant@hotmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * (This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package net.sf.portecle;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.security.*;

import net.sf.portecle.gui.error.DThrowable;
import net.sf.portecle.crypto.*;

/**
 * Generates a key pair which the user may cancel at any time by pressing the
 * cancel button.
 */
class DGeneratingKeyPair extends JDialog
{
    /** Resource bundle */
    private static ResourceBundle m_res = ResourceBundle.getBundle("net/sf/portecle/resources");

    /** Panel to hold generating key pair label */
    private JPanel m_jpGenKeyPair;

    /** Generating key pair label */
    private JLabel m_jlGenKeyPair;

    /** Panel to hold cancel button */
    private JPanel m_jpCancel;

    /** Cancel button to cancel dialog */
    private JButton m_jbCancel;

    /** Key from input map to action map for the cancel button */
    private static final String CANCEL_KEY = "CANCEL_KEY";

    /** Stores the key pair generation type */
    private KeyPairType m_keyPairType;

    /** Stores the key pair size to generate */
    private int m_iKeySize;

    /** Generated Key Pair */
    private KeyPair m_keyPair;

    /** Reference to the dialog for the GenerateKeyPair inner class to reference */
    private JDialog dialog = this;

    /** The thread that actually does the key pair generation */
    private Thread m_generator;

    /**
     * Creates new DGeneratingKeyPair dialog where the parent is a frame.
     *
     * @param parent The parent frame
     * @param bModal Is dialog modal?
     * @param keyPairType The key pair generation type
     * @param iKeySize The key size to generate
     */
    public DGeneratingKeyPair(JFrame parent, boolean bModal, KeyPairType keyPairType, int iKeySize)
    {
        super(parent, bModal);
        m_keyPairType = keyPairType;
        m_iKeySize = iKeySize;
        initComponents();
    }

    /**
     * Creates new DGeneratingKeyPair dialog where the parent is a dialog.
     *
     * @param parent The parent dialog
     * @param bModal Is dialog modal?
     * @param keyPairType The key pair generation type
     * @param iKeySize The key size to generate
     */
    public DGeneratingKeyPair(JDialog parent, boolean bModal, KeyPairType keyPairType, int iKeySize)
    {
        super(parent, bModal);
        m_keyPairType = keyPairType;
        m_iKeySize = iKeySize;
        initComponents();
    }

    /**
     * Initialise the dialog's GUI components.
     */
    private void initComponents()
    {
        // Generate key Pair label
        m_jlGenKeyPair = new JLabel(m_res.getString("DGeneratingKeypair.m_jlGenKeyPair.text"));
        ImageIcon icon = new ImageIcon(getClass().getResource(m_res.getString("DGeneratingKeypair.Generating.image")));
        m_jlGenKeyPair.setIcon(icon);
        m_jpGenKeyPair = new JPanel(new FlowLayout(FlowLayout.CENTER));
        m_jpGenKeyPair.add(m_jlGenKeyPair);
        m_jpGenKeyPair.setBorder(new EmptyBorder(5, 5, 5, 5));

        // Cancel button
        m_jbCancel = new JButton(m_res.getString("DGeneratingKeyPair.m_jbCancel.text"));
        m_jbCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                cancelPressed();
            }
        });
        m_jbCancel.getInputMap(m_jbCancel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), CANCEL_KEY);
        m_jbCancel.getActionMap().put(CANCEL_KEY, new AbstractAction () {
                                          public void actionPerformed(ActionEvent evt) {
                                              cancelPressed();
                                      }});
        m_jpCancel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        m_jpCancel.add(m_jbCancel);

        getContentPane().add(m_jpGenKeyPair, BorderLayout.NORTH);
        getContentPane().add(m_jpCancel, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                if ((m_generator != null) && (m_generator.isAlive()))
                {
                    m_generator.interrupt();
                }
                closeDialog();
            }
        });

        setTitle(m_res.getString("DGeneratingKeyPair.Title"));
        setResizable(false);

        pack();
    }

    /**
     * Start key pair generation in a separate thread.
     */
    public void startKeyPairGeneration()
    {
        m_generator = new Thread(new GenerateKeyPair());
        m_generator.setPriority(Thread.MIN_PRIORITY);
        m_generator.start();
    }

    /**
     * Cancel button pressed or otherwise activated.
     */
    private void cancelPressed()
    {
        if ((m_generator != null) && (m_generator.isAlive()))
        {
            m_generator.interrupt();
        }
        closeDialog();
    }

    /** Closes the dialog */
    private void closeDialog()
    {
        setVisible(false);
        dispose();
    }

    /**
     * Get the generated key pair.
     *
     * @return The generated key pair or null if the user cancelled the dialog
     */
    public KeyPair getKeyPair()
    {
        return m_keyPair;
    }

    /**
     * Generates a key pair.  Is Runnable so can be run in a seperate thread.
     */
    private class GenerateKeyPair implements Runnable
    {
        /** Store any crypto exception that occurs */
        CryptoException m_ex;

        /**
         * Generate a key pair.
         */
        public void run()
        {
            // Generate key pair
            KeyPair keyPair;
            try
            {
                if (m_keyPairType == KeyPairType.DSA)
                {
                    keyPair = KeyPairUtil.generateKeyPair(KeyPairType.DSA, m_iKeySize);
                }
                else
                {
                    keyPair = KeyPairUtil.generateKeyPair(KeyPairType.RSA, m_iKeySize);
                }

                if (true)

                m_keyPair = keyPair;

                // Manipulate GUI in event handler thread
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if (dialog.isShowing()) { closeDialog(); }
                    }
                });
            }
            catch (CryptoException ex)
            {
                // Store excpetion in member so it can be accessed from inner class
                m_ex = ex;

                // Manipulate GUI in event handler thread
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if (dialog.isShowing())
                        {
                            DThrowable dThrowable = new DThrowable(dialog, true, m_ex);
							dThrowable.setLocationRelativeTo(DGeneratingKeyPair.this);
                            dThrowable.setVisible(true);
                            closeDialog();
                        }
                    }
                });
            }
        }
    }
}
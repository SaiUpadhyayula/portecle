/*
 * DChangePassword.java
 * This file is part of Portecle, a multipurpose keystore and certificate tool.
 *
 * Copyright © 2004 Wayne Grant, waynedgrant@hotmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package net.sf.portecle.gui.password;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

/**
 * Dialog used for entering and confirming a password and checking it against
 * an old password which may or may not have been supplied to the dialog.
 */
public class DChangePassword
    extends JDialog
{
    /** Key from input map to action map for the cancel button */
    private static final String CANCEL_KEY = "CANCEL_KEY";

    /** Resource bundle */
    private static ResourceBundle m_res = ResourceBundle.getBundle("net/sf/portecle/gui/password/resources");

    /** Old password entry password field */
    private JPasswordField m_jpfOld;

    /** First password entry password field */
    private JPasswordField m_jpfFirst;

    /** Password confirmation entry password field */
    private JPasswordField m_jpfConfirm;

    /** Stores new password entered */
    private char[] m_cNewPassword;

    /** Stores old password entered/supplied */
    private char[] m_cOldPassword;

    /**
     * Creates new DChangePassword dialog where the parent is a frame.
     *
     * @param parent Parent frame
     * @param bModal The dialog's title
     * @param cOldPassword The password to be changed
     */
    public DChangePassword(JFrame parent, boolean bModal, char[] cOldPassword)
    {
        this(parent, bModal, m_res.getString("DChangePassword.Title"),
            cOldPassword);
    }

    /**
     * Creates new DChangePassword dialog where the parent is a frame.
     *
     * @param parent Parent frame
     * @param bModal The dialog's title
     * @param sTitle Is dialog modal?
     * @param cOldPassword The password to be changed
     */
    public DChangePassword(JFrame parent, boolean bModal, String sTitle,
        char[] cOldPassword)
    {
        super(parent, sTitle, bModal);
        m_cOldPassword = cOldPassword;
        initComponents();
    }

    /**
     * Creates new DChangePassword dialog where the parent is a dialog.
     *
     * @param parent Parent frame
     * @param bModal Is dialog modal?
     * @param cOldPassword The password to be changed
     */
    public DChangePassword(JDialog parent, boolean bModal, char[] cOldPassword)
    {
        this(parent, m_res.getString("DChangePassword.Title"), bModal,
            cOldPassword);
    }

    /**
     * Creates new DChangePassword dialog where the parent is a dialog.
     *
     * @param parent Parent frame
     * @param sTitle The dialog's title
     * @param bModal Is dialog modal?
     * @param cOldPassword The password to be changed
     */
    public DChangePassword(JDialog parent, String sTitle, boolean bModal,
        char[] cOldPassword)
    {
        super(parent, sTitle, bModal);
        m_cOldPassword = cOldPassword;
        initComponents();
    }

    /**
     * Get the new password set in the dialog.
     *
     * @return The new password or null if none was set
     */
    public char[] getNewPassword()
    {
        return m_cNewPassword;
    }

    /**
     * Get the old password set in the dialog.
     *
     * @return The old password or null if none was set/supplied
     */
    public char[] getOldPassword()
    {
        return m_cOldPassword;
    }

    /**
     * Initialise the dialog's GUI components.
     */
    private void initComponents()
    {
        getContentPane().setLayout(new BorderLayout());

        JLabel jlFirst = new JLabel(
            m_res.getString("DChangePassword.jlFirst.text"));
        m_jpfFirst = new JPasswordField(15);

        JLabel jlConfirm = new JLabel(
            m_res.getString("DChangePassword.jlConfirm.text"));
        m_jpfConfirm = new JPasswordField(15);

        JLabel jlOld = new JLabel(m_res.getString("DChangePassword.jlOld.text"));

        // Old password was supplied - just disable the old password
        // field after filling it with junk
        if (m_cOldPassword != null) {
            m_jpfOld = new JPasswordField("1234567890", 15);
            m_jpfOld.setEnabled(false);
        }
        else {
            m_jpfOld = new JPasswordField(10);
        }

        JButton jbOK = new JButton(m_res.getString("DChangePassword.jbOK.text"));
        jbOK.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                okPressed();
            }
        });

        JButton jbCancel = new JButton(
            m_res.getString("DChangePassword.jbCancel.text"));
        jbCancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                cancelPressed();
            }
        });
        jbCancel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), CANCEL_KEY);
        jbCancel.getActionMap().put(CANCEL_KEY, new AbstractAction()
        {
            public void actionPerformed(ActionEvent evt)
            {
                cancelPressed();
            }
        });

        JPanel jpPassword = new JPanel(new GridLayout(3, 2, 5, 5));
        jpPassword.add(jlOld);
        jpPassword.add(m_jpfOld);
        jpPassword.add(jlFirst);
        jpPassword.add(m_jpfFirst);
        jpPassword.add(jlConfirm);
        jpPassword.add(m_jpfConfirm);
        jpPassword.setBorder(new EmptyBorder(5, 5, 5, 5));

        JPanel jpButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        jpButtons.add(jbOK);
        jpButtons.add(jbCancel);

        getContentPane().add(jpPassword, BorderLayout.CENTER);
        getContentPane().add(jpButtons, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent evt)
            {
                closeDialog();
            }
        });

        setResizable(false);

        getRootPane().setDefaultButton(jbOK);

        pack();
    }

    /**
     * Check for the following:
     * <ul>
     *     <li>That the user has supplied and confirmed a password.
     *     <li>That the password's match.
     *     <li>That they have a length greater than a perscribed minimum.
     *     <li>That the old password was supplied or set by the user.
     * </ul>
     * Store the old and changed password in this object.
     *
     * @return True, if the user's dialog entry matches the above criteria,
     *         false otherwise
     */
    private boolean checkPassword()
    {
        String sOldPassword = new String(m_jpfOld.getPassword());
        String sFirstPassword = new String(m_jpfFirst.getPassword());
        String sConfirmPassword = new String(m_jpfConfirm.getPassword());

        if (sFirstPassword.equals(sConfirmPassword)) {
            if (m_cOldPassword == null) {
                m_cOldPassword = sOldPassword.toCharArray();
            }
            m_cNewPassword = sFirstPassword.toCharArray();
            return true;
        }

        JOptionPane.showMessageDialog(this,
            m_res.getString("PasswordsNoMatch.message"), getTitle(),
            JOptionPane.WARNING_MESSAGE);

        return false;
    }

    /**
     * OK button pressed or otherwise activated.
     */
    private void okPressed()
    {
        if (checkPassword()) {
            closeDialog();
        }
    }

    /**
     * Cancel button pressed or otherwise activated.
     */
    private void cancelPressed()
    {
        closeDialog();
    }

    /**
     * Close the dialog.
     */
    private void closeDialog()
    {
        setVisible(false);
        dispose();
    }
}
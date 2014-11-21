package com.shaneisrael.st.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;

public class RegisterKeysetUI extends JFrame
{

    private JPanel contentPane;
    private JTextField textField;
    private JTextField textField_1;

    /**
     * Create the frame.
     */
    public RegisterKeysetUI()
    {
        setTitle("Register New Keyset");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 370, 179);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        
        JPanel panel = new JPanel();
        contentPane.add(panel, BorderLayout.CENTER);
        panel.setLayout(new MigLayout("", "[71.00,grow][79.00,grow]", "[36.00][28.00][]"));
        
        JLabel lblKey = new JLabel("Key 1");
        lblKey.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel.add(lblKey, "cell 0 0,alignx center");
        
        JLabel lblKey_1 = new JLabel("Key 2");
        lblKey_1.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel.add(lblKey_1, "cell 1 0,alignx center");
        
        textField = new JTextField();
        textField.setFont(new Font("Tahoma", Font.BOLD, 13));
        textField.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(textField, "cell 0 1,growx");
        textField.setColumns(10);
        
        textField_1 = new JTextField();
        textField_1.setFont(new Font("Tahoma", Font.BOLD, 13));
        textField_1.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(textField_1, "cell 1 1,growx");
        textField_1.setColumns(10);
        
        final JButton btnRegister = new JButton("Register");
        btnRegister.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent arg0) {
                
                if(btnRegister.getText().equals("Register"))
                    btnRegister.setText("Registering...");
                else
                    btnRegister.setText("Register");
            }
        });
        panel.add(btnRegister, "cell 1 2,alignx center");
        
        this.setVisible(true);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

}

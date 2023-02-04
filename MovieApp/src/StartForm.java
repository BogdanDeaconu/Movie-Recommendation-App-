import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartForm extends JDialog {
    private JButton btnSignUp;
    private JButton btnLogIn;
    private JPanel StartPanel;

    public StartForm(JFrame parent){
        super(parent);
        setTitle(" Movie");
        setContentPane(StartPanel);
        setMinimumSize(new Dimension(450,474));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);


        btnLogIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {login();}
        });
        btnSignUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {signup();}
        });
        setVisible(true);
    }

    private void login() {
        LogInForm logInForm = new LogInForm(null);
        logInForm.setVisible(true);
        dispose();
    }
    private void signup(){
            RegistrationForm registrationForm = new RegistrationForm(null);
            registrationForm.setVisible(true);
            dispose();
    }


    public static void main(String[] args){
        StartForm startForm = new StartForm(null);
    }

}

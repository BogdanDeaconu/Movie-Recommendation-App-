import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;


public class RegistrationForm extends JDialog{
    private JTextField pfUsername;
    private JButton btnRegister;
    private JButton btnCancel;
    private JPanel RegisterPanel;
    private JPasswordField pfPassword;
    private JComboBox cbRoles;

    public RegistrationForm(JFrame parent) {
        super(parent);
        setTitle("Create Account");
        setContentPane(RegisterPanel);
        setMinimumSize(new Dimension(450, 474));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        cbRoles.addItem("Admin");
        cbRoles.addItem("User");

        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                  registeruser();
            }
        });
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        setVisible(true);
    }

    private void registeruser() {
        String username = pfUsername.getText();
        String password = String.valueOf(pfPassword.getPassword());
        String role = String.valueOf(cbRoles.getSelectedItem());

        if (username.isEmpty() || password.isEmpty() || role.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter all fields",
                    "Try again",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        user = addUserToDatabase( username, password,role);
        if (user != null) {
            LogInForm logInForm = new LogInForm(null);
            logInForm.setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to register new user",
                    "Try again",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

        User user;
        private User addUserToDatabase( String username, String password,String role) {
            User user = null;
            final String DB_URL = "jdbc:mysql://localhost/movieapp?serverTimezone=UTC";
            final String USERNAME = "root";
            final String PASSWORD = "";

            try{
                Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);


                Statement stmt = conn.createStatement();
                String sql = "INSERT INTO users (username, password,role) " +
                        "VALUES (?,?,?)";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                preparedStatement.setString(3,role);


                int addedRows = preparedStatement.executeUpdate();
                if (addedRows > 0) {
                    user = new User();
                    user.username = username;
                    user.password = password;
                    user.role = role;
                }

                stmt.close();
                conn.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            return user;
    }

}

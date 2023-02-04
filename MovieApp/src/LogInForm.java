import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LogInForm extends JDialog {
    private JButton btnCancel;
    private JPanel LogInPanel;
    private JTextField pfusername;
    private JButton btnAdmin;
    private JPasswordField pfpassword;

    public LogInForm(JFrame parent){
        super(parent);
        setTitle("Login");
        setContentPane(LogInPanel);
        setMinimumSize(new Dimension(450, 474));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);



        btnAdmin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = pfusername.getText();
                String password = String.valueOf(pfpassword.getPassword());

                user = getAuthenticatedUser(username, password);

                if (user != null) {
                    if(user.role.equals("Admin")) {
                         AdminForm adminForm=new AdminForm(null);
                         adminForm.setVisible(true);
                         dispose();
                    } else if (user.role.equals("User")) {
                        UserForm userForm=new UserForm(null,user.uid,user.username);
                        userForm.setVisible(true);
                        dispose();
                    }
                }
                else {
                    JOptionPane.showMessageDialog(LogInForm.this,
                            "Username or Password Invalid",
                            "Try again",
                            JOptionPane.ERROR_MESSAGE);
                }
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

    public User user;
    private User getAuthenticatedUser(String username, String password) {
        User user = null;

        final String DB_URL = "jdbc:mysql://localhost/movieapp?serverTimezone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try{
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM users WHERE username=? AND password=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                user = new User();
                user.uid=resultSet.getString("id");
                user.username = resultSet.getString("username");
                user.password = resultSet.getString("password");
                user.role = resultSet.getString("role");
            }

            stmt.close();
            conn.close();

        }catch(Exception e){
            e.printStackTrace();
        }


        return user;
    }

}


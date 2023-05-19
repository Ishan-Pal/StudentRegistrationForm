import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StudentRegistrationForm extends JFrame {
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JRadioButton maleRadioButton;
    private JRadioButton femaleRadioButton;
    private JCheckBox mathCheckBox;
    private JCheckBox physicsCheckBox;
    private JList<String> studentList;
    private JButton saveButton;
    private JButton retrieveButton;
    private JTree studentTree;

    private Connection connection;
    private Statement statement;

    public StudentRegistrationForm() {
        // Set up the JFrame
        setTitle("Student Registration Form");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        // Create the controls
        JLabel firstNameLabel = new JLabel("First Name:");
        JLabel lastNameLabel = new JLabel("Last Name:");
        firstNameField = new JTextField(15);
        lastNameField = new JTextField(15);

        JLabel genderLabel = new JLabel("Gender:");
        maleRadioButton = new JRadioButton("Male");
        femaleRadioButton = new JRadioButton("Female");
        ButtonGroup genderButtonGroup = new ButtonGroup();
        genderButtonGroup.add(maleRadioButton);
        genderButtonGroup.add(femaleRadioButton);

        JLabel subjectsLabel = new JLabel("Subjects:");
        mathCheckBox = new JCheckBox("Math");
        physicsCheckBox = new JCheckBox("Physics");

        JLabel studentListLabel = new JLabel("Students:");
        studentList = new JList<>();

        saveButton = new JButton("Save");
        retrieveButton = new JButton("Retrieve");
        studentTree = new JTree();

        // Add the controls to the JFrame
        add(firstNameLabel);
        add(firstNameField);
        add(lastNameLabel);
        add(lastNameField);
        add(genderLabel);
        add(maleRadioButton);
        add(femaleRadioButton);
        add(subjectsLabel);
        add(mathCheckBox);
        add(physicsCheckBox);
        add(studentListLabel);
        add(new JScrollPane(studentList));
        add(saveButton);
        add(retrieveButton);
        add(new JScrollPane(studentTree));

        // Set action listeners for the buttons
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveStudent();
            }
        });

        retrieveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                retrieveStudents();
            }
        });

        // Connect to the database
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/your_database_name", "username", "password");
            statement = connection.createStatement();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        pack();
        setVisible(true);
    }

    private void saveStudent() {
        try {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String gender = maleRadioButton.isSelected() ? "Male" : "Female";
            String subjects = "";
            if (mathCheckBox.isSelected()) {
                subjects += "Math ";
            }
            if (physicsCheckBox.isSelected()) {
                subjects += "Physics ";
            }

            String sql = "INSERT INTO students (first_name, last_name, gender, subjects) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, gender);
            preparedStatement.setString(4, subjects);
            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Student information saved successfully.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving student information.");
        }
    }

    private void retrieveStudents() {
        try {
            String sql = "SELECT * FROM students";
            ResultSet resultSet = statement.executeQuery(sql);

            DefaultMutableTreeNode root = new DefaultMutableTreeNode("Students");

            DefaultListModel<String> studentListModel = new DefaultListModel<>();
            while (resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String gender = resultSet.getString("gender");
                String subjects = resultSet.getString("subjects");

                // Add the student to the JTree
                DefaultMutableTreeNode studentNode = new DefaultMutableTreeNode(firstName + " " + lastName + " (" + gender + ")");
                DefaultMutableTreeNode subjectsNode = new DefaultMutableTreeNode("Subjects: " + subjects);
                studentNode.add(subjectsNode);
                root.add(studentNode);

                // Add the student to the JList
                studentListModel.addElement(firstName + " " + lastName);
            }

            studentTree.setModel(new javax.swing.tree.DefaultTreeModel(root));
            studentList.setModel(studentListModel);

            resultSet.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving student information.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new StudentRegistrationForm();
            }
        });
    }
}

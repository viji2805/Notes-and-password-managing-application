import javax.swing.*;
import java.awt.GridLayout;      // only what we need from AWT
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class NotesPasswordManager {

    /* ---------- Simple Encrypt / Decrypt ---------- */
    public static String encrypt(String text) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) sb.append((char) (c + 3));
        return sb.toString();
    }

    public static String decrypt(String text) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) sb.append((char) (c - 3));
        return sb.toString();
    }

    /* ---------- Notes ---------- */
    public static void saveNote(String note) {
        try (FileWriter fw = new FileWriter("notes.txt", true)) {
            fw.write(note + "\n");
        } catch (IOException e) { showError("Error saving note", e); }
    }

    public static List<String> getAllNotes() {
        List<String> notes = new ArrayList<>();
        File file = new File("notes.txt");
        if (!file.exists()) return notes;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) notes.add(line);
        } catch (IOException e) { showError("Error reading notes", e); }
        return notes;
    }

    /* ---------- Passwords ---------- */
    public static void savePassword(String site, String user, String pass) {
        try (FileWriter fw = new FileWriter("passwords.txt", true)) {
            fw.write(site + "," + user + "," + encrypt(pass) + "\n");
        } catch (IOException e) { showError("Error saving password", e); }
    }

    public static List<String> getAllPasswords() {
        List<String> list = new ArrayList<>();
        File file = new File("passwords.txt");
        if (!file.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 3);
                if (parts.length == 3) {
                    list.add("Website: " + parts[0]
                           + ", Username: " + parts[1]
                           + ", Password: " + decrypt(parts[2]));
                }
            }
        } catch (IOException e) { showError("Error reading passwords", e); }
        return list;
    }

    private static void showError(String msg, Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, msg + "\n" + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    /* ---------- GUI ---------- */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Notes & Password Manager");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(2, 2, 10, 10));

        JButton addNoteBtn   = new JButton("Add Note");
        JButton viewNotesBtn = new JButton("View Notes");
        JButton savePwdBtn   = new JButton("Save Password");
        JButton viewPwdBtn   = new JButton("View Passwords");

        frame.add(addNoteBtn);
        frame.add(viewNotesBtn);
        frame.add(savePwdBtn);
        frame.add(viewPwdBtn);

        addNoteBtn.addActionListener(e -> {
            String note = JOptionPane.showInputDialog(frame, "Enter Note:");
            if (note != null && !note.trim().isEmpty()) {
                saveNote(note.trim());
                JOptionPane.showMessageDialog(frame, "Note saved!");
            }
        });

        viewNotesBtn.addActionListener(e -> {
            List<String> notes = getAllNotes();
            JOptionPane.showMessageDialog(frame,
                    notes.isEmpty() ? "No notes found." : String.join("\n", notes));
        });

        savePwdBtn.addActionListener(e -> {
            JTextField site = new JTextField();
            JTextField user = new JTextField();
            JPasswordField pass = new JPasswordField();
            Object[] fields = { "Website:", site, "Username:", user, "Password:", pass };

            int option = JOptionPane.showConfirmDialog(frame, fields,
                    "Save Password", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                savePassword(site.getText().trim(),
                             user.getText().trim(),
                             new String(pass.getPassword()));
                JOptionPane.showMessageDialog(frame, "Password saved!");
            }
        });

        viewPwdBtn.addActionListener(e -> {
            List<String> passwords = getAllPasswords();
            JOptionPane.showMessageDialog(frame,
                    passwords.isEmpty() ? "No passwords found."
                                        : String.join("\n", passwords));
        });

        frame.setVisible(true);
    }
}
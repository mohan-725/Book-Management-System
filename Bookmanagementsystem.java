import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.ArrayList;

public class BookManagementSystemApp extends JFrame {
    private DefaultTableModel tableModel;
    private JTable bookTable;
    private JTextField searchField;

    public BookManagementSystemApp() {
        setTitle("Book Management System");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tableModel = new DefaultTableModel();
        tableModel.addColumn("Title");
        tableModel.addColumn("Author");
        tableModel.addColumn("ISBN");

        bookTable = new JTable(tableModel);
        bookTable.setAutoCreateRowSorter(true);
        bookTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editBook();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(bookTable);

        JButton addButton = new JButton("Add Book");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBook();
            }
        });

        JButton removeButton = new JButton("Remove Book");
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeBook();
            }
        });

        JButton saveButton = new JButton("Save Books");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveBooks();
            }
        });

        JButton loadButton = new JButton("Load Books");
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadBooks();
            }
        });

        searchField = new JTextField(20);
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                searchBooks();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                searchBooks();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                searchBooks();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout());
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        add(searchPanel, BorderLayout.NORTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addBook() {
        String title = JOptionPane.showInputDialog("Enter the book title:");
        String author = JOptionPane.showInputDialog("Enter the author:");
        String isbn = JOptionPane.showInputDialog("Enter the ISBN:");

        if (title != null && author != null && isbn != null) {
            tableModel.addRow(new Object[]{title, author, isbn});
        }
    }

    private void editBook() {
        int selectedRow = bookTable.getSelectedRow();
        
         if (selectedRow != -1) {
             String title = (String) JOptionPane.showInputDialog(null, "Edit the book title:", "Edit Book", JOptionPane.QUESTION_MESSAGE, null, null, tableModel.getValueAt(selectedRow, 0));
             String author = (String) JOptionPane.showInputDialog(null, "Edit the author:", "Edit Book", JOptionPane.QUESTION_MESSAGE, null, null, tableModel.getValueAt(selectedRow, 1));
             String isbn = (String) JOptionPane.showInputDialog(null, "Edit the ISBN:", "Edit Book", JOptionPane.QUESTION_MESSAGE, null, null, tableModel.getValueAt(selectedRow, 2));

             if (title != null && author != null && isbn != null) {
                 tableModel.setValueAt(title, selectedRow, 0);
                 tableModel.setValueAt(author, selectedRow, 1);
                 tableModel.setValueAt(isbn, selectedRow, 2);
             }
         }
    }

    private void removeBook() {
         int selectedRow = bookTable.getSelectedRow();
         if (selectedRow != -1) {
             tableModel.removeRow(selectedRow);
         }
     }

     private void saveBooks() {
         JFileChooser fileChooser = new JFileChooser();
         int result = fileChooser.showSaveDialog(this);

         if (result == JFileChooser.APPROVE_OPTION) {
             File file = fileChooser.getSelectedFile();

             try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file))) {
                 ArrayList<Book> books = new ArrayList<>();

                 for (int i = 0; i < tableModel.getRowCount(); i++) {
                     String title = (String) tableModel.getValueAt(i, 0);
                     String author = (String) tableModel.getValueAt(i, 1);
                     String isbn = (String) tableModel.getValueAt(i, 2);

                     books.add(new Book(title, author, isbn));
                 }

                 outputStream.writeObject(books);
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }
     }

     private void loadBooks() {
         JFileChooser fileChooser = new JFileChooser();
         int result = fileChooser.showOpenDialog(this);

         if (result == JFileChooser.APPROVE_OPTION) {
             File file = fileChooser.getSelectedFile();

             try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
                 ArrayList<Book> books = (ArrayList<Book>) inputStream.readObject();

                 tableModel.setRowCount(0);

                 for (Book book : books) {
                     tableModel.addRow(new Object[]{book.getTitle(), book.getAuthor(), book.getIsbn()});
                 }
             } catch (IOException | ClassNotFoundException e) {
                 e.printStackTrace();
             }
         }
     }

     private void searchBooks() {
         String searchText = searchField.getText().toLowerCase();

         for (int i = 0; i < tableModel.getRowCount(); i++) {
             String title = ((String) tableModel.getValueAt(i, 0)).toLowerCase();
             String author = ((String) tableModel.getValueAt(i, 1)).toLowerCase();
             String isbn = ((String) tableModel.getValueAt(i, 2)).toLowerCase();

             if (title.contains(searchText) || author.contains(searchText) || isbn.contains(searchText)) {
                 bookTable.setRowSelectionInterval(i, i);
                 break;
             }
         }
     }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new BookManagementSystemApp();
            }
        });
    }

    private static class Book implements Serializable {
        private String title;
        private String author;
        private String isbn;

        public Book(String title, String author, String isbn) {
            this.title = title;
            this.author = author;
            this.isbn = isbn;
        }

        public String getTitle() {
            return title;
        }

        public String getAuthor() {
            return author;
        }

        public String getIsbn() {
            return isbn;
        }
    }
}

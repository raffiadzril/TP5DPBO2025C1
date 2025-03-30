import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class Menu extends JFrame{
    public static void main(String[] args) {
        // buat object window
        Menu window = new Menu();

        // atur ukuran window
        window.setSize(400, 560);
        // letakkan window di tengah layar
        window.setLocationRelativeTo(null);
        // isi window
        window.setContentPane(window.mainPanel);
        // ubah warna background
        window.getContentPane().setBackground(Color.WHITE);
        // tampilkan window
        window.setVisible(true);
        // agar program ikut berhenti saat window diclose
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // index baris yang diklik
    private int selectedIndex = -1;
    // list untuk menampung semua mahasiswa
    private ArrayList<Mahasiswa> listMahasiswa;
    private Database database;

    private JPanel mainPanel;
    private JTextField nimField;
    private JTextField namaField;
    private JTable mahasiswaTable;
    private JButton addUpdateButton;
    private JButton cancelButton;
    private JComboBox jenisKelaminComboBox;
    private JButton deleteButton;
    private JLabel titleLabel;
    private JLabel nimLabel;
    private JLabel namaLabel;
    private JLabel jenisKelaminLabel;
    private JRadioButton c1RadioButton;
    private JRadioButton c2RadioButton;
    private JRadioButton aRadioButton;
    private JRadioButton bRadioButton;

    JRadioButton[] kelasRadioButton = {
            c1RadioButton,
            c2RadioButton,
            aRadioButton,
            bRadioButton
    };
    // constructor
    public Menu() {
        // inisialisasi listMahasiswa
        listMahasiswa = new ArrayList<>();

        database = new Database();


        // isi tabel mahasiswa
        mahasiswaTable.setModel(setTable());

        // ubah styling title
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 24));


        // atur isi combo box
        String[] jenisKelamin = {"Laki-laki", "Perempuan"};
        jenisKelaminComboBox.setModel(new DefaultComboBoxModel(jenisKelamin));

        // atur radio button kelas
        String[] kelas = {"C1", "C2", "A", "B"};
        ButtonGroup groupKelas = new ButtonGroup();
        groupKelas.add(c1RadioButton);
        groupKelas.add(c2RadioButton);
        groupKelas.add(aRadioButton);
        groupKelas.add(bRadioButton);

        // sembunyikan button delete
        deleteButton.setVisible(false);

        // saat tombol add/update ditekan
        addUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(selectedIndex == -1) {
                    insertData();
                } else {
                    updateData();
                }
            }
        });
        // saat tombol delete ditekan
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedIndex >= 0) {
                    int confirm = JOptionPane.showConfirmDialog(
                            null,
                            "Hapus data?",
                            "Konfirmasi",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (confirm == JOptionPane.YES_OPTION) {
                        deleteData();
                    }
                }
            }
        });

        // saat tombol cancel ditekan
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
        // saat salah satu baris tabel ditekan
        mahasiswaTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // ubah selectedIndex menjadi baris tabel yang diklik
                selectedIndex = mahasiswaTable.getSelectedRow();

                // simpan value textfield dan combo box
                String selectedNim = mahasiswaTable.getModel().getValueAt(selectedIndex, 1).toString();
                String selectedNama = mahasiswaTable.getModel().getValueAt(selectedIndex, 2).toString();
                String selectedJenisKelamin = mahasiswaTable.getModel().getValueAt(selectedIndex, 3).toString();
                String selectedKelas = mahasiswaTable.getModel().getValueAt(selectedIndex, 4).toString();
                // ubah isi textfield dan combo box
                nimField.setText(selectedNim);
                namaField.setText(selectedNama);
                jenisKelaminComboBox.setSelectedItem(selectedJenisKelamin);
                // pilih radio button yang sesuai dengan selectedKelas
                int i = 0;
                boolean found = false;
                while (i < kelasRadioButton.length && !found) {
                    if (kelasRadioButton[i].getText().equals(selectedKelas)) {
                        kelasRadioButton[i].setSelected(true);
                        found = true;
                    }
                    i++;
                }



                // ubah button "Add" menjadi "Update"
                addUpdateButton.setText("Update");

                // tampilkan button delete
                deleteButton.setVisible(true);
            }
        });
    }

    public final DefaultTableModel setTable() {
        // tentukan kolom tabel
        Object[] columns = {"No", "NIM", "Nama", "Jenis Kelamin", "Kelas"};

        // buat objek tabel dengan kolom yang sudah dibuat
        DefaultTableModel temp = new DefaultTableModel(null, columns);

        try{
            // ambil data dari database
            ResultSet resultSet = database.selectQuery("SELECT * FROM mahasiswa");
            int idx = 0;
            while(resultSet.next()) {
                Object[] row = new Object[5];

                row[0] = idx + 1;
                row[1] = resultSet.getString("nim");
                row[2] = resultSet.getString("nama");
                row[3] = resultSet.getString("jenis_kelamin");
                row[4] = resultSet.getString("kelas");

                temp.addRow(row);
                idx++;
            }
        } catch (SQLException e) {
            // menangani kesalahan SQL dengan melempar runtime exception
            throw new RuntimeException(e);
        }
        return temp;
    }
    // Tambahkan metode ini di dalam kelas Menu
    private boolean isFormValid() {
        List<String> kosong = new ArrayList<>();
        if (nimField.getText().isEmpty()){
            kosong.add("NIM");
        }
        if(namaField.getText().isEmpty()){
            kosong.add("Nama");
        }
        if(jenisKelaminComboBox.getSelectedIndex() == -1){
            kosong.add("Jenis Kelamin");
        }

        boolean kelasSelected = false;
        for (JRadioButton rb : kelasRadioButton) {
            if (rb.isSelected()) {
                kelasSelected = true;
            }
        }
        if (!kelasSelected) {
            kosong.add("Kelas");
        }
        if (!kosong.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(null, "Input " + String.join(", ", kosong) + " tidak boleh kosong", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private boolean isNimExists(String nim) {
        try {
            ResultSet resultSet = database.selectQuery("SELECT nim FROM mahasiswa WHERE nim = '" + nim + "'");
            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Modifikasi metode insertData
    public void insertData() {
        if (!isFormValid()) {
            return;
        }

        String nim = nimField.getText();
        if (isNimExists(nim)) {
            JOptionPane.showMessageDialog(null, "NIM sudah ada", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nama = namaField.getText();
        String jenisKelamin = jenisKelaminComboBox.getSelectedItem().toString();
        String kelas = "";
        boolean found = false;
        int i = 0;
        while (i < kelasRadioButton.length && !kelasRadioButton[i].isSelected()) {
            i++;
        }

        if (i < kelasRadioButton.length) {
            kelas = kelasRadioButton[i].getText();
        }
        String query = "INSERT INTO mahasiswa (nim, nama, jenis_kelamin, kelas) VALUES ('" + nim + "', '" + nama + "', '" + jenisKelamin + "', '" + kelas + "')";
        database.insertUpdateDeleteQuery(query);

        mahasiswaTable.setModel(setTable());
        clearForm();

        System.out.println("Insert data berhasil");
        JOptionPane.showMessageDialog(null, "Insert data berhasil");
    }

    // Modifikasi metode updateData
    public void updateData() {
        if (!isFormValid()) {
            return;
        }

        String nim = nimField.getText();
        String nama = namaField.getText();
        String jenisKelamin = jenisKelaminComboBox.getSelectedItem().toString();
        String kelas = "";
        int i = 0;
        while (i < kelasRadioButton.length && !kelasRadioButton[i].isSelected()) {
            i++;
        }

        if (i < kelasRadioButton.length) {
            kelas = kelasRadioButton[i].getText();
        }


        String query = "UPDATE mahasiswa SET nama = '" + nama + "', jenis_kelamin = '" + jenisKelamin + "', kelas = '" + kelas + "' WHERE nim = '" + nim + "'";
        database.insertUpdateDeleteQuery(query);

        mahasiswaTable.setModel(setTable());
        clearForm();

        System.out.println("Update data berhasil");
        JOptionPane.showMessageDialog(null, "Update data berhasil");
    }


    public void deleteData() {
        // delete data dari database
        String nim = nimField.getText();
        String query = "DELETE FROM mahasiswa WHERE nim = '" + nim + "'";
        database.insertUpdateDeleteQuery(query);

        // update tabel
        mahasiswaTable.setModel(setTable());

        // bersihkan form
        clearForm();

        // feedback
        System.out.println("Delete data berhasil");
        JOptionPane.showMessageDialog(null, "Delete data berhasil");
    }

    public void clearForm() {
        // kosongkan semua texfield dan combo box
        nimField.setText("");
        namaField.setText("");
        jenisKelaminComboBox.setSelectedIndex(0);
        for (JRadioButton rb : kelasRadioButton) {
            rb.setSelected(false);
        }

        // ubah button "Update" menjadi "Add"
        addUpdateButton.setText("Add");

        // sembunyikan button delete
        deleteButton.setVisible(false);

        // ubah selectedIndex menjadi -1 (tidak ada baris yang dipilih)
        selectedIndex = -1;
    }

}

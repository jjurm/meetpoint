package com.jjurm.projects.mpp.system;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.jjurm.projects.mpp.algorithm.Algorithm;
import com.jjurm.projects.mpp.algorithm.Algorithm.Result;
import com.jjurm.projects.mpp.algorithm.DiscreteAlgorithm;
import com.jjurm.projects.mpp.db.DatabaseManager;
import com.jjurm.projects.mpp.db.PlaceFinder;
import com.jjurm.projects.mpp.db.PlaceFinder.NotFoundException;
import com.jjurm.projects.mpp.map.ProductivityMap;
import com.jjurm.projects.mpp.map.ProductivityMapsFactory;
import com.jjurm.projects.mpp.model.Attendant;
import com.jjurm.projects.mpp.model.Parameters;
import com.jjurm.projects.mpp.model.Parameters.ParametersList;
import com.jjurm.projects.mpp.model.Place;
import com.jjurm.projects.mpp.util.Holder;

public class Application {

  ExecutorService executor = Executors.newSingleThreadExecutor();

  DefaultListModel<Attendant> attendants = new DefaultListModel<Attendant>();
  DefaultTableModel results = new DefaultTableModel(Result.tableColumns, 0);
  Algorithm algorithm;
  Parameters parameters;
  ArrayList<Pair<JTextField, Holder<Double>>> parameterBindings = new ArrayList<>();

  private JFrame frame;
  private JTextField textDate;
  private JTextField textOrigin;
  private JTextField textAge;
  private JTable tableResults;
  private JProgressBar progressBar;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    DatabaseManager.init();

    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        try {
          Application window = new Application();
          window.frame.setTitle("Meeting point planner");
          window.frame.setVisible(true);
          window.textOrigin.requestFocus();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public Application() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frame = new JFrame();
    frame.setBounds(100, 100, 940, 550);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JPanel panelMain = new JPanel();
    frame.getContentPane().add(panelMain, BorderLayout.CENTER);
    panelMain.setLayout(null);

    JPanel panelInput = new JPanel();
    panelInput.setBounds(10, 11, 309, 489);
    panelInput.setBorder(BorderFactory.createTitledBorder("Input"));
    panelMain.add(panelInput);
    panelInput.setLayout(null);

    JLabel lblDate = new JLabel("Date (YYYYMMDD):");
    lblDate.setBounds(10, 21, 94, 14);
    panelInput.add(lblDate);

    textDate = new JTextField();
    textDate.setText("20170815");
    textDate.setBounds(114, 18, 78, 20);
    panelInput.add(textDate);
    textDate.setColumns(10);

    JLabel lblAttendants = new JLabel("Attendants:");
    lblAttendants.setBounds(10, 46, 78, 14);
    panelInput.add(lblAttendants);

    JList<Attendant> list = new JList<Attendant>();
    list.setModel(attendants);
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    JScrollPane scrollAttendants = new JScrollPane(list);
    scrollAttendants.setBounds(10, 71, 289, 249);
    panelInput.add(scrollAttendants);

    JLabel lblOrigin = new JLabel("Origin:");
    lblOrigin.setBounds(10, 331, 46, 14);
    panelInput.add(lblOrigin);

    textOrigin = new JTextField();
    textOrigin.setBounds(66, 328, 114, 20);
    panelInput.add(textOrigin);
    textOrigin.setColumns(10);

    JLabel lblAge = new JLabel("Age:");
    lblAge.setBounds(10, 356, 46, 14);
    panelInput.add(lblAge);

    textAge = new JTextField();
    textAge.setBounds(66, 353, 114, 20);
    textAge.addActionListener(this::addAttendant);
    panelInput.add(textAge);
    textAge.setColumns(10);

    JButton btnAdd = new JButton("Add");
    btnAdd.addActionListener(this::addAttendant);
    btnAdd.setBounds(10, 381, 78, 23);
    panelInput.add(btnAdd);

    JButton btnRemove = new JButton("Remove");
    btnRemove.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int selectedIndex = list.getSelectedIndex();
        if (selectedIndex != -1) {
          attendants.remove(selectedIndex);
        }
      }
    });
    btnRemove.setBounds(94, 381, 86, 23);
    panelInput.add(btnRemove);

    algorithm = new DiscreteAlgorithm(10, d -> progressBar.setValue((int) (d * 1000)));

    JPanel panelResult = new JPanel();
    panelResult.setBounds(544, 11, 370, 489);
    panelResult.setBorder(BorderFactory.createTitledBorder("Result"));
    panelMain.add(panelResult);
    panelResult.setLayout(null);

    JButton btnCalculate = new JButton("Compute");
    btnCalculate.setBounds(20, 24, 89, 23);
    panelResult.add(btnCalculate);
    btnCalculate.addActionListener(e -> executor.submit(this::compute));

    progressBar = new JProgressBar();
    progressBar.setBounds(119, 28, 226, 14);
    panelResult.add(progressBar);
    progressBar.setMinimum(0);
    progressBar.setMaximum(1000);

    tableResults = new JTable();
    tableResults.setModel(results);

    JScrollPane scrollResults = new JScrollPane(tableResults);
    scrollResults.setBounds(20, 59, 325, 353);
    panelResult.add(scrollResults);

    JPanel panelParams = new JPanel();
    panelParams.setBounds(329, 11, 205, 489);
    panelParams.setBorder(BorderFactory.createTitledBorder("Parameters"));
    panelMain.add(panelParams);
    panelParams.setLayout(null);

    parameters = new Parameters();

    int positionY = 18;
    int additionY = 26;
    for (Map.Entry<Class<? extends ProductivityMap>, ParametersList> entry : parameters
        .entrySet()) {
      String name = entry.getKey().getSimpleName();
      name = name.substring(0, name.length() - 3);
      ParametersList parameters = entry.getValue();

      final JCheckBox chckbx = new JCheckBox(name);
      chckbx.setBounds(6, positionY, 150, 23);
      positionY += additionY;
      chckbx.setSelected(parameters.getUseThisMap());
      chckbx.addActionListener(e -> parameters.setUseThisMap(chckbx.isSelected()));
      panelParams.add(chckbx);

      for (Map.Entry<String, Holder<Double>> parameter : parameters.entrySet()) {
        final JLabel label = new JLabel(parameter.getKey());
        label.setBounds(32, positionY, 59, 14);
        panelParams.add(label);

        final JTextField textField = new JTextField();
        textField.setBounds(100, positionY - 3, 86, 20);
        textField.setColumns(10);
        textField.setText(parameter.getValue().get().toString());
        parameterBindings
            .add(new ImmutablePair<JTextField, Holder<Double>>(textField, parameter.getValue()));
        panelParams.add(textField);

        positionY += additionY;
      }
    }

  }

  private void addAttendant(ActionEvent e) {
    try {
      Place origin = PlaceFinder.city(textOrigin.getText());
      double age = Double.parseDouble(textAge.getText());
      Attendant at = new Attendant(origin, age);
      attendants.addElement(at);
      textOrigin.setText("");
      textAge.setText("");
    } catch (NotFoundException e1) {
      // do nothing
    } catch (SQLException e1) {
      e1.printStackTrace();
    }
    textOrigin.requestFocus();
  }

  private void compute() {
    if (attendants.size() > 0) {
      try {

        for (Pair<JTextField, Holder<Double>> binding : parameterBindings) {
          try {
            Double value = Double.parseDouble(binding.getLeft().getText());
            binding.getRight().set(value);
          } catch (NumberFormatException e) {
            e.printStackTrace();
            return;
          }
        }

        ProductivityMapsFactory mapsFactory = new ProductivityMapsFactory(parameters);
        for (Map.Entry<Class<? extends ProductivityMap>, ParametersList> entry : parameters
            .entrySet()) {
          ParametersList list = entry.getValue();
          if (list.getUseThisMap()) {
            Class<? extends ProductivityMap> clazz = entry.getKey();
            mapsFactory.addFactory(clazz);
          }
        }

        try {
          SimpleDateFormat parser = new SimpleDateFormat("yyyyMMdd");
          Date date = parser.parse(textDate.getText());
          ArrayList<Attendant> ats = Collections.list(attendants.elements());

          TreeSet<Result> resultSet =
              algorithm.find(date, ats.toArray(new Attendant[0]), mapsFactory);
          Object[][] rows = new Object[resultSet.size()][];
          int i = 0;
          for (Result r : resultSet) {
            rows[i] = r.getTableRow();
            i++;
          }
          results.setDataVector(rows, Result.tableColumns);
        } catch (ParseException e1) {
          e1.printStackTrace();
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      System.out.println("No attendants added");
    }
  }
}

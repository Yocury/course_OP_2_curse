package presentation.view.page;
//ToDo: Коментарии.

import data.db_manager.OrderDBManager;
import data.DataService;
import domain.usecases.DeleteUseCase;
import domain.usecases.batche.AddBatchUseCase;
import domain.usecases.order.AddOrderUseCase;
import presentation.MyConfig;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

public abstract class PagePanelView extends JPanel {
    public JTable table;
    protected DefaultTableModel tableModel;
    protected JLabel titleLabel;
    protected JButton addButton;
    protected JButton deleteButton;
    protected JButton editButton;
    protected JButton refreshButton;
    protected JPanel buttonPanel;
    private final AddBatchUseCase addBatchUseCase;
    private final AddOrderUseCase addOrderUseCase;
    private OrderDBManager db;
    private final DataService.BatchAnalysis dataService;

    public PagePanelView(String title) {
        setLayout(new BorderLayout());
        db = new OrderDBManager();
        this.addBatchUseCase = MyConfig.instance().addBatchUseCase();
        this.addOrderUseCase = MyConfig.instance().addOrderUseCase();
        dataService = new DataService.BatchAnalysis();
        // Заголовок страницы
        titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);

        // Таблица данных
        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        //настройки таблицы
        table.setDefaultEditor(Object.class, null);
        table.setFillsViewportHeight(true);
        table.setCellSelectionEnabled(true);
        table.setColumnSelectionAllowed(true);
        table.setRowSelectionAllowed(true);

        //Создаем и получаем кнопки
        addButton = new JButton("Добавить");
        deleteButton = new JButton("Удалить");
        editButton = new JButton("Изменить");
        refreshButton = new JButton("Обновить");

        // Панель с кнопками справа
        buttonPanel = new JPanel();

        addCustomButtons();

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Добавляем обработчики событий
        refreshButton.addActionListener(e -> refreshData());
    }

    protected abstract String FilteringToTypes();



    public abstract boolean checkForEdit(int column);

    protected abstract boolean isRowEditable(int selectedRow);

    protected abstract String getColumnNameByIndex(int column);



    protected void addButtonToPanel(JButton button) {
        buttonPanel.add(button);
    }

    protected abstract void addCustomButtons();

    // Абстрактный метод для обновления данных таблицы
    public abstract void updateData();

    protected void refreshData() {
        updateData();
        if (tableModel != null) {
            tableModel.fireTableDataChanged();
        }
    }

    protected void handleDelete(String title, int id) {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Вы уверены, что хотите удалить эту запись?",
            "Подтверждение удаления",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            DeleteUseCase deleteUseCase = MyConfig.instance().deleteUseCase(title);
            deleteUseCase.delete(title, id);
            refreshData();
        }
    }
}

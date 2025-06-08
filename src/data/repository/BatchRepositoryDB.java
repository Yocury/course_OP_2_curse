package data.repository;

import data.db_manager.BatchDBManager;
import data.DataService;
import domain.entities.Batch;
import domain.port.BatchRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Реализация репозитория для работы с партиями.
 * Использует BatchDBManager для взаимодействия с базой данных
 * и DataService для анализа данных партий.
 */
public class BatchRepositoryDB implements BatchRepository {
    private List<Batch> batches = new ArrayList<>();
    private final BatchDBManager db;
    private final DataService dataService;

    /**
     * Конструктор класса. Инициализирует менеджер базы данных,
     * сервис анализа данных и загружает партии.
     */
    public BatchRepositoryDB() {
        this.db = new BatchDBManager();
        this.dataService = new DataService();
        batches = db.load();
    }

    /**
     * Получает список всех партий.
     * @return список всех партий
     */
    @Override
    public List<Batch> getAll() {
        return batches = db.load();
    }

    /**
     * Фильтрует партии по статусу.
     * @param filter статус для фильтрации
     * @return список отфильтрованных партий
     */
    @Override
    public List<Batch> filter(String filter) {
        return db.loadByStatus(filter);
    }

    /**
     * Анализирует партию по ID.
     * @param id ID партии для анализа
     * @return результаты анализа партии
     */
    @Override
    public DataService.BatchAnalysis analyze(int id) {
        return dataService.analyzeBatch(id);
    }

    /**
     * Добавляет новую партию.
     * @param batch партия для добавления
     * @return добавленная партия
     */
    @Override
    public Batch addNew(Batch batch) {
        batches.add(batch);
        db.add(batch);
        return batch;
    }

    /**
     * Удаляет партию по ID.
     * @param id ID партии для удаления
     * @return true если удаление прошло успешно, false в случае ошибки
     */
    @Override
    public boolean delete(int id) {
        try {
            db.delete(id);
            batches.removeIf(b -> b.getId() == id);
            return true;
        } catch (Exception e) {
            System.err.println("Ошибка при удалении партии: " + e.getMessage());
            return false;
        }
    }

    /**
     * Обновляет существующую партию.
     * @param batch партия для обновления
     * @return обновленная партия
     */
    @Override
    public Batch update(Batch batch) {
        for (int i = 0; i < batches.size(); i++) {
            if (batches.get(i).getId() == batch.getId()) {
                batches.set(i, batch);
                db.update(batch);
                break;
            }
        }
        return batch;
    }

    /**
     * Анализирует партию по ID.
     * @param batchId ID партии для анализа
     * @return результаты анализа партии
     */
    @Override
    public Object analyzeBatch(int batchId) {
        return dataService.analyzeBatch(batchId);
    }

    /**
     * Получает следующий доступный ID для новой партии.
     * @return следующий доступный ID
     */
    @Override
    public int getNextId() {
        return db.getId("batches") + 1;
    }
}

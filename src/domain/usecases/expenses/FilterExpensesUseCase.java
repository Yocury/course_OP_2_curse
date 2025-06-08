package domain.usecases.expenses;

import domain.entities.Expenses;
import domain.port.ExpensesRepository;
import java.util.List;

public class FilterExpensesUseCase {
    private final ExpensesRepository expensesRepository;
    public FilterExpensesUseCase(ExpensesRepository expensesRepository) {
        this.expensesRepository = expensesRepository;
    }
    public List<Expenses> invoke(String type) {
        return expensesRepository.getByType(type);
    }
} 
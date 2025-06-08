package domain.usecases.expenses;

import domain.entities.Expenses;
import domain.port.ExpensesRepository;
import java.util.List;

public class GetAllExpensesUseCase {
    private final ExpensesRepository expensesRepository;
    public GetAllExpensesUseCase(ExpensesRepository expensesRepository) {
        this.expensesRepository = expensesRepository;
    }
    public List<Expenses> invoke() {
        return expensesRepository.getAll();
    }
} 
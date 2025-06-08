package domain.usecases.expenses;

import domain.entities.Expenses;
import domain.port.ExpensesRepository;

public class UpdateExpensesUseCase {
    private final ExpensesRepository expensesRepository;
    public UpdateExpensesUseCase(ExpensesRepository expensesRepository) {
        this.expensesRepository = expensesRepository;
    }
    public Expenses invoke(Expenses expense) {
        return expensesRepository.update(expense);
    }
} 
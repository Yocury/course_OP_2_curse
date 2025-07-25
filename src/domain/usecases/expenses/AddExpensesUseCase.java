package domain.usecases.expenses;

import domain.entities.Expenses;
import domain.port.ExpensesRepository;

public class AddExpensesUseCase {
    private final ExpensesRepository expensesRepository;
    public AddExpensesUseCase(ExpensesRepository expensesRepository) {
        this.expensesRepository = expensesRepository;
    }
    public Expenses invoke(Expenses expenses) {
        return expensesRepository.addNew(expenses);
    }
}

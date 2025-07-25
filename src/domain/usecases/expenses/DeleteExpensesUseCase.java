package domain.usecases.expenses;

import domain.port.ExpensesRepository;

public class DeleteExpensesUseCase {
    private final ExpensesRepository expensesRepository;
    public DeleteExpensesUseCase(ExpensesRepository expensesRepository) {
        this.expensesRepository = expensesRepository;
    }
    public boolean invoke(int id) {
        return expensesRepository.delete(id);
    }
}

package domain.usecases.expenses;

import domain.port.ExpensesRepository;

public class GetNextExpenseIdUseCase {
    private final ExpensesRepository expensesRepository;
    public GetNextExpenseIdUseCase(ExpensesRepository expensesRepository) {
        this.expensesRepository = expensesRepository;
    }
    public int invoke() {
        return expensesRepository.getNextId();
    }
} 
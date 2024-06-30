import java.time.LocalDate;
import java.util.Date;

public class Transaction {
    private String customerId="";
    private String accountNumber="";
    private double previousBalance=0.0;
    private double currentTransaction=0.0;
    private double newBalance=0.0;
    private String transactionType="";
    private double totalCreditedMonth=0.0;
    private double totalDebitedMonth =0.0;
    private double reward=0.0;
    private LocalDate transactionDate=null;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public double getPreviousBalance() {
        return previousBalance;
    }

    public void setPreviousBalance(double previousBalance) {
        this.previousBalance = previousBalance;
    }

    public double getCurrentTransaction() {
        return currentTransaction;
    }

    public void setCurrentTransaction(double currentTransaction) {
        this.currentTransaction = currentTransaction;
    }

    public double getNewBalance() {
        return newBalance;
    }

    public void setNewBalance(double newBalance) {
        this.newBalance = newBalance;
    }

    public double getTotalDebitedMonth() {
        return totalDebitedMonth;
    }

    public void setTotalDebitedMonth(double totalDebitedMonth) {
        this.totalDebitedMonth = totalDebitedMonth;
    }

    public double getTotalCreditedMonth() {
        return totalCreditedMonth;
    }

    public void setTotalCreditedMonth(double totalCreditedMonth) {
        this.totalCreditedMonth = totalCreditedMonth;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public double getReward() {
        return reward;
    }

    public void setReward(double reward) {
        this.reward = reward;
    }
}

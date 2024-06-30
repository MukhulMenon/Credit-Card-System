public class Account {
    private String customerid;
    private String accountNumber = "";


    private double balance=0.0;
    private double maxWithdrawal=0.0;
    private double maxOnlineLimit =0.0;
    private double maxWirelessLimit=0.0;
    private double totalCreditMonth =0.0;
    private double totalDebitMonth =0.0;

    public String getCustomerid() {
        return customerid;
    }

    public void setCustomerid(String customerid) {
        this.customerid = customerid;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getMaxWithdrawal() {
        return maxWithdrawal;
    }

    public void setMaxWithdrawal(double maxWithdrawal) {
        this.maxWithdrawal = maxWithdrawal;
    }

    public double getMaxOnlineLimit() {
        return maxOnlineLimit;
    }

    public void setMaxOnlineLimit(double maxOnlineLimit) {
        this.maxOnlineLimit = maxOnlineLimit;
    }

    public double getMaxWirelessLimit() {
        return maxWirelessLimit;
    }

    public void setMaxWirelessLimit(double maxWirelessLimit) {
        this.maxWirelessLimit = maxWirelessLimit;
    }

    public double getTotalCreditMonth() {
        return totalCreditMonth;
    }

    public void setTotalCreditMonth(double totalCreditMonth) {
        this.totalCreditMonth = totalCreditMonth;
    }

    public double getTotalDebitMonth() {
        return totalDebitMonth;
    }

    public void setTotalDebitMonth(double totalDebitMonth) {
        this.totalDebitMonth = totalDebitMonth;
    }
}

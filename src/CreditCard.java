import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.sql.Connection;

public class CreditCard {
    public static void InitialSetup(Connection conn,DBFunctions db){
        db.create_table(conn,"customers","(customerid VARCHAR(10), fullname VARCHAR(50), accounts VARCHAR(100),rewards FLOAT(15), primary key(customerid));");
        db.create_table(conn,"transactions","(customerid VARCHAR(10), accountnumber VARCHAR(15), prevbalance FLOAT(15),transactiontype VARCHAR(40), currenttransaction FLOAT(15), newbalance FLOAT(15),reward FLOAT(15), transactiondate DATE) ;");
        db.create_table(conn,"accounts","(customerid VARCHAR(10), accountnumber VARCHAR(100), balance FLOAT(15),maxwithdrawallimit FLOAT(15), maxonlinelimit FLOAT(15), maxwirelesslimit FLOAT(15), totalcreditedmonth FLOAT(15), totaldebitedmonth FLOAT(15), primary key(accountnumber));");
        db.create_table(conn,"rewards","(customerid VARCHAR(10), accounts VARCHAR(100), totaldebitedmonth FLOAT(15), rewardtype VARCHAR(25), rewardvalue FLOAT(15));");
    }

    public static void AccountAddition(Connection conn, DBFunctions db,Scanner sc) {
        String addAccount = "";
        System.out.print("Would you like to add accounts [Y/N] : ");
        addAccount = sc.next();
        List<Account> accounts = new ArrayList<>();
        while (addAccount.equals("Y") || addAccount.equals("y")) {
            Account account = new Account();
            account = SetAccountDetails(sc, db, conn, account);
            List<Account> currentList = db.get_account_data(conn, "accounts");
            if (!currentList.isEmpty()) {
                for (Account eachAccount : currentList) {
                    String val1 = eachAccount.getCustomerid();
                    String val2 = account.getCustomerid();
                    if (!val1.equals(val2)) {
                        accounts.add(account);
                    } else {
                        System.out.println("Account already exists!");
                    }
                }
            }
            accounts.add(account);

            System.out.print("Add another Account [Y/N] : ");
            addAccount = sc.next();
        }
        if(!accounts.isEmpty()) {
            db.insert_data_accounts(conn, "accounts", accounts);
        }
    }

    public static Account SetAccountDetails(Scanner sc,DBFunctions db,Connection conn,Account account){
        System.out.print("Enter Customer ID : ");
        account.setCustomerid(sc.next());
        System.out.print("Enter Account number : ");
        account.setAccountNumber(sc.next());
        System.out.print("Enter Balance : ");
        double balance=sc.nextDouble();
        account.setBalance(balance);
        account.setMaxWithdrawal(balance/10);
        account.setMaxOnlineLimit(balance/10);
        account.setMaxWirelessLimit(balance/20);
        return account;
    }

    public static void CustomerAddition(Connection conn,DBFunctions db ,Scanner sc){
        String addCustomers="";
        System.out.print("Would you like to add customers [Y/N] : ");
        addCustomers=sc.next();
        List<UserData> customers = new ArrayList<>();
        while (addCustomers.equals("Y")||addCustomers.equals("y")) {
            UserData customer= new UserData();
            customer = SetCustomerDetails(sc,db,conn,customer);
            List<UserData> currentList= db.get_customer_data(conn,"customers");
            if (!currentList.isEmpty()){
            for(UserData eachCustomer:currentList) {
                String val1 = eachCustomer.getCustomerId();
                String val2 = customer.getCustomerId();
                    if (!val1.equals(val2)) {
                        customers.add(customer);
                    } else {
                        System.out.println("Customer already exists!");
                    }
                }
            }
            customers.add(customer);

            System.out.print("Add another customer [Y/N] : ");
            addCustomers=sc.next();
        }
        if(!customers.isEmpty()) {
            db.insert_data_customers(conn, "customers", customers);
        }
    }

    public static UserData SetCustomerDetails(Scanner sc,DBFunctions db,Connection conn,UserData customer){
        System.out.print("Enter Customer ID : ");
        customer.setCustomerId(sc.next());
        System.out.print("Enter Customer First Name : ");
        customer.setFirstName(sc.next());
        System.out.print("Enter Customer Last Name : ");
        customer.setLastName(sc.next());
        String accounts=db.get_accounts(conn,customer.getCustomerId());
        customer.setAccounts(accounts);
        return customer;

    }

    public static void Transactions(Connection conn,DBFunctions db, Scanner sc){
        List<UserData> customers = db.get_customer_data(conn,"customers");
        List<String> customerIdList =new ArrayList<>();
        for(UserData eachCustomer:customers){
            customerIdList.add(eachCustomer.getCustomerId());
        }
        System.out.print("Choose the type of transaction [Credit - C | Withdraw - W | Online - O | Wireless - WL | Transfer - T ] : ");
        String type=sc.next();
        InitializeTransaction(conn,sc,customerIdList,db,type);
    }

    public static void InitializeTransaction(Connection conn,Scanner sc,List<String> customerIdList, DBFunctions db,String type){
        System.out.print("Enter Your Customer ID : ");
        String custId=sc.next();
        if(!customerIdList.contains(custId)){
            System.out.println("Customer does not exist!");
        }
        else{
            UserData senderCustomer = db.get_customer_row_with_value(conn,"customerid",custId);
            String accountsSender=senderCustomer.getAccounts();
            System.out.print("Choose account number [ "+accountsSender+" ]  : ");
            accountsSender=accountsSender.replace(" ,","_");
            List<String> accountnumbersSender= List.of(accountsSender.split("_"));
            String accountSender=sc.next();
            if(accountnumbersSender.contains(accountSender)) {
                if(type.equals("T")|| type.equals("t")){
                    System.out.print("Enter Customer if of  :");
                    String custIdReciever=sc.next();

                    UserData recieverCustomer = db.get_customer_row_with_value(conn,"customerid",custIdReciever);
                    String accountsReciever=recieverCustomer.getAccounts();
                    System.out.print("Choose account number [ "+accountsReciever+" ]  : ");
                    accountsReciever=accountsReciever.replace(" ,","_");
                    List<String> accountnumbersReciever= List.of(accountsReciever.split("_"));
                    String accountReciever=sc.next();
                    if(accountnumbersReciever.contains(accountReciever)) {
                        RunTransactionTransfer(conn, db, senderCustomer,recieverCustomer,accountSender,accountReciever);
                    }
                    else{
                        System.out.println("Invalid account number!");
                    }
                }
                else {
                    UserData currentCustomer = db.get_customer_row_with_value(conn, "customerid", custId);
                    String accounts = currentCustomer.getAccounts();
                    System.out.print("Choose account number [ " + accounts + " ]  : ");
                    accounts = accounts.replace(" ,", "_");
                    List<String> accountnumbers = List.of(accounts.split("_"));
                    String account = sc.next();
                    if (accountnumbers.contains(account)) {
                        RunTransaction(conn, db, currentCustomer, type, account);
                    } else {
                        System.out.println("Invalid account number!");
                    }
                }
            }
        }
    }

    public static void RunTransaction(Connection conn,DBFunctions db, UserData customer,String type,String account){
        Account currentAccount=db.get_account_row_with_value(conn,"accounts","accountnumber",account);
        double currentBalance= currentAccount.getBalance();
        String customerId= customer.getCustomerId();
        double maxWithdrawalLimit =currentAccount.getMaxWithdrawal();
        double maxOnlineLimit=currentAccount.getMaxOnlineLimit();
        double maxWirelessLimit = currentAccount.getMaxWirelessLimit();
        double totalCreditMonth = currentAccount.getTotalCreditMonth();
        double totalDebitMonth =currentAccount.getTotalDebitMonth();
        Scanner sc = new Scanner(System.in);

            Transaction currentTransaction = new Transaction();
            System.out.println("Current Balance : "+currentBalance);
            currentTransaction.setPreviousBalance(currentBalance);
            System.out.print("Enter Amount : ");
            double curTransaction =sc.nextDouble();
            currentTransaction.setTotalDebitedMonth(totalDebitMonth);
            currentTransaction.setTotalCreditedMonth(totalCreditMonth);
        switch (type) {
            case "W":
            case "w": {
                if ((currentBalance - curTransaction) >= 0.0) {
                    if (curTransaction < maxWithdrawalLimit) {
                        currentTransaction.setCustomerId(customerId);
                        currentTransaction.setAccountNumber(account);
                        currentTransaction.setCurrentTransaction(curTransaction);
                        List<Double> accountBalanceData = db.insert_data_transactions(conn, "transactions", currentTransaction,type);
                        double reward= db.insert_data_reward(conn,customerId,type,currentTransaction,accountBalanceData.get(1));
                        db.update_customers_rewards(conn,"customers",reward,customerId);
                        db.update_customers_balance(conn, "accounts", accountBalanceData.get(0), account);
                        db.update_debit_transaction(conn, "accounts", accountBalanceData.get(1), account);
                    } else {
                        System.out.println("Insufficient Balance");
                    }
                } else {
                    System.out.println("You have entered a value over your withdrawal limit !");
                }

                break;
            }
            case "C":
            case "c": {
                currentTransaction.setCustomerId(customerId);
                currentTransaction.setAccountNumber(account);
                currentTransaction.setCurrentTransaction(curTransaction);
                List<Double> accountBalanceData = db.insert_data_transactions(conn, "transactions", currentTransaction,type);
                db.update_customers_balance(conn, "accounts", accountBalanceData.get(0), account);
                db.update_credit_transaction(conn, "accounts", accountBalanceData.get(1), account);
                break;
            }
            case "O":
            case "o": {
                if ((currentBalance - curTransaction) >= 0.0) {
                    if (curTransaction < maxOnlineLimit) {
                        currentTransaction.setCustomerId(customerId);
                        currentTransaction.setAccountNumber(account);
                        currentTransaction.setCurrentTransaction(curTransaction);
                        List<Double> accountBalanceData = db.insert_data_transactions(conn, "transactions", currentTransaction,type);
                        double reward= db.insert_data_reward(conn,customerId,type,currentTransaction,accountBalanceData.get(1));
                        db.update_customers_rewards(conn,"customers",reward,customerId);
                        db.update_customers_balance(conn, "accounts", accountBalanceData.get(0), account);
                        db.update_debit_transaction(conn, "accounts", accountBalanceData.get(1), account);
                    } else {
                        System.out.println("Insufficient Balance");
                    }
                } else {
                    System.out.println("You hava entered a value over you online transaction limit !");
                }
                break;
            }
            case "WL":
            case "wl": {
                if ((currentBalance - curTransaction) >= 0.0) {
                    if (curTransaction < maxWirelessLimit) {
                        currentTransaction.setCustomerId(customerId);
                        currentTransaction.setAccountNumber(account);
                        currentTransaction.setCurrentTransaction(curTransaction);
                        List<Double> accountBalanceData = db.insert_data_transactions(conn, "transactions", currentTransaction,type);
                        double reward= db.insert_data_reward(conn,customerId,type,currentTransaction,accountBalanceData.get(1));
                        db.update_customers_rewards(conn,"customers",reward,customerId);
                        db.update_customers_balance(conn, "accounts", accountBalanceData.get(0), account);
                        db.update_debit_transaction(conn, "accounts", accountBalanceData.get(1), account);
                    } else {
                        System.out.println("Insufficient Balance");
                    }
                } else {
                    System.out.println("You have entered a value over your wireless transfer limit!");
                }
                break;
            }
        }

    }
    public static void RunTransactionTransfer(Connection conn,DBFunctions db, UserData senderCustomer,UserData recieverCustomer,String accountSender,String accountReviecer){
        Account senderAccount=db.get_account_row_with_value(conn,"accounts","accountnumber",accountSender);
        double currentBalanceSender= senderAccount.getBalance();
        String customerIdSender= senderCustomer.getCustomerId();
        double totalDebitMonth =senderAccount.getTotalDebitMonth();

        Account recieverAccount=db.get_account_row_with_value(conn,"accounts","accountnumber",accountReviecer);
        double currentBalanceReceiver= recieverAccount.getBalance();
        String customerIdReceiver= recieverCustomer.getCustomerId();
        double totalCreditMonthReceiver = recieverAccount.getTotalCreditMonth();
        Scanner sc = new Scanner(System.in);

        Transaction currentSender = new Transaction();
            currentSender.setCustomerId(customerIdSender);
            currentSender.setAccountNumber(accountSender);
        Transaction currentReceiver = new Transaction();
            currentReceiver.setCustomerId(customerIdReceiver);
            currentReceiver.setAccountNumber(accountReviecer);
            currentReceiver.setPreviousBalance(currentBalanceReceiver);

        System.out.println("Your Balance : "+currentBalanceSender);
            currentSender.setPreviousBalance(currentBalanceSender);
        System.out.print("Enter Amount : ");
            double curTransaction =sc.nextDouble();
            currentSender.setCurrentTransaction(curTransaction);
            currentReceiver.setCurrentTransaction(curTransaction);
            currentSender.setTotalDebitedMonth(totalDebitMonth);
            currentReceiver.setTotalCreditedMonth(totalCreditMonthReceiver);

        List<Double> accountBalanceData = db.insert_data_transactions_transfer(conn, "transactions", currentSender,currentReceiver);

        db.update_customers_balance(conn, "accounts", accountBalanceData.get(0), accountSender);
        db.update_debit_transaction(conn, "accounts", accountBalanceData.get(1), accountSender);

        db.update_customers_balance(conn,"accounts",accountBalanceData.get(2),accountReviecer);
        db.update_credit_transaction(conn,"accounts",accountBalanceData.get(3),accountReviecer);



    }

    public static void Statements(Connection conn, DBFunctions db, Scanner sc){
        System.out.print("Enter Customer id : ");
        String customerid=sc.next();
        db.get_statement(conn,customerid);
    }
    /////////////////////// Main class /////////////////////////

    public static void main(String[] args){
       DBFunctions db = new DBFunctions();
       Connection conn = db.connect_to_db("creditcardsystem","postgres","mirev");
       Scanner sc = new Scanner(System.in);
       System.out.print("Is this an initial run on the system [Y/N] : ");
       String initial=sc.next();
       if(initial.equals("Y")||initial.equals("y")) {
           InitialSetup(conn, db);
       }
        String status="Y";
        while(status.equals("Y") || status.equals("y")){
            System.out.print("Choose Operation [Account Addition - A | Customer Addition - C | Transaction - T | Statement - S] : ");
            String operation=sc.next();
            switch (operation) {
                case "C", "c" -> CustomerAddition(conn, db, sc);
                case "T", "t" -> Transactions(conn, db, sc);
                case "A", "a" -> AccountAddition(conn, db, sc);
                case "S", "s" -> Statements(conn,db,sc);
                default -> System.out.println("Invalid input!");
            }
            System.out.print("Would you like to perform other operations [Y/N] : ");
            status=sc.next();
        }

    }
}
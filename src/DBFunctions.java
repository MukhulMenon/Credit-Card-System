import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DBFunctions {
    public Connection connect_to_db(String dbName,String user,String pass){
        Connection conn =null;
        try{
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+dbName,user,pass);
            if(conn!=null){
                System.out.println("Connection is established!");
            }
            else{
                System.out.println("Connection error!!");
            }
        }catch (Exception e){
            System.out.println(e);
        }
        return conn;
    }

    public void create_table(Connection conn, String table_name,String table_details){
        Statement statement;
                try{
                    String query = "Create Table "+table_name+table_details;
                    statement=conn.createStatement();
                    statement.executeUpdate(query);
                    System.out.println("Table Created");
                }catch (Exception e){
                    System.out.println(e);
                }
    }

    public void delete_row(Connection conn, String table_name,String condition){
        Statement statement;

        try {
            String query = "Delete from " + table_name + " where " + condition;
            statement = conn.createStatement();
            statement.executeUpdate(query);
            System.out.println("Data Deleted !");
            read_data_customer(conn);
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public void drop_table(Connection conn,String table_name){
        Statement statement;
        try{
            String query="Drop table "+table_name;
            statement=conn.createStatement();
            statement.executeUpdate(query);
            System.out.println("Table dropped");
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public void read_data_customer(Connection conn){
        Statement statement;
        ResultSet rs = null;
        try{
            String readQuery="Select * from customers";
            statement=conn.createStatement();
            rs = statement.executeQuery(readQuery);

            while(rs.next()){
                System.out.println("-------------------------------------------------------------------------------");
                System.out.print(rs.getString("customerid")+" | ");
                System.out.print(rs.getString("fullname")+" | ");
                System.out.print(rs.getString("accounts")+" | ");
                System.out.println("-------------------------------------------------------------------------------");
            }

        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    public void insert_data_customers(Connection conn,String table_name, List<UserData> customers){
        Statement statement;
        try{
            String insertQuery = "INSERT INTO "+table_name+"(customerid, fullname, accounts) VALUES (?, ?, ?)";
            PreparedStatement pstmt= conn.prepareStatement(insertQuery);
            for (UserData customer : customers) {
                pstmt.setString(1, customer.getCustomerId());
                pstmt.setString(2, customer.getFirstName() + " " + customer.getLastName());
                pstmt.setString(3, customer.getAccounts());
                pstmt.executeUpdate();
            }
            System.out.println("Data Entered Successfully");
        }catch (Exception e){
            System.out.println(e);
        }
    }


    public void search_customer_by_id(Connection conn,String column_name,String searchValue,String table_name){
        Statement statement;
        ResultSet rs=null;
        try{
            String query= "select * from "+table_name+" where customerid ='"+searchValue+"'";
            statement =conn.createStatement();
            rs=statement.executeQuery(query);
            while(rs.next()){
                System.out.println("-------------------------------------------------------------------------------");
                System.out.print(rs.getString("customerid")+" | ");
                System.out.print(rs.getString("fullname")+" | ");
                System.out.print(rs.getString("accounts")+" | ");
                System.out.println("-------------------------------------------------------------------------------");
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }
    //////////////////////////// Customer db functions //////////////////////////////

    public List<UserData> get_customer_data(Connection conn,String table_name){
        List<UserData> current_list=new ArrayList<>();
        Statement statement;
        ResultSet rs = null;
        try{
            String readQuery="Select * from "+table_name;
            statement=conn.createStatement();
            rs = statement.executeQuery(readQuery);

            while(rs.next()){
                UserData customer = new UserData();
                customer.setCustomerId(rs.getString("customerid"));
                String[] name_list = rs.getString("fullname").split(" ");
                customer.setFirstName(name_list[0]);
                customer.setLastName(name_list[1]);
                customer.setAccounts(rs.getString("accounts"));
                current_list.add(customer);
            }
        }
        catch (Exception e){
            System.out.println(e);
        }
        return current_list;
    }

    public UserData get_customer_row_with_value(Connection conn, String columnName, String searchValue){
        UserData customer=new UserData();
        Statement statement;
        ResultSet rs = null;
        try{
            String query= "select * from customers where "+columnName+" = '"+searchValue+"'";
            statement =conn.createStatement();
            rs=statement.executeQuery(query);

            while(rs.next()){
                customer.setCustomerId(rs.getString("customerid"));
                String[] name_list = rs.getString("fullname").split(" ");
                customer.setFirstName(name_list[0]);
                customer.setLastName(name_list[1]);
                customer.setAccounts(rs.getString("accounts"));
                customer.setReward(rs.getDouble("rewards"));
            }
        }
        catch (Exception e){
            System.out.println(e);
        }
        return customer;
    }

    public void update_customers_balance(Connection conn, String table_name,double currentBalance,String accountnumber){
        Statement statement;

        try{
            String insertQuery = "Update "+table_name+" set balance = '"+currentBalance+"' where accountnumber = '"+accountnumber+"'";
            statement= conn.createStatement();
            statement.executeUpdate(insertQuery);
        }catch (Exception e){
            System.out.println(e);
        }
    }

    /////////////////////////////////// Transactions db functions /////////////////////////////////
    public List<Double> insert_data_transactions(Connection conn,String table_name, Transaction transaction, String type){
        long millis=System.currentTimeMillis();
        java.sql.Date transactionDate=new java.sql.Date(millis);

        List<Double> data = new ArrayList<>();
        double newBalance =0.0;
        double totalCreditMonth = transaction.getTotalCreditedMonth();
        double totalDebitedMonth =transaction.getTotalDebitedMonth();
        double currentTransaction=transaction.getCurrentTransaction();
        double prevBalance= transaction.getPreviousBalance();
        double rewardValue=0.0;

        String transactionType="";
        switch (type){
            case "C":
            case "c": {
                transactionType="Credited";
                newBalance = prevBalance + currentTransaction;
                data.add(newBalance);
                totalCreditMonth+=currentTransaction;
                data.add(totalCreditMonth);
                break;
            }
            case "W":
            case "w":
            case "O":
            case "o":
            case "WL":
            case "wl":
            {   transactionType="Debited - "+type;
                newBalance = prevBalance - currentTransaction;
                data.add(newBalance);
                totalDebitedMonth += currentTransaction;
                data.add(totalDebitedMonth);

                switch (type){
                    case "W":
                    case "w":{
                        rewardValue=currentTransaction*(0.05);
                        break;
                    }
                    case "O":
                    case "o":{
                        rewardValue=currentTransaction*(0.1);
                        break;
                    }
                    case "WL":
                    case "wl":{
                        rewardValue=currentTransaction*(0.075);
                    }
                }
                break;
            }
        }
        try{
            String insertQuery = "INSERT INTO "+table_name+"(customerid, accountnumber, prevbalance, transactiontype, currenttransaction, newbalance, reward, transactiondate) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt= conn.prepareStatement(insertQuery);

                pstmt.setString(1, transaction.getCustomerId());
                pstmt.setString(2, transaction.getAccountNumber());
                pstmt.setDouble(3, prevBalance);
                pstmt.setString(4, transactionType);
                pstmt.setDouble(5, currentTransaction);
                pstmt.setDouble(6, newBalance);
                pstmt.setDouble(7,rewardValue);
                pstmt.setDate(8,transactionDate);
                pstmt.executeUpdate();

            System.out.println("Transaction Added Successfully");
        }catch (Exception e){
            System.out.println(e);
        }
        return data;
    }

    public List<Double> insert_data_transactions_transfer(Connection conn,String table_name, Transaction senderTransaction, Transaction recieverTransaction){
        long millis=System.currentTimeMillis();
        java.sql.Date transactionDate=new java.sql.Date(millis);

        List<Double> data = new ArrayList<>();
        double totalCreditMonth = recieverTransaction.getTotalCreditedMonth();
        double totalDebitedMonth =senderTransaction.getTotalDebitedMonth();
        double currentTransaction=senderTransaction.getCurrentTransaction();
        double senderPrevBalance= senderTransaction.getPreviousBalance();
        double recieverPrevBalance = recieverTransaction.getPreviousBalance();

        double senderNewBalance= senderPrevBalance-currentTransaction;
        data.add(senderNewBalance);
        double senderDebitTotal=totalDebitedMonth+=currentTransaction;
        data.add(senderDebitTotal);

        double recieverNewBalance=recieverPrevBalance+currentTransaction;
        data.add(recieverNewBalance);
        double recieverCreditTotal=totalCreditMonth+=currentTransaction;
        data.add(recieverCreditTotal);


        try{
            String insertQuery = "INSERT INTO "+table_name+"(customerid, accountnumber, prevbalance, transactiontype, currenttransaction, newbalance, transactiondate) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt= conn.prepareStatement(insertQuery);

            pstmt.setString(1, senderTransaction.getCustomerId());
            pstmt.setString(2, senderTransaction.getAccountNumber());
            pstmt.setDouble(3, senderPrevBalance);
            pstmt.setString(4, "Transfer");
            pstmt.setDouble(5, currentTransaction);
            pstmt.setDouble(6, senderNewBalance);
            pstmt.setDate(7,transactionDate);
            pstmt.executeUpdate();

            System.out.println("Transaction Added Successfully");
        }catch (Exception e){
            System.out.println(e);
        }
        try{
            String insertQuery = "INSERT INTO "+table_name+"(customerid, accountnumber, prevbalance, transactiontype, currenttransaction, newbalance, transactiondate) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt= conn.prepareStatement(insertQuery);

            pstmt.setString(1, recieverTransaction.getCustomerId());
            pstmt.setString(2, recieverTransaction.getAccountNumber());
            pstmt.setDouble(3, recieverPrevBalance);
            pstmt.setString(4, "Credited");
            pstmt.setDouble(5, currentTransaction);
            pstmt.setDouble(6, recieverNewBalance);
            pstmt.setDate(7,transactionDate);
            pstmt.executeUpdate();

            System.out.println("Transaction Added Successfully");
        }catch (Exception e){
            System.out.println(e);
        }
        return data;
    }


    public void update_credit_transaction(Connection conn, String table_name,double totalcreditedmonth,String accountnumber){
        Statement statement;

        try{
            String insertQuery = "Update "+table_name+" set totalcreditedmonth = '"+totalcreditedmonth+"' where accountnumber = '"+accountnumber+"'";
            statement= conn.createStatement();
            statement.executeUpdate(insertQuery);
        }catch (Exception e){
            System.out.println(e);
        }
    }
    public void update_debit_transaction(Connection conn, String table_name,double totaldebitedmonth,String accountnumber){
        Statement statement;

        try{
            String insertQuery = "Update "+table_name+" set totaldebitedmonth = '"+totaldebitedmonth+"' where accountnumber = '"+accountnumber+"'";
            statement= conn.createStatement();
            statement.executeUpdate(insertQuery);
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public List<Transaction> get_transactions_with_customerid(Connection conn, String searchValue){
        List<Transaction> transactions= new ArrayList<>();
        Statement statement;
        ResultSet rs = null;
        try{
            String query= "select * from transactions where customerid = '"+searchValue+"'";
            statement =conn.createStatement();
            rs=statement.executeQuery(query);

            while(rs.next()){
                Transaction transaction=new Transaction();
                transaction.setCustomerId(rs.getString("customerid"));
                transaction.setAccountNumber(rs.getString("accountnumber"));
                transaction.setPreviousBalance(rs.getDouble("prevbalance"));
                transaction.setTransactionType(rs.getString("transactiontype"));
                transaction.setCurrentTransaction(rs.getDouble("currenttransaction"));
                transaction.setNewBalance(rs.getDouble("newbalance"));
                transaction.setReward(rs.getDouble("reward"));
                transaction.setTransactionDate(rs.getDate("transactiondate").toLocalDate());
                transactions.add(transaction);
            }
        }
        catch (Exception e){
            System.out.println(e);
        }
        return transactions;
    }

    ////////////////////////////// Account db functions //////////////////////////////////
    public String get_accounts(Connection conn,String customerId){
        String accounts="";
        Statement statement;
        ResultSet rs = null;
        try {
            String query = "select * from accounts where customerid = '" + customerId + "'";
            statement = conn.createStatement();
            rs = statement.executeQuery(query);
            accounts=accounts.concat("[ ");

            while (rs.next()) {
                accounts=accounts.concat(rs.getString("accountnumber")+" ,");
            }
        }
        catch (Exception e){
            System.out.println(e);
        }
        accounts=accounts.concat("]");
        accounts=accounts.replace("[ ","");
        accounts=accounts.replace(" ,]","");
        return accounts;
    }
    public void insert_data_accounts(Connection conn,String table_name, List<Account> accounts){
        try{
            String insertQuery = "INSERT INTO "+table_name+"(customerid, accountnumber, balance, maxwithdrawallimit, maxonlinelimit, maxwirelesslimit) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt= conn.prepareStatement(insertQuery);
            for (Account account : accounts) {
                pstmt.setString(1, account.getCustomerid());
                pstmt.setString(2, account.getAccountNumber());
                pstmt.setDouble(3, account.getBalance());
                pstmt.setDouble(4, account.getMaxWithdrawal());
                pstmt.setDouble(5, account.getMaxOnlineLimit());
                pstmt.setDouble(6, account.getMaxWirelessLimit());

                pstmt.executeUpdate();
            }
            System.out.println("Data Entered Successfully");
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public List<Account> get_account_data(Connection conn,String table_name){
        List<Account> current_list=new ArrayList<>();
        Statement statement;
        ResultSet rs = null;
        try{
            String readQuery="Select * from "+table_name;
            statement=conn.createStatement();
            rs = statement.executeQuery(readQuery);

            while(rs.next()){
                Account account = new Account();
                account.setCustomerid(rs.getString("customerid"));
                account.setAccountNumber(rs.getString("accountnumber"));
                account.setBalance(rs.getDouble("balance"));
                account.setMaxWithdrawal(rs.getDouble("maxwithdrawallimit"));
                account.setMaxOnlineLimit(rs.getDouble("maxonlinelimit"));
                account.setMaxWirelessLimit(rs.getDouble("maxwirelesslimit"));
                current_list.add(account);
            }
        }
        catch (Exception e){
            System.out.println(e);
        }
        return current_list;
    }
    public Account get_account_row_with_value(Connection conn, String table_name, String columnName, String searchValue){
        Account account=new Account();
        Statement statement;
        ResultSet rs = null;
        try{
            String query= "select * from "+table_name+" where "+columnName+" = '"+searchValue+"'";
            statement =conn.createStatement();
            rs=statement.executeQuery(query);

            while(rs.next()){
                account.setCustomerid(rs.getString("customerid"));
                account.setAccountNumber(rs.getString("accountnumber"));
                account.setBalance(rs.getDouble("balance"));
                account.setMaxWithdrawal(rs.getDouble("maxwithdrawallimit"));
                account.setMaxOnlineLimit(rs.getDouble("maxonlinelimit"));
                account.setMaxWirelessLimit(rs.getDouble("maxwirelesslimit"));
                account.setTotalCreditMonth(rs.getDouble("totalcreditedmonth"));
                account.setTotalDebitMonth(rs.getDouble("totaldebitedmonth"));
            }
        }
        catch (Exception e){
            System.out.println(e);
        }
        return account;
    }
    public List<Account> get_accounts_row_with_customerid(Connection conn, String searchValue){
        List<Account> accounts= new ArrayList<>();
        Statement statement;
        ResultSet rs = null;
        try{
            String query= "select * from accounts where customerid = '"+searchValue+"'";
            statement =conn.createStatement();
            rs=statement.executeQuery(query);

            while(rs.next()){
                Account account=new Account();
                account.setCustomerid(rs.getString("customerid"));
                account.setAccountNumber(rs.getString("accountnumber"));
                account.setBalance(rs.getDouble("balance"));
                account.setMaxWithdrawal(rs.getDouble("maxwithdrawallimit"));
                account.setMaxOnlineLimit(rs.getDouble("maxonlinelimit"));
                account.setMaxWirelessLimit(rs.getDouble("maxwirelesslimit"));
                account.setTotalCreditMonth(rs.getDouble("totalcreditedmonth"));
                account.setTotalDebitMonth(rs.getDouble("totaldebitedmonth"));
                accounts.add(account);
            }
        }
        catch (Exception e){
            System.out.println(e);
        }
        return accounts;
    }
    //////////////////////////////////// Rewards db functions ///////////////////////////////////

    public double insert_data_reward(Connection conn,String customerId,String type,Transaction currentTransaction,double totaldebitedmonth){
        UserData customer= get_customer_row_with_value(conn,"customerid",customerId);
        String accounts=customer.getAccounts();
        double current=currentTransaction.getCurrentTransaction();
        double rewardValue=0.0;
        switch (type){
            case "W":
            case "w":{
                rewardValue=current*(0.05);
                break;
            }
            case "O":
            case "o":{
                rewardValue=current*(0.1);
                break;
            }
            case "WL":
            case "wl":{
                rewardValue=current*(0.075);
            }
        }
        try{
            String insertQuery = "INSERT INTO rewards"+"(customerid, accounts, totaldebitedmonth, rewardtype, rewardvalue) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt= conn.prepareStatement(insertQuery);

                pstmt.setString(1, currentTransaction.getCustomerId());
                pstmt.setString(2,accounts);
                pstmt.setDouble(3,totaldebitedmonth);
                pstmt.setString(4,type+" reward");
                pstmt.setDouble(5,rewardValue);
                pstmt.executeUpdate();

            System.out.println("Data Entered Successfully");
        }catch (Exception e){
            System.out.println(e);
        }
        return rewardValue;
    }
    public void update_customers_rewards(Connection conn, String table_name,double rewards,String customerId){
        Statement statement;
        UserData current=get_customer_row_with_value(conn,"customerid",customerId);
        double currentReward=current.getReward();
        rewards+=currentReward;
        try{
            String insertQuery = "Update "+table_name+" set rewards = '"+rewards+"' where customerid = '"+customerId+"'";
            statement= conn.createStatement();
            statement.executeUpdate(insertQuery);
        }catch (Exception e){
            System.out.println(e);
        }
    }

    //////////////////////// Statements//////////////////////////////////////
    public void get_statement(Connection conn,String customerid){
        UserData customer = get_customer_row_with_value(conn,"customerid",customerid);
        List<Transaction> transactions=get_transactions_with_customerid(conn,customerid);
        List<Account> accounts = get_accounts_row_with_customerid(conn,customerid);
        String name= customer.getFirstName()+" "+customer.getLastName();
        String accountNumbers= customer.getAccounts();

        LocalDate now =LocalDate.now();
        LocalDate fromDate=now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.getMonth().length(now.isLeapYear()));

        double totalDebit=0.0;
        double totalCredit =0.0;

        double totalReward = 0.0;
        System.out.println("-------------------------------------------------------------------------------------");
        System.out.println("Customer ID : "+ customerid);
        System.out.println("Customer Name : "+name);
        System.out.println("Customer Accounts : "+accountNumbers);
        System.out.println("Transactions :");
        System.out.println("______________________________________________________________________________________");
        for(Transaction eachTransaction:transactions){
            if(eachTransaction.getTransactionDate().isAfter(fromDate) && eachTransaction.getTransactionDate().isBefore(endDate)){
            System.out.print(eachTransaction.getAccountNumber() + " | ");
            System.out.print(eachTransaction.getPreviousBalance() + " | ");
            System.out.print(eachTransaction.getCurrentTransaction() + " | ");
            String type =eachTransaction.getTransactionType();
                switch (type){
                    case "Credited":
                    case "credited": {
                        totalCredit+=eachTransaction.getCurrentTransaction();
                        break;
                    }
                    case "Debited - W":
                    case "Debited - w":
                    case "Debited - O":
                    case "Debited - o":
                    case "Debited - WL":
                    case "Debited - wl":
                    case "Transfer" :
                    {
                        totalDebit+=eachTransaction.getCurrentTransaction();
                        break;
                    }
                }
            System.out.print(eachTransaction.getTransactionType()+ " | ");
            System.out.print(eachTransaction.getNewBalance() + " | ");
            totalReward+=eachTransaction.getReward();
            System.out.println(eachTransaction.getReward());
            }
        }
        System.out.println("______________________________________________________________________________________");
        System.out.println("Total Reward : " +totalReward);
        System.out.println("Total Debited amount : "+ totalDebit);
        System.out.println("Total Credited amount : "+totalCredit);
        System.out.println("Amount Payable : "+(totalDebit-totalCredit));
        System.out.println("-------------------------------------------------------------------------------------");

    }
}

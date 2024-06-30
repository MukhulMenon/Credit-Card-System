import java.sql.Array;

public class UserData {
       private String customerId="";
       private String firstName="";
       private String lastName="";
       private String accounts;
       private double reward;
       
       //Getter's ans Setter's//
       public String getFirstName() {
           return firstName;
         }
       
         public void setFirstName(String firstName) {
           this.firstName = firstName;
         }
   
         public String getLastName() {
           return lastName;
         }
       
         public void setLastName(String lastName) {
           this.lastName = lastName;
         }

         public String getCustomerId() {
            return customerId;
         }

         public void setCustomerId(String customerId) {
            this.customerId = customerId;
         }

         public String getAccounts() {
             return accounts;
         }

         public void setAccounts(String accounts) {
            this.accounts = accounts;
         }


    public double getReward() {
        return reward;
    }

    public void setReward(double reward) {
        this.reward = reward;
    }
}

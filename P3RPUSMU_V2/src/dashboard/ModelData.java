package dashboard;

/**
 *
 * @author RAVEN
 */
public class ModelData {

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public double getAmount() {
        return Am;
    }

    public void setAmount(double Am) {
        this.Am = Am;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

/*    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }*/

    public ModelData(String month, double Am, double cost) {
        this.month = month;
        this.Am = Am;
        this.cost = cost;
    //    this.profit = profit;
    }

    public ModelData() {
    }

    private String month;
    private double Am;
    private double cost;
   // private double profit;
}
//, double profit
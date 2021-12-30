package local.ubms.com;

public class BillList {

    private String id,customer_id,bill_no,meter_no,period_from,period_to,bill_amount,bill_date,due_date,encoded_by,status,amount_tendered,balance,date_created;


    public BillList(String id,
                    String customer_id,
                    String bill_no,
                    String meter_no,
                    String period_from,
                    String period_to,
                    String bill_amount,
                    String bill_date,
                    String due_date,
                    String encoded_by,
                    String status,
                    String amount_tendered,
                    String balance,
                    String date_created) {

        this.id = id;
        this.customer_id = customer_id;
        this.bill_no = bill_no;
        this.meter_no = meter_no;
        this.period_from = period_from;
        this.period_to = period_to;
        this.bill_amount = bill_amount;
        this.bill_date = bill_date;
        this.due_date = due_date;
        this.encoded_by = encoded_by;
        this.status = status;
        this.amount_tendered = amount_tendered;
        this.balance = balance;
        this.date_created = date_created;




    }


    public String getId() {
        return id;

    }

    public String getCustomer_id() {
        return customer_id;

    }

    public String getBill_no() {
        return bill_no;

    }

    public String getMeter_no() {
        return meter_no;

    }
    public String getPeriod_from() {
        return period_from;

    }
    public String getPeriod_to() {
        return period_to;

    }
    public String getBill_amount() {
        return bill_amount;

    }
    public String getBill_date() {
        return bill_date;

    }
    public String getDue_date() {
        return due_date;

    }
    public String getEncoded_by() {
        return encoded_by;

    }public String getStatus() {
        return status;

    }public String getAmount_tendered() {
        return amount_tendered;

    }public String getBalance() {
        return balance;

    }public String getDate_created() {
        return date_created;

    }




}
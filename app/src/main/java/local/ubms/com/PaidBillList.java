package local.ubms.com;

public class PaidBillList {

    private String id,bill_id,customer_id,date_paid,amount_tendered,payment_method;


    public PaidBillList(String id,
                        String bill_id,
                        String customer_id,
                        String date_paid,
                        String amount_tendered,
                        String payment_method) {

        this.id = id;
        this.bill_id = bill_id;
        this.customer_id = customer_id;
        this.date_paid = date_paid;
        this.amount_tendered = amount_tendered;
        this.payment_method = payment_method;





    }


    public String getId() {
        return id;

    }
    public String getBill_id() {
        return bill_id;

    }

    public String getCustomer_id() {
        return customer_id;

    }

    public String getDate_paid() {
        return date_paid;

    }

    public String getAmount_tendered() {
        return amount_tendered;

    }
    public String getPayment_method() {
        return payment_method;

    }






}
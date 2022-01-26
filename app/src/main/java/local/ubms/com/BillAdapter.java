package local.ubms.com;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.ViewHolder> {
    private List<BillList> myListList;
    private Context ct;


    public BillAdapter(List<BillList> myListList, Context ct) {
        this.myListList = myListList;
        this.ct = ct;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.bill_rec,parent,false);
        return new ViewHolder(view);


    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BillList myList=myListList.get(position);

        holder.billAmount.setText("Amount: ₱"+myList.getBill_amount());
        holder.billDue.setText("Due date: "+myList.getDue_date());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ct, ViewBill.class);
                intent.putExtra("amount_tendered",myList.getAmount_tendered());
                intent.putExtra("bill_amount",myList.getBill_amount());
                intent.putExtra("balance",myList.getBalance());
                intent.putExtra("bill_date",myList.getBill_date());
                intent.putExtra("bill_no",myList.getBill_no());
                intent.putExtra("customer_id",myList.getCustomer_id());
                intent.putExtra("date_created",myList.getDate_created());
                intent.putExtra("due_date",myList.getDue_date());
                intent.putExtra("encoded_by",myList.getEncoded_by());
                intent.putExtra("id",myList.getId());
                intent.putExtra("meter_no",myList.getMeter_no());
                intent.putExtra("period_from",myList.getPeriod_from());
                intent.putExtra("period_to",myList.getPeriod_to());
                intent.putExtra("status",myList.getStatus());
                ct.startActivity(intent);

            }
        });





    }



    @Override
    public int getItemCount() {
        return myListList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView billAmount,billDue;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            billAmount = itemView.findViewById(R.id.billAmount);
            billDue = itemView.findViewById(R.id.billDue);







        }
    }
}
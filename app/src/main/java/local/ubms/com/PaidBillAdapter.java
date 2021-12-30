package local.ubms.com;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PaidBillAdapter extends RecyclerView.Adapter<PaidBillAdapter.ViewHolder> {
    private List<PaidBillList> myListList;
    private Context ct;


    public PaidBillAdapter(List<PaidBillList> myListList, Context ct) {
        this.myListList = myListList;
        this.ct = ct;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.paid_bill_rec,parent,false);
        return new ViewHolder(view);





    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PaidBillList myList=myListList.get(position);

        holder.paidBillAmount.setText("Amount: ₱"+myList.getAmount_tendered());
        holder.paidBillDate.setText("Date paid: "+myList.getDate_paid());
        holder.paidBillNumber.setText("Bill #"+myList.getBill_id());
        holder.paidBillMethodText.setText(myList.getPayment_method());

        if (myList.getPayment_method().matches("Paypal")){
            holder.paidBillImage.setImageResource(R.drawable.paypal);
        }else{
            holder.paidBillImage.setImageResource(R.drawable.walk);
        }



    }



    @Override
    public int getItemCount() {
        return myListList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        private TextView paidBillNumber,paidBillAmount,paidBillDate,paidBillMethodText;
        private ImageView paidBillImage;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            paidBillNumber = itemView.findViewById(R.id.paidBillNumber);
            paidBillAmount = itemView.findViewById(R.id.paidBillAmount);
            paidBillDate = itemView.findViewById(R.id.paidBillDate);
            paidBillMethodText = itemView.findViewById(R.id.paidBillMethodText);
            paidBillImage = itemView.findViewById(R.id.paidBillImage);





        }
    }
}
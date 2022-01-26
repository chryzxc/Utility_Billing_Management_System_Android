package local.ubms.com;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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

        String dateStr = myList.getDate_paid();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = df.parse(dateStr);
            df.setTimeZone(TimeZone.getDefault());

          //  String formattedDate = df.format(date);
          //  Toast.makeText(ct, date.toString(), Toast.LENGTH_SHORT).show();
            holder.paidBillDate.setText("Date paid: "+DateFormat.format("MM-dd-yyyy hh:mm:ss a",date));
        } catch (ParseException e) {
            Toast.makeText(ct, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }


        holder.paidBillAmount.setText("Amount: ₱"+myList.getAmount_tendered());
      //  holder.paidBillDate.setText("Date paid: "+myList.getDate_paid());
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
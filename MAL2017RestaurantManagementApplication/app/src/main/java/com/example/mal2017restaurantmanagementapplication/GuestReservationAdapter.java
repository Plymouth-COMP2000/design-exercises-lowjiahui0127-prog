package com.example.mal2017restaurantmanagementapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

public class GuestReservationAdapter extends RecyclerView.Adapter<GuestReservationAdapter.ViewHolder> {

    private Context context;
    private List<Reservation> reservations;
    private OnReservationClickListener listener;

    public interface OnReservationClickListener {
        void onEditClick(Reservation reservation);
        void onCancelClick(Reservation reservation);
    }

    public GuestReservationAdapter(Context context, List<Reservation> reservations) {
        this.context = context;
        this.reservations = reservations;
    }

    public void setOnReservationClickListener(OnReservationClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_booking_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reservation reservation = reservations.get(position);

        holder.tvBookingId.setText(reservation.getReservationNumber());
        holder.tvGuestName.setText(reservation.getGuestName());
        holder.tvGuestEmail.setText(reservation.getGuestEmail());
        holder.tvBookingDate.setText(reservation.getDate());
        holder.tvBookingTime.setText(reservation.getTime());
        holder.tvGuestCount.setText(reservation.getGuestCount() + " guests");

        if (reservation.getSpecialRequests() != null && !reservation.getSpecialRequests().isEmpty()) {
            holder.tvSpecialRequests.setVisibility(View.VISIBLE);
            holder.tvSpecialRequests.setText(reservation.getSpecialRequests());
        } else {
            holder.tvSpecialRequests.setVisibility(View.GONE);
        }

        holder.btnEdit.setOnClickListener(v -> {
            android.util.Log.d("ClickDebug", "Edit button clicked at position: " + position);
            android.util.Log.d("ClickDebug", "Reservation ID: " + reservation.getId());

            if (listener != null) {
                listener.onEditClick(reservation);
            } else {
                android.util.Log.e("ClickDebug", "ERROR: Listener is null!");
            }
        });

        holder.btnCancel.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCancelClick(reservation);
            }
        });

        // Set status text, color, and background
        String status = reservation.getStatus();
        holder.tvStatus.setText(status);

        int statusColor;
        int statusBgResId;
        switch (status.toLowerCase()) {
            case "pending":
                statusColor = context.getResources().getColor(R.color.status_pending);
                statusBgResId = R.drawable.status_pending_bg;
                break;
            case "confirmed":
                statusColor = context.getResources().getColor(R.color.status_confirmed);
                statusBgResId = R.drawable.status_confirmed_bg;
                break;
            case "cancelled":
                statusColor = context.getResources().getColor(R.color.status_cancelled);
                statusBgResId = R.drawable.status_cancelled_bg;
                break;
            default:
                statusColor = context.getResources().getColor(R.color.muted_text);
                statusBgResId = R.drawable.shape_chip;
        }
        holder.tvStatus.setTextColor(statusColor);
        holder.tvStatus.setBackgroundResource(statusBgResId);

        // Show/hide buttons based on status
        holder.guestButtons.setVisibility(View.VISIBLE);
        holder.staffButtons.setVisibility(View.GONE);

        if ("cancelled".equalsIgnoreCase(status)) {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);
        } else {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnCancel.setVisibility(View.VISIBLE);
        }

        // Set button click listeners
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(reservation);
            }
        });

        holder.btnCancel.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCancelClick(reservation);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookingId, tvStatus, tvGuestName, tvGuestEmail,
                tvBookingDate, tvBookingTime, tvGuestCount, tvSpecialRequests;
        ViewGroup guestButtons, staffButtons;
        MaterialButton btnEdit , btnCancel ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookingId = itemView.findViewById(R.id.tv_booking_id);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvGuestName = itemView.findViewById(R.id.tv_guest_name);
            tvGuestEmail = itemView.findViewById(R.id.tv_guest_email);
            tvBookingDate = itemView.findViewById(R.id.tv_booking_date);
            tvBookingTime = itemView.findViewById(R.id.tv_booking_time);
            tvGuestCount = itemView.findViewById(R.id.tv_guest_count);
            tvSpecialRequests = itemView.findViewById(R.id.tv_special_requests);

            guestButtons = itemView.findViewById(R.id.guest_buttons);
            staffButtons = itemView.findViewById(R.id.staff_buttons);

            btnEdit  = itemView.findViewById(R.id.btnEditBooking);
            btnCancel  = itemView.findViewById(R.id.btnCancelBooking);
        }
    }
}
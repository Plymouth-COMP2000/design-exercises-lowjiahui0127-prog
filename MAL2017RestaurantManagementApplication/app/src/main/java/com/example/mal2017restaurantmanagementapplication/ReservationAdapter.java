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

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ViewHolder> {

    private Context context;
    private List<Reservation> reservations;
    private OnReservationClickListener listener;
    private boolean isStaffView;

    public interface OnReservationClickListener {
        void onEditClick(Reservation reservation);
        void onCancelClick(Reservation reservation);
        void onDeleteClick(Reservation reservation);
        void onStatusChangeClick(Reservation reservation, String newStatus);
    }

    public ReservationAdapter(Context context, List<Reservation> reservations, boolean isStaffView) {
        this.context = context;
        this.reservations = reservations;
        this.isStaffView = isStaffView;
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

        holder.tvBookingId.setText("Booking " + reservation.getReservationNumber());
        holder.tvBookingDate.setText(reservation.getDate());
        holder.tvBookingTime.setText(reservation.getTime());
        holder.tvGuestCount.setText(reservation.getGuestCount() + " guests");

        if (reservation.getSpecialRequests() != null && !reservation.getSpecialRequests().isEmpty()) {
            holder.tvSpecialRequests.setVisibility(View.VISIBLE);
            holder.tvSpecialRequests.setText(reservation.getSpecialRequests());
        } else {
            holder.tvSpecialRequests.setVisibility(View.GONE);
        }

        // Set status text and color
        String status = reservation.getStatus();
        holder.tvStatus.setText(status);

        int statusColor;
        switch (status.toLowerCase()) {
            case "pending":
                statusColor = context.getResources().getColor(R.color.status_pending);
                break;
            case "confirmed":
                statusColor = context.getResources().getColor(R.color.status_confirmed);
                break;
            case "cancelled":
                statusColor = context.getResources().getColor(R.color.status_cancelled);
                break;
            default:
                statusColor = context.getResources().getColor(R.color.muted_text);
        }
        holder.tvStatus.setTextColor(statusColor);

        // Show/hide buttons based on user type and status
        if (isStaffView) {
            // Staff view: Show Approve/Reject buttons for pending reservations
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);

            if ("pending".equalsIgnoreCase(status)) {
                holder.btnApprove.setVisibility(View.VISIBLE);
                holder.btnReject.setVisibility(View.VISIBLE);
                holder.btnDelete.setVisibility(View.VISIBLE);
            } else {
                holder.btnApprove.setVisibility(View.GONE);
                holder.btnReject.setVisibility(View.GONE);
                holder.btnDelete.setVisibility(View.VISIBLE);
            }
        } else {
            // Guest view: Show Edit/Cancel buttons for pending/confirmed reservations
            holder.btnApprove.setVisibility(View.GONE);
            holder.btnReject.setVisibility(View.GONE);

            if ("cancelled".equalsIgnoreCase(status)) {
                holder.btnEdit.setVisibility(View.GONE);
                holder.btnCancel.setVisibility(View.GONE);
                holder.btnDelete.setVisibility(View.VISIBLE);
            } else {
                holder.btnEdit.setVisibility(View.VISIBLE);
                holder.btnCancel.setVisibility(View.VISIBLE);
                holder.btnDelete.setVisibility(View.GONE);
            }
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

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(reservation);
            }
        });

        holder.btnApprove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onStatusChangeClick(reservation, "confirmed");
            }
        });

        holder.btnReject.setOnClickListener(v -> {
            if (listener != null) {
                listener.onStatusChangeClick(reservation, "cancelled");
            }
        });
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookingId, tvBookingDate, tvBookingTime, tvGuestCount, tvSpecialRequests, tvStatus;
        MaterialButton btnEdit, btnCancel, btnDelete, btnApprove, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookingId = itemView.findViewById(R.id.tv_booking_id);
            tvBookingDate = itemView.findViewById(R.id.tv_booking_date);
            tvBookingTime = itemView.findViewById(R.id.tv_booking_time);
            tvGuestCount = itemView.findViewById(R.id.tv_guest_count);
            tvSpecialRequests = itemView.findViewById(R.id.tv_special_requests);
            tvStatus = itemView.findViewById(R.id.tv_status);

            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
package com.example.njupt_coursetable.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.njupt_coursetable.R;
import com.example.njupt_coursetable.data.model.Reminder;

/**
 * 提醒列表适配器
 * 用于在RecyclerView中显示提醒列表
 */
public class ReminderAdapter extends ListAdapter<Reminder, ReminderAdapter.ReminderViewHolder> {

    /**
     * 提醒点击监听器
     */
    private OnReminderClickListener onReminderClickListener;
    
    /**
     * 提醒开关状态改变监听器
     */
    private OnReminderSwitchChangeListener onReminderSwitchChangeListener;

    /**
     * 构造函数
     * @param onReminderClickListener 提醒点击监听器
     * @param onReminderSwitchChangeListener 提醒开关状态改变监听器
     */
    public ReminderAdapter(OnReminderClickListener onReminderClickListener, 
                          OnReminderSwitchChangeListener onReminderSwitchChangeListener) {
        super(DIFF_CALLBACK);
        this.onReminderClickListener = onReminderClickListener;
        this.onReminderSwitchChangeListener = onReminderSwitchChangeListener;
    }

    /**
     * DiffUtil回调，用于计算列表差异
     */
    private static final DiffUtil.ItemCallback<Reminder> DIFF_CALLBACK = new DiffUtil.ItemCallback<Reminder>() {
        @Override
        public boolean areItemsTheSame(@NonNull Reminder oldItem, @NonNull Reminder newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Reminder oldItem, @NonNull Reminder newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getDescription().equals(newItem.getDescription()) &&
                    oldItem.getRemindTime() == newItem.getRemindTime() &&
                    oldItem.isEnabled() == newItem.isEnabled() &&
                    oldItem.getAdvanceMinutes() == newItem.getAdvanceMinutes();
        }
    };

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reminder, parent, false);
        return new ReminderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminder currentReminder = getItem(position);
        holder.bind(currentReminder);
    }

    /**
     * 提醒视图持有者
     */
    class ReminderViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitle;
        private TextView textViewDescription;
        private TextView textViewTime;
        private TextView textViewAdvance;
        private Switch switchEnabled;

        /**
         * 构造函数
         * @param itemView 视图项
         */
        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_reminder_title);
            textViewDescription = itemView.findViewById(R.id.text_view_reminder_description);
            textViewTime = itemView.findViewById(R.id.text_view_reminder_time);
            textViewAdvance = itemView.findViewById(R.id.text_view_reminder_advance);
            switchEnabled = itemView.findViewById(R.id.switch_reminder_enabled);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (onReminderClickListener != null && position != RecyclerView.NO_POSITION) {
                    onReminderClickListener.onReminderClick(getItem(position));
                }
            });
        }

        /**
         * 绑定数据到视图
         * @param reminder 提醒对象
         */
        public void bind(Reminder reminder) {
            textViewTitle.setText(reminder.getTitle());
            textViewDescription.setText(reminder.getDescription());
            // 将时间戳转换为可读的时间格式
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
            String timeStr = sdf.format(new java.util.Date(reminder.getRemindTime()));
            textViewTime.setText(timeStr);
            textViewAdvance.setText("提前 " + reminder.getAdvanceMinutes() + " 分钟");
            switchEnabled.setChecked(reminder.isEnabled());

            switchEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int position = getAdapterPosition();
                    if (onReminderSwitchChangeListener != null && position != RecyclerView.NO_POSITION) {
                        onReminderSwitchChangeListener.onReminderSwitchChange(getItem(position), isChecked);
                    }
                }
            });
        }
    }

    /**
     * 提醒点击监听器接口
     */
    public interface OnReminderClickListener {
        /**
         * 提醒点击回调方法
         * @param reminder 被点击的提醒
         */
        void onReminderClick(Reminder reminder);
    }

    /**
     * 提醒开关状态改变监听器接口
     */
    public interface OnReminderSwitchChangeListener {
        /**
         * 提醒开关状态改变回调方法
         * @param reminder 提醒对象
         * @param isEnabled 是否启用
         */
        void onReminderSwitchChange(Reminder reminder, boolean isEnabled);
    }
}
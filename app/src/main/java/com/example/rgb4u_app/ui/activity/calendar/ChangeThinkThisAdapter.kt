import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.calendar.MyData
import com.example.rgb4u_app.ui.fragment.ChangeThinkBottomSheetDialogFragment

class ChangeThinkThisAdapter(
    private val items: List<MyData>,  // 데이터 리스트
    private val fragmentManager: FragmentManager  // BottomSheet 표시를 위한 FragmentManager
) : RecyclerView.Adapter<ChangeThinkThisAdapter.ChangeThinkThisViewHolder>() {

    inner class ChangeThinkThisViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImageView: ImageView = itemView.findViewById(R.id.TypeImageView)
        val nameTextView: TextView = itemView.findViewById(R.id.thinktypeTextView)
        val detailButtonLayout: View = itemView.findViewById(R.id.detailButton)
        val relativeLayoutContainer: LinearLayout = itemView.findViewById(R.id.relativeLayoutContainer)

        fun bind(item: MyData) {
            // 프로필 이미지와 이름 설정
            profileImageView.setImageResource(item.imageResId)
            nameTextView.text = item.name

            // '자세히 보기' 버튼 클릭 시 BottomSheet 호출
            detailButtonLayout.setOnClickListener {
                val bottomSheet = ChangeThinkBottomSheetDialogFragment.newInstance(
                    item.imageResId,
                    item.name,
                    item.description
                )
                bottomSheet.show(fragmentManager, bottomSheet.tag)
            }

            // 기존 뷰 초기화
            relativeLayoutContainer.removeAllViews()

            // 최대 3개의 Think 및 Change 메시지 추가
            for (i in 0 until minOf(item.thinkMessages.size, 3)) {
                val layout = LayoutInflater.from(itemView.context)
                    .inflate(R.layout.item_think_change, relativeLayoutContainer, false)

                // Think 및 Change 메시지 설정
                layout.findViewById<TextView>(R.id.ThinkTextView).text = item.thinkMessages[i]
                layout.findViewById<TextView>(R.id.ThinkExtraTextView).text = item.thinkExtraMessages[i]
                layout.findViewById<TextView>(R.id.ChangemessageTextView).text = item.changeMessages[i]
                layout.findViewById<TextView>(R.id.ChangeExtraTextView).text = item.changeExtraMessages[i]

                // Toggle 버튼 설정
                setupToggleButtons(layout)

                // 추가한 레이아웃을 컨테이너에 추가
                relativeLayoutContainer.addView(layout)
            }
        }

        private fun setupToggleButtons(layout: View) {
            val thimkToggleButton = layout.findViewById<ImageButton>(R.id.ThinktoggleButton)
            val changeToggleButton = layout.findViewById<ImageButton>(R.id.ChangetoggleButton)

            // 초기 상태 설정 (내용 숨김)
            layout.findViewById<TextView>(R.id.ThinkExtraTextView).visibility = View.GONE
            layout.findViewById<TextView>(R.id.ChangeExtraTextView).visibility = View.GONE

            // ThimktoggleButton 클릭 시 ThinkExtraTextView 토글
            thimkToggleButton.setOnClickListener {
                val thinkExtraTextView = layout.findViewById<TextView>(R.id.ThinkExtraTextView)
                if (thinkExtraTextView.visibility == View.VISIBLE) {
                    thinkExtraTextView.visibility = View.GONE
                    thimkToggleButton.setBackgroundResource(R.drawable.ic_toggle_down)
                } else {
                    thinkExtraTextView.visibility = View.VISIBLE
                    thimkToggleButton.setBackgroundResource(R.drawable.ic_toggle_up)
                }
            }

            // ChangetoggleButton 클릭 시 ChangeExtraTextView 토글
            changeToggleButton.setOnClickListener {
                val changeExtraTextView = layout.findViewById<TextView>(R.id.ChangeExtraTextView)
                if (changeExtraTextView.visibility == View.VISIBLE) {
                    changeExtraTextView.visibility = View.GONE
                    changeToggleButton.setBackgroundResource(R.drawable.ic_toggle_down)
                } else {
                    changeExtraTextView.visibility = View.VISIBLE
                    changeToggleButton.setBackgroundResource(R.drawable.ic_toggle_up)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChangeThinkThisViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_changethink_container, parent, false)
        return ChangeThinkThisViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChangeThinkThisViewHolder, position: Int) {
        holder.bind(items[position])

        // 마지막 항목인지 확인하여 구분선 보임/숨김 설정
        holder.itemView.findViewById<View>(R.id.dottedline).visibility =
            if (position == items.size - 1) View.GONE else View.VISIBLE
    }

    override fun getItemCount(): Int = items.size
}

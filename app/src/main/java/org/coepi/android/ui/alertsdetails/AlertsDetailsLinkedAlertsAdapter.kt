package org.coepi.android.ui.alertsdetails

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color.parseColor
import android.graphics.Paint
import android.graphics.Paint.Style.FILL
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater.from
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.coepi.android.databinding.ItemAlertDetailsLinkedAlertBinding
import org.coepi.android.databinding.ItemAlertDetailsLinkedAlertBinding.inflate
import org.coepi.android.system.ScreenUnitsConverter
import org.coepi.android.ui.alertsdetails.AlertsDetailsLinkedAlertsAdapter.ViewHolder
import org.coepi.android.ui.alertsdetails.LinkedAlertViewDataConnectionImage.Body
import org.coepi.android.ui.alertsdetails.LinkedAlertViewDataConnectionImage.Bottom
import org.coepi.android.ui.alertsdetails.LinkedAlertViewDataConnectionImage.Top

class AlertsDetailsLinkedAlertsAdapter(
    unitsConverter: ScreenUnitsConverter
) : ListAdapter<LinkedAlertViewData, ViewHolder>(AlertsDiffCallback()) {
    private val paths: LinedAlertsPaths = LinedAlertsPaths(unitsConverter)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val parent: ViewGroup,
        private val binding: ItemAlertDetailsLinkedAlertBinding =
            inflate(from(parent.context), parent, false)
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(viewData: LinkedAlertViewData): Unit = binding.run {
            this.viewData = viewData
            image.pathDrawer = when (viewData.image) {
                Top -> paths.top
                Body -> paths.center
                Bottom -> paths.bottom
            }
        }
    }
}

private class AlertsDiffCallback : ItemCallback<LinkedAlertViewData>() {
    override fun areItemsTheSame(oldItem: LinkedAlertViewData, newItem: LinkedAlertViewData)
            : Boolean = oldItem.alert.id == newItem.alert.id

    override fun areContentsTheSame(oldItem: LinkedAlertViewData, newItem: LinkedAlertViewData)
            : Boolean = oldItem == newItem
}

private class LinedAlertsPaths(private val unitsConverter: ScreenUnitsConverter) {
    private val linkedAlertsShapePaint = Paint(0).apply {
        style = FILL
        color = parseColor("#979797")
    }
    private val lineWidth: Int = unitsConverter.dpToPixel(3f).toInt()
    private val circleRadius: Int = unitsConverter.dpToPixel(5f).toInt()

    val top = TopPathDrawer(linkedAlertsShapePaint, lineWidth, circleRadius,
        unitsConverter.dpToPixel(18f).toInt())
    val center = CenterPathDrawer(linkedAlertsShapePaint, lineWidth)
    val bottom = BottomPathDrawer(linkedAlertsShapePaint, lineWidth, circleRadius,
        unitsConverter.dpToPixel(18f).toInt())
}

class LinkedAlertsPathView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    var pathDrawer: LinkedAlertsPathDrawer? = null
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        pathDrawer?.draw(canvas, width, height)
    }
}

interface LinkedAlertsPathDrawer {
    fun draw(canvas: Canvas, width: Int, height: Int)
    fun rect(width: Int, lineWidth: Int, top: Int, height: Int): Rect =
        Rect((width / 2) - (lineWidth / 2), top,
            (width / 2) - (lineWidth / 2) + lineWidth, height)
}

class TopPathDrawer(private val paint: Paint, private val lineWidth: Int,
                    private val circleRadius: Int,
                    private val topOffset: Int): LinkedAlertsPathDrawer {
    override fun draw(canvas: Canvas, width: Int, height: Int) {
        canvas.drawRect(rect(width, lineWidth, topOffset, height), paint)
        canvas.drawCircle(width / 2f, topOffset.toFloat(), circleRadius.toFloat(), paint)
    }
}

class CenterPathDrawer(private val paint: Paint,
                       private val lineWidth: Int): LinkedAlertsPathDrawer {
    override fun draw(canvas: Canvas, width: Int, height: Int) {
        canvas.drawRect(rect(width, lineWidth, 0, height), paint)
    }
}

class BottomPathDrawer(private val paint: Paint, private val lineWidth: Int,
                       private val circleRadius: Int, private val height: Int): LinkedAlertsPathDrawer {
    override fun draw(canvas: Canvas, width: Int, height: Int) {
        canvas.drawRect(rect(width, lineWidth, 0, this.height), paint)
        canvas.drawCircle(width / 2f, this.height.toFloat(), circleRadius.toFloat(), paint)
    }
}

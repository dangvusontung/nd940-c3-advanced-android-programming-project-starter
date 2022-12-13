package com.udacity

import android.animation.Animator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

private const val CIRCLE_ANIMATION = "CIRCLE_ANIMATION"
private const val ANIMATION_FILL = "ANIMATION_FILL"

private const val TAG = "LoadingButton"
class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

//    View & animation attribute
    private var widthSize = 0
    private var heightSize = 0
    private var textSize = 0F
    private var defaultBackgroundColor = 0
    private var loadingBackgroundColor = 0
    private var textColor = 0
    private var defaultTitle = ""
    private var loadingTitle = ""
    private var loadingColor = 0
    private var isLoading = false
    private var loadingWidth = 0F
    private var loadingCircleAngle = 0F
    private val cornerRadius = resources.getDimension(R.dimen.corner_radius)

//     Animator
    private val valueAnimator = ValueAnimator()

//    Paint object
    private val paint = Paint().apply {
        isAntiAlias = true
        strokeWidth = resources.getDimension(R.dimen.stroke_width)
        textSize = resources.getDimension(R.dimen.default_text_size)
    }

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

    }

    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            defaultBackgroundColor = getColor(R.styleable.LoadingButton_standardBackgroundColor, 0)
            loadingBackgroundColor = getColor(R.styleable.LoadingButton_activeBackgroundColor, 0)
            textColor = getColor(R.styleable.LoadingButton_textColor, 0)
            loadingColor = getColor(R.styleable.LoadingButton_loadingColor, 0)
            defaultTitle = getString(R.styleable.LoadingButton_standardTitle) ?: ""
            loadingTitle = getString(R.styleable.LoadingButton_activeTitle) ?: ""
            textSize = getDimension(R.styleable.LoadingButton_textSize, 0F)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawAnimation(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    override fun performClick(): Boolean {
        super.performClick()
        buttonState = ButtonState.Clicked
        startAnimation()
        return true
    }

    private fun drawAnimation(canvas: Canvas) {
        Log.d(TAG, "drawAnimation: ")
        canvas.save()
        canvas.clipRect(
            0F, 0F,
            widthSize.toFloat(), heightSize.toFloat()
        )
        canvas.drawColor(defaultBackgroundColor)
        paint.color = loadingBackgroundColor
        canvas.drawRect(0F, 0F, loadingWidth, heightSize.toFloat(), paint)
        paint.apply {
            color = textColor
            textAlign = Paint.Align.CENTER
        }

        val title = if (isLoading) loadingTitle else defaultTitle
        val rectBoundText = Rect()
        paint.getTextBounds(title, 0, title.length, rectBoundText)
        val xTitle = widthSize.toFloat() / 2
        val yTitle = heightSize.toFloat() / 2 + rectBoundText.height().toFloat() / 2
        canvas.drawText(title, xTitle, yTitle, paint)

        if (isLoading) {
            paint.color = loadingColor
            val left = xTitle + rectBoundText.width() / 2
            val top = heightSize.toFloat() / 2 - cornerRadius / 4
            val rectBoundCircle = RectF(left, top, left + cornerRadius, top + cornerRadius)
            canvas.drawArc(rectBoundCircle, 0F, loadingCircleAngle, true, paint)
        }

        canvas.restore()
    }

    private fun startAnimation() {
        val circleAnimationPropertyValuesHolder = PropertyValuesHolder.ofFloat(CIRCLE_ANIMATION, 0F, 360F)
        val fillAnimationPropertyValuesHolder =
            PropertyValuesHolder.ofFloat(ANIMATION_FILL, 0f, widthSize.toFloat())

        valueAnimator.setValues(circleAnimationPropertyValuesHolder, fillAnimationPropertyValuesHolder)
        valueAnimator.duration = 5000

        valueAnimator.addUpdateListener { animator ->
            loadingCircleAngle = animator.getAnimatedValue(CIRCLE_ANIMATION) as Float
            loadingWidth = animator.getAnimatedValue(ANIMATION_FILL) as Float
            invalidate()
        }

        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {
                buttonState = ButtonState.Loading
                isLoading = true
            }

            override fun onAnimationEnd(p0: Animator?) {
                buttonState = ButtonState.Completed
                isLoading = false

            }

            override fun onAnimationCancel(p0: Animator?) {
                buttonState = ButtonState.Completed
                isLoading = false
            }

            override fun onAnimationRepeat(p0: Animator?) {
            }
        })

        valueAnimator.start()
    }
}